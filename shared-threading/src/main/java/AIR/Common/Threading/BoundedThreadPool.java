/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Threading;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundedThreadPool implements IThreadPool
{

  private static int                           _numberOfProcessors                 = Runtime.getRuntime ().availableProcessors ();
  private static final Logger                  _logger                             = LoggerFactory.getLogger (BoundedThreadPool.class);

  private final BlockingQueue<Runnable>        _taskQueue;
  private final ThreadPoolExecutor             _workerThreadPool;
  private final int                            _taskQHighWaterMark;
  private final int                            _taskQLowWaterMark;
  private final IThreadPoolStatsRecorder        _statsRecorder;
  private final String                         _name;
  private final Object                         _statusLock                         = new Object ();

  private volatile boolean                     _suspendQUntilLowWaterMarkisReached = false;
  private volatile ThreadPoolStatus            _status                             = ThreadPoolStatus.Uninitialized;
  private volatile int                         _lastThreadNumber                   = 0;
  private volatile IThreadPoolExceptionHandler _exceptionHandler                   = null;

  public BoundedThreadPool (int threadCount, String threadPoolName, int highWaterMark, int lowWaterMark) {
    this (threadCount, threadPoolName, highWaterMark, lowWaterMark, true);
  }

  public BoundedThreadPool (int threadCount, String threadPoolName, int highWaterMark, int lowWaterMark, boolean captureStats) {

    this (threadCount, threadPoolName, highWaterMark, lowWaterMark,
        captureStats ? new ThreadPoolStatsRecorder () : null);

  }

  public BoundedThreadPool (int threadCount, String threadPoolName, int highWaterMark, int lowWaterMark, IThreadPoolStatsRecorder statsRecorder) {
    if (threadCount <= 0)
    {
      threadCount = Math.max (_numberOfProcessors, 2);
    }
    _name = StringUtils.defaultString (threadPoolName);
    _taskQHighWaterMark = highWaterMark <= 0 ? Integer.MAX_VALUE : highWaterMark;
    _taskQLowWaterMark = lowWaterMark <= 0 ? highWaterMark : lowWaterMark;
    if (lowWaterMark > highWaterMark)
    {
      throw new IllegalArgumentException ("The low watermark cannot be larger than the high watermark");
    }

    if ( statsRecorder != null ) {
      statsRecorder.setThreadPool ( this );
    }
    _statsRecorder = statsRecorder;
    _taskQueue = new ArrayBlockingQueue<> (_taskQHighWaterMark, true);
    ThreadFactory threadFactory = new NamedThreadFactory ();
    _workerThreadPool = new ThreadPoolExecutor (threadCount, threadCount, 0, TimeUnit.NANOSECONDS, _taskQueue, threadFactory);
    synchronized (_statusLock) {
      _workerThreadPool.prestartAllCoreThreads ();
      _status = ThreadPoolStatus.Active;
    }
  }

  public static int getNumberOfProcessors () {
    return _numberOfProcessors;
  }

  public int getCount () {
    return _taskQueue.size ();
  }

  public int getThreadCount () {
    return _workerThreadPool.getPoolSize ();
  }

  public int getActiveCount () {
    return _workerThreadPool.getActiveCount ();
  }

  public IThreadPoolExceptionHandler getExceptionHandler () {
    return _exceptionHandler;
  }

  public void setExceptionHandler (IThreadPoolExceptionHandler value) {
    _exceptionHandler = value;
  }

  public boolean isShutdown () {
    synchronized (_statusLock) {
      return (_status == ThreadPoolStatus.ShuttingDown || _status == ThreadPoolStatus.Closed);
    }
  }

  @Override
  public synchronized boolean Enqueue (ITask task) {

    // We aren't worrying about synchronizing the status read, because reads of
    // volatile properties are atomic by contract
    if (_status != ThreadPoolStatus.Active) {
      return false;
    }

    if (_suspendQUntilLowWaterMarkisReached) {
      if (getCount () >= _taskQLowWaterMark) {
        return false;
      }
      _suspendQUntilLowWaterMarkisReached = false;
    }
    if (_statsRecorder != null) {
      _statsRecorder.recordTaskQueued (task);
    }
    TaskRunner runner = new TaskRunner (task);
    try {
      _workerThreadPool.submit (runner);
    } catch (RejectedExecutionException e) {
      _suspendQUntilLowWaterMarkisReached = true;
      if (_statsRecorder != null) {
        _statsRecorder.recordTaskRejected (task);
      }
      return false;
    }
    return true;
  }

  @Override
  public IThreadPoolStats getStats () {
    if (_statsRecorder != null) {
      return _statsRecorder.getStats ();
    }
    return null;
  }

  public void shutdown (boolean wait) {
    synchronized (_statusLock) {
      if (this._status != ThreadPoolStatus.Active)
        return;
      _status = ThreadPoolStatus.ShuttingDown;
    }
    _logger.info (String.format (String.format ("BoundedThreadPool \"%s\" Shutting Down", _name)));

    if (wait) {
      _workerThreadPool.shutdown ();
    }
    else {
      _workerThreadPool.shutdownNow ();
    }

    // There _really_ should not be anything else that can be changing the
    // status variable at this point, so
    // we don't bother synchronizing.
    _status = ThreadPoolStatus.Closed;
    _logger.info (String.format (String.format ("BoundedThreadPool \"%s\" Shut Down", _name)));
  }

  public void finalize () {
    shutdown (false);
  }

  private class TaskRunner implements Runnable
  {

    private final ITask _task;

    public TaskRunner (ITask task) {
      _task = task;
    }

    @Override
    public void run () {
      if (_statsRecorder != null) {
        _statsRecorder.recordTaskDequeued (_task);
      }
      try {
        _task.Execute ();
      } catch (Throwable t) {
        if (_exceptionHandler != null) {
          try {
            _exceptionHandler.onException (_task, t);
          } catch (Throwable t2) {
            _logger.error ("Error was thrown in worker thread's registered exception handler", t2);
            _logger.error (String.format (
                "A worker thread's registered exception handler failed while handling the following error\n    \"%s\"", t.getMessage ()), t);
          }
        }
      } finally {
        if (_statsRecorder != null) {
          _statsRecorder.recordTaskExecuted (_task);
        }
      }
    }

  }

  private class NamedThreadFactory implements ThreadFactory
  {

    @Override
    public Thread newThread (Runnable r) {
      Thread thread = new Thread (r);
      thread.setDaemon (true);
      String thread_name = "";
      if (!StringUtils.isEmpty (_name)) {
        thread_name = _name + "#" + Integer.toString (_lastThreadNumber);
        _lastThreadNumber++;
      }

      thread.setName (thread_name);

      return thread;
    }

  }

}
