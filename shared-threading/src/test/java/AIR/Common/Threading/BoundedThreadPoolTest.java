/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Threading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import AIR.Common.time.TimeSpan;

public class BoundedThreadPoolTest
{

  private static Logger        _logger         = LoggerFactory.getLogger (BoundedThreadPoolTest.class);
  private BoundedThreadPool    POOL_UNDER_TEST = null;
  private SimpleErrorCollector ERROR_COLLECTOR = null;

  @Before
  public void setUp () {
    POOL_UNDER_TEST = new BoundedThreadPool (5, "TEST_THREAD_POOL", 10, 5, true);
    ERROR_COLLECTOR = new SimpleErrorCollector ();
    POOL_UNDER_TEST.setExceptionHandler (ERROR_COLLECTOR);
  }

  @After
  public void tearDown () {
    if (POOL_UNDER_TEST != null) {
      POOL_UNDER_TEST.shutdown (true);
    }
  }

  @Test
  public void createThreadPoolTest () {
    SimpleTask task = new SimpleTask ();
    POOL_UNDER_TEST.Enqueue (task);
    assertTrue ("Task did not run in 1 second.", task.waitForRun ());
    assertTrue ("Errors occurred", ERROR_COLLECTOR.getExceptions ().size () == 0);
    assertEquals ("Pool reports wrong number of threads", 5, POOL_UNDER_TEST.getThreadCount ());
  }

  @Test
  public void queueMaxTest () {
    List<SlowTask> tasks = fillPool ();
    assertFalse ("Task ran, but it should not have been queued.", tasks.get (15).waitForRun (new TimeSpan (1250, TimeUnit.MILLISECONDS)));
    for (int i = 0; i < 15; i++) {
      assertTrue ("Queued task did not run.", tasks.get (i).waitForRun (new TimeSpan (3250, TimeUnit.MILLISECONDS)));
    }
    assertTrue ("Errors occurred", ERROR_COLLECTOR.getExceptions ().size () == 0);
    assertEquals ("Pool reports wrong number of threads", 5, POOL_UNDER_TEST.getThreadCount ());
  }

  @Test
  public void queueRecoveryTest () throws InterruptedException {
    List<SlowTask> tasks = fillPool ();

    while (POOL_UNDER_TEST.getCount () >= 5) {
      Thread.sleep (100);
    }

    // After letting some threads close, we should be able to enqueue a new
    // thread.
    assertTrue ("Unable to queue task after pool has emptied", POOL_UNDER_TEST.Enqueue (tasks.get (15)));
    for (int i = 0; i < 16; i++) {
      assertTrue ("Queued task did not run.", tasks.get (i).waitForRun (new TimeSpan (3250, TimeUnit.MILLISECONDS)));
    }
    assertTrue ("Errors occurred", ERROR_COLLECTOR.getExceptions ().size () == 0);
    assertEquals ("Pool reports wrong number of threads", 5, POOL_UNDER_TEST.getThreadCount ());
  }

  @Test
  public void testGetNumberOfProcessors () {
    assertTrue (BoundedThreadPool.getNumberOfProcessors () >= 1);
  }

  @Test
  public void getCountTest () {
    fillPool ();
    assertEquals (POOL_UNDER_TEST.getCount (), 10);
  }

  @Test
  public void getThreadCountTest () {
    assertEquals (POOL_UNDER_TEST.getThreadCount (), 5);
  }

  @Test
  public void exceptionHandlerTest () throws InterruptedException {
    final String message = "I am throwing this error to see if it is caught!";
    POOL_UNDER_TEST.Enqueue (new AbstractTask ()
    {

      @Override
      public void Execute () {
        throw new RuntimeException (message);
      }
    });
    synchronized (ERROR_COLLECTOR) {
      List<Throwable> exceptions = ERROR_COLLECTOR.getExceptions ();
      if (exceptions.size () < 1) {
        ERROR_COLLECTOR.wait (1_000);
      }
      assertEquals (1, exceptions.size ());
      assertTrue (exceptions.get (0) instanceof RuntimeException);
      assertEquals (message, exceptions.get (0).getMessage ());
    }
  }

  @Test
  public void isShutdownTest () {
    assertFalse (POOL_UNDER_TEST.isShutdown ());
    POOL_UNDER_TEST.shutdown (true);
    assertTrue (POOL_UNDER_TEST.isShutdown ());
    POOL_UNDER_TEST = null;
  }

