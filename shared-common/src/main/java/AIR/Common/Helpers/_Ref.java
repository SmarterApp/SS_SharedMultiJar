/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Helpers;

// TODO Shiva add preamble
/*
 * java does not have a "ref" or "out" counterpart. We will use a generic
 * wrapper instead.
 */
public class _Ref<T>
{
  private T ref;

  public _Ref () {
    ref = null;
  }

  public _Ref (T t) {
    ref = t;
  }

  public T get () {
    return ref;
  }

  public void set (T t) {
    this.ref = t;
  }

  @Override
  public String toString () {
    return ref.toString ();
  }
}
