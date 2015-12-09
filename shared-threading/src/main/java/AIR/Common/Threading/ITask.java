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

public interface ITask
{

  void Execute ();

  Timestamp getTimeEnqueued ();

  void setTimeEnqueued (Timestamp value);

  Timestamp getTimeDequeued ();

  void setTimeDequeued (Timestamp value);

  TimeSpan getExecutionTime ();

  void setExecutionTime (TimeSpan value);

}
