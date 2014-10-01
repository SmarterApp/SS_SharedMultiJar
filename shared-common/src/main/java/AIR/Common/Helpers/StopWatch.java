/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Helpers;

/**
 * @author temp_rreddy
 * 
 */
public class StopWatch extends org.apache.commons.lang3.time.StopWatch
{

  private boolean _isRunning = false;

  public void resume () {
    super.resume ();
    _isRunning = true;
  }

  public void start () {
    super.start ();
    _isRunning = true;
  }

  public void stop () {
    super.stop ();
    _isRunning = false;
  }

  public void suspend () {
    super.suspend ();
    _isRunning = false;
  }

  public boolean isRunning () {
    return _isRunning;
  }
}