  @Test
  public void statsTest () {
    IThreadPoolStats beforeStats = POOL_UNDER_TEST.getStats ();
    assertTrue (beforeStats.getAveQDelay () == null);
    assertTrue (beforeStats.getAveTaskExecutionTime () == null);
    assertEquals (5, beforeStats.getCurrentThreadCount ());
    assertEquals (TimeSpan.MIN_VALUE, beforeStats.getMaxQDelay ());
    assertEquals (TimeSpan.MAX_VALUE, beforeStats.getMinQDelay ());
    assertEquals (TimeSpan.MIN_VALUE, beforeStats.getMaxTaskExecutionTime ());
    assertEquals (TimeSpan.MAX_VALUE, beforeStats.getMinTaskExecutionTime ());
    assertEquals (0, beforeStats.getTasksExecuted ());
    assertEquals (0, beforeStats.getTasksInQCount ());

    List<SlowTask> tasks = fillPool ();

    IThreadPoolStats duringStats = POOL_UNDER_TEST.getStats ();

    // At this point, 5 tasks have been submitted for execution and 10 are still
    // in the queue.
    // Note that this DOES NOT MEAN that five tasks have _completed_ execution.
    // The name of the
    // property is a bit misleading
    assertEquals (5, duringStats.getTasksExecuted ());
    assertEquals (10, duringStats.getTasksInQCount ());

    // Wait for all of the tasks to be finished running. Should take less than
    // three seconds.
    for (int i = 0; i < 15; i++) {
      assertTrue ("Queued task did not run.", tasks.get (i).waitForRun (new TimeSpan (3250, TimeUnit.MILLISECONDS)));
    }
    IThreadPoolStats afterStats = POOL_UNDER_TEST.getStats ();

    assertEquals (5, afterStats.getCurrentThreadCount ());

    // Five tasks will execute immediately.
    // Five tasks will be delayed almost 750 ms.
    // Five tasks will be delayed almost 1750 ms.
    assertTrue (afterStats.getMinQDelay ().getDuration (TimeUnit.MILLISECONDS) >= 0);
    assertTrue (afterStats.getMinQDelay ().getDuration (TimeUnit.MILLISECONDS) <= 50);
    assertTrue (afterStats.getMaxQDelay ().getDuration (TimeUnit.MILLISECONDS) >= 1700);
    assertTrue (afterStats.getMaxQDelay ().getDuration (TimeUnit.MILLISECONDS) <= 1800);
    assertTrue (afterStats.getAveQDelay ().getDuration (TimeUnit.MILLISECONDS) >= 783);
    assertTrue (afterStats.getAveQDelay ().getDuration (TimeUnit.MILLISECONDS) <= 883);

    // Make sure min, ave and max are correctly ordered
    assertTrue (afterStats.getMinQDelay ().compareTo (afterStats.getAveQDelay ()) <= 0);
    assertTrue (afterStats.getAveQDelay ().compareTo (afterStats.getMaxQDelay ()) <= 0);

    // Tasks are designed to execute for one second
    _logger.debug (afterStats.getMinTaskExecutionTime ().toString ());
    _logger.debug (afterStats.getMaxTaskExecutionTime ().toString ());
    _logger.debug (afterStats.getAveTaskExecutionTime ().toString ());
    assertTrue (afterStats.getMinTaskExecutionTime ().getDuration (TimeUnit.MILLISECONDS) >= 975);
    assertTrue (afterStats.getMaxTaskExecutionTime ().getDuration (TimeUnit.MILLISECONDS) >= 975);
    assertTrue (afterStats.getAveTaskExecutionTime ().getDuration (TimeUnit.MILLISECONDS) >= 975);
    assertTrue (afterStats.getMinTaskExecutionTime ().getDuration (TimeUnit.MILLISECONDS) <= 1025);
    assertTrue (afterStats.getMaxTaskExecutionTime ().getDuration (TimeUnit.MILLISECONDS) <= 1025);
    assertTrue (afterStats.getAveTaskExecutionTime ().getDuration (TimeUnit.MILLISECONDS) <= 1025);

    // Make sure min, ave and max are correctly ordered
    assertTrue (afterStats.getMinTaskExecutionTime ().compareTo (afterStats.getAveTaskExecutionTime ()) <= 0);
    assertTrue (afterStats.getAveTaskExecutionTime ().compareTo (afterStats.getMaxTaskExecutionTime ()) <= 0);

    assertEquals (15, afterStats.getTasksExecuted ());
    assertEquals (0, afterStats.getTasksInQCount ());

    assertTrue ("Errors occurred", ERROR_COLLECTOR.getExceptions ().size () == 0);
  }

