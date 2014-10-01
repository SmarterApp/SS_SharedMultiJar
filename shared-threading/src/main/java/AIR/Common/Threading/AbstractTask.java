/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Threading;

import java.sql.Timestamp;

import AIR.Common.time.TimeSpan;

public abstract class AbstractTask implements ITask
{
  private volatile Timestamp _timeEnqueued  = null;
  private volatile Timestamp _timeDequeued  = null;
  private volatile TimeSpan  _executionTime = null;

  @Override
  public Timestamp getTimeEnqueued () {
    return _timeEnqueued;
  }

  @Override
  public void setTimeEnqueued (Timestamp value) {
    _timeEnqueued = value;
  }

  @Override
  public Timestamp getTimeDequeued () {
    return _timeDequeued;
  }

  @Override
  public void setTimeDequeued (Timestamp value) {
    _timeDequeued = value;
  }

  @Override
  public TimeSpan getExecutionTime () {
    return _executionTime;
  }

  @Override
  public void setExecutionTime (TimeSpan value) {
    _executionTime = value;
  }

}
