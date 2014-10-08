/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Threading;

import java.util.concurrent.TimeUnit;

import AIR.Common.time.TimeSpan;
import AIR.Common.time.TimeUtils;

public class ThreadPoolStatsRecorder implements IThreadPoolStatsRecorder
{
  private volatile long        tasksQueued           = 0;
  private volatile long        tasksDeQueued         = 0;
  private volatile long        tasksExecuted         = 0;

  // Number of tasks being queued/dequeued per second
  // float aveTaskArrivalRate;
  // float aveTaskServiceRate;

  // Time task sits in queue
  private volatile TimeSpan    minTaskQueueingDelay  = TimeSpan.MAX_VALUE;
  private volatile Long        totTaskQueueingNanos  = 0L;
  private volatile TimeSpan    maxTaskQueueingDelay  = TimeSpan.MIN_VALUE;

  // Time task takes to execute
  private volatile TimeSpan    minTaskExecutionDelay = TimeSpan.MAX_VALUE;
  private volatile Long        totTaskExecutionNanos = 0L;
  private volatile TimeSpan    maxTaskExecutionDelay = TimeSpan.MIN_VALUE;

  // The owning ThreadPool
  private volatile IThreadPool threadPool            = null;

  /* (non-Javadoc)
   * @see AIR.Common.Threading.IThreadPoolStatsRecorder#getThreadPool()
   */
  @Override
  public IThreadPool getThreadPool () {
    return threadPool;
  }

  /* (non-Javadoc)
   * @see AIR.Common.Threading.IThreadPoolStatsRecorder#setThreadPool(AIR.Common.Threading.IThreadPool)
   */
  @Override
  public void setThreadPool (IThreadPool threadPool) {
    this.threadPool = threadPool;
  }

  // In order to avoid a race condition, we must call recordTaskQueued()
  // before we
  // actually know whether the queue will accept the task. We therefore have
  // added
  // recordTaskRejected() to undo what was done by recordTaskQueued();
  /* (non-Javadoc)
   * @see AIR.Common.Threading.IThreadPoolStatsRecorder#recordTaskQueued(AIR.Common.Threading.ITask)
   */
  @Override
  public synchronized void recordTaskQueued (ITask task) {
    task.setTimeEnqueued (TimeUtils.nowTimestamp ());
    tasksQueued++;
  }

  /* (non-Javadoc)
   * @see AIR.Common.Threading.IThreadPoolStatsRecorder#recordTaskRejected(AIR.Common.Threading.ITask)
   */
  @Override
  public synchronized void recordTaskRejected (ITask task) {
    task.setTimeEnqueued (null);
    tasksQueued--;
  }

  /* (non-Javadoc)
   * @see AIR.Common.Threading.IThreadPoolStatsRecorder#recordTaskDequeued(AIR.Common.Threading.ITask)
   */
  @Override
  public synchronized void recordTaskDequeued (ITask task) {
    task.setTimeDequeued (TimeUtils.nowTimestamp ());
    tasksDeQueued++;
    TimeSpan taskinQTime = new TimeSpan (task.getTimeEnqueued (), task.getTimeDequeued ());
    minTaskQueueingDelay = TimeSpan.min (taskinQTime, minTaskQueueingDelay);
    maxTaskQueueingDelay = TimeSpan.max (taskinQTime, maxTaskQueueingDelay);
    totTaskQueueingNanos += taskinQTime.getDuration (TimeUnit.NANOSECONDS);
  }

  /* (non-Javadoc)
   * @see AIR.Common.Threading.IThreadPoolStatsRecorder#recordTaskExecuted(AIR.Common.Threading.ITask)
   */
  @Override
  public synchronized void recordTaskExecuted (ITask task) {
    task.setExecutionTime (new TimeSpan (task.getTimeDequeued (), TimeUtils.nowTimestamp ()));
    tasksExecuted++;
    minTaskExecutionDelay = TimeSpan.min (task.getExecutionTime (), minTaskExecutionDelay);
    maxTaskExecutionDelay = TimeSpan.max (task.getExecutionTime (), maxTaskExecutionDelay);
    totTaskExecutionNanos += task.getExecutionTime ().getDuration (TimeUnit.NANOSECONDS);
  }

  /* (non-Javadoc)
   * @see AIR.Common.Threading.IThreadPoolStatsRecorder#getStats()
   */
  @Override
  public synchronized IThreadPoolStats getStats () {
    TimeSpan aveTaskQueuingDelay = null;
    if (tasksDeQueued > 0) {
      aveTaskQueuingDelay = new TimeSpan (totTaskQueueingNanos / tasksDeQueued, TimeUnit.NANOSECONDS);
    }
    TimeSpan aveTaskExecutionTime = null;
    if (tasksExecuted > 0) {
      aveTaskExecutionTime = new TimeSpan (totTaskExecutionNanos / tasksExecuted, TimeUnit.NANOSECONDS);
    }
    return new ThreadPoolStats (tasksQueued, tasksDeQueued, getThreadPool ().getThreadCount (),
        minTaskQueueingDelay, aveTaskQueuingDelay, maxTaskQueueingDelay,
        minTaskExecutionDelay, aveTaskExecutionTime, maxTaskExecutionDelay);
  }

}
