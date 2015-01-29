/*************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2015 American
 * Institutes for Research
 *
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 *************************************************************************/

package AIR.Common.xml;

/**
 * @author temp_mbikkina
 *
 */
public class NotProperJDomNodeException extends RuntimeException
{
  public NotProperJDomNodeException (String message)
  {
    super (message);
  }

  public NotProperJDomNodeException (String message, Throwable th)
  {
    super (message, th);
  }

  public NotProperJDomNodeException (Throwable th)
  {
    super (th);
  }

}
