/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Threading;

import AIR.Common.time.TimeSpan;

public class ThreadPoolStats implements IThreadPoolStats
{
  private long     _tasksQueued;
  private long     _tasksDeQueued;
  private int      _currentThreadCount;

  // Number of seconds task sits in queue
  private TimeSpan _minTaskQueueingDelay;
  private TimeSpan _aveTaskQueueingDelay;
  private TimeSpan _maxTaskQueueingDelay;

  // Number of seconds task takes to execute
  private TimeSpan _minTaskExecutionTime;
  private TimeSpan _aveTaskExecutionTime;
  private TimeSpan _maxTaskExecutionTime;

  public ThreadPoolStats (
      long tasksQueued,
      long tasksDeQueued,
      int threadCount,
      TimeSpan minTaskQDelay,
      TimeSpan aveTaskQDelay,
      TimeSpan maxTaskQDelay,
      TimeSpan minTaskExecutionTime,
      TimeSpan aveTaskExecutionTime,
      TimeSpan maxTaskExecutionTime) {
    _tasksQueued = tasksQueued;
    _tasksDeQueued = tasksDeQueued;
    _currentThreadCount = threadCount;
    _minTaskQueueingDelay = minTaskQDelay;
    _maxTaskQueueingDelay = maxTaskQDelay;
    _aveTaskQueueingDelay = aveTaskQDelay;
    _minTaskExecutionTime = minTaskExecutionTime;
    _maxTaskExecutionTime = maxTaskExecutionTime;
    _aveTaskExecutionTime = aveTaskExecutionTime;

  }

  @Override
  public long getTasksExecuted () {
    return _tasksDeQueued;
  }

  @Override
  public long getTasksInQCount () {
    return _tasksQueued - _tasksDeQueued;
  }

  @Override
  public long getCurrentThreadCount () {
    return _currentThreadCount;
  }

  @Override
  public TimeSpan getMinQDelay () {
    return _minTaskQueueingDelay;
  }

  @Override
  public TimeSpan getAveQDelay () {
    return _aveTaskQueueingDelay;
  }

  @Override
  public TimeSpan getMaxQDelay () {
    return _maxTaskQueueingDelay;
  }

  @Override
  public TimeSpan getMinTaskExecutionTime () {
    return _minTaskExecutionTime;
  }

  @Override
  public TimeSpan getAveTaskExecutionTime () {
    return _aveTaskExecutionTime;
  }

  @Override
  public TimeSpan getMaxTaskExecutionTime () {
    return _maxTaskExecutionTime;
  }

}