  /**
   * Fill the pool with fifteen tasks, each of which takes one second to
   * execute.
   * 
   * @return A list of 25 1-second tasks. The first fifteen have been queued,
   *         the rest are "spares"
   */
  private synchronized List<SlowTask> fillPool () {
    List<SlowTask> tasks;
    tasks = new ArrayList<SlowTask> (25);
    int i;
    for (i = 0; i < 25; i++) {
      SlowTask task = new SlowTask (new TimeSpan (1, TimeUnit.SECONDS));
      task.setName (String.format ("SLOW TASK %d", i + 1));
      tasks.add (task);
    }

    // Enqueue enough tasks to occupy all of the threads, then wait a few
    // milliseconds for them to be dispatched
    // to the worker threads
    for (i = 0; i < 5; i++) {
      assertTrue ("Queue was full prematurely", POOL_UNDER_TEST.Enqueue (tasks.get (i)));
    }
    try {
      wait (250);
    } catch (InterruptedException e) {
    }

    // Now try to enqueue the remaining tasks. Enqueue should return false when
    // the queue is full (task 15)
    for (i = 5; i < 25; i++) {
      boolean succeed = POOL_UNDER_TEST.Enqueue (tasks.get (i));
      // _logger.debug ( String.format( " ** %d tasks are queued",
      // POOL_UNDER_TEST.getCount () ) );
      if (!succeed)
        break;
    }
    assertEquals ("Queue did not fill when it should have", 15, i);
    assertEquals ("Wrong number of tasks in backlog", 10, POOL_UNDER_TEST.getCount ());
    assertEquals ("Wrong number of tasks in active threads", 5, POOL_UNDER_TEST.getActiveCount ());
    return tasks;
  }

  private class SimpleTask extends AbstractTask
  {

    private volatile boolean _hasRun = false;

    @Override
    synchronized public void Execute () {
      _hasRun = true;
      this.notifyAll ();
    }

    synchronized public boolean waitForRun () {
      if (!_hasRun) {
        try {
          this.wait (1000L);
        } catch (InterruptedException e) {
          // No action necessary
        }
      }
      return _hasRun;
    }

  }

  private class SlowTask extends AbstractTask
  {
    private volatile boolean _hasRun    = false;
    private final TimeSpan   _runTime;
    private volatile String  _name;
    private final Object     _name_lock = new Object ();

    public SlowTask (TimeSpan runTime) {
      this._runTime = runTime;
    }

    @Override
    synchronized public void Execute () {
      try {
        // _logger.debug( String.format( "Thread %s entering wait", getName() )
        // );
        Thread.sleep (_runTime.getDuration (TimeUnit.MILLISECONDS));
        _hasRun = true;
        // _logger.debug( String.format(
        // "Thread %s completed wait for %d millisecondes", getName(),
        // _runTime.getDuration( TimeUnit.MILLISECONDS ) ) );
      } catch (InterruptedException e) {
        // _logger.debug( String.format( "Thread %s interrupted", getName() ) );
        // If we are interrupted, then the run was not completed.
      } finally {
        this.notifyAll ();
        // _logger.debug( String.format( "Thread %s exiting", getName() ) );
      }
    }

    synchronized public boolean waitForRun (TimeSpan waitTime) {
      if (!_hasRun) {
        try {
          this.wait (waitTime.getDuration (TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
          // No action necessary
        }
      }
      return _hasRun;
    }

    @SuppressWarnings ("unused")
    public String getName () {
      synchronized (_name_lock) {
        return _name;
      }
    }

    public void setName (String value) {
      synchronized (_name_lock) {
        _name = value;
      }
    }
  }

  private class SimpleErrorCollector implements IThreadPoolExceptionHandler
  {

    List<Throwable> _exceptions = new ArrayList<Throwable> ();

    @Override
    synchronized public void onException (ITask task, Throwable t) {
      _exceptions.add (t);
      notifyAll ();
    }

    public List<Throwable> getExceptions () {
      return _exceptions;
    }

  }

}
