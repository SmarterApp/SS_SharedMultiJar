/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Threading;

public interface IThreadPoolStatsRecorder
{

  public abstract IThreadPool getThreadPool ();

  public abstract void setThreadPool (IThreadPool threadPool);

  // In order to avoid a race condition, we must call recordTaskQueued()
  // before we
  // actually know whether the queue will accept the task. We therefore have
  // added
  // recordTaskRejected() to undo what was done by recordTaskQueued();
  public abstract void recordTaskQueued (ITask task);

  public abstract void recordTaskRejected (ITask task);

  public abstract void recordTaskDequeued (ITask task);

  public abstract void recordTaskExecuted (ITask task);

  public abstract IThreadPoolStats getStats ();

}
