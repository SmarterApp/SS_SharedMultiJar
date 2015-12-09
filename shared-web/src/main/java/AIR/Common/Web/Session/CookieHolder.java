/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.Session;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Helpers.InvalidCastException;

// TODO Shiva We may not need this. Are there default implementations?
/*
 */
public class CookieHolder extends ArrayList<MultiValueCookie>
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private HttpContext       _context         = null;

  protected CookieHolder (HttpContext context, Cookie[] cookies) {
    super ();
    this._context = context;
    if (cookies == null)
      return;
    for (Cookie cookie : cookies) {
      this.addCookieOnConstructor (cookie);
    }
  }

  @Override
  public boolean contains (Object cookieObject) {
    if (cookieObject instanceof MultiValueCookie) {
      MultiValueCookie cookie = (MultiValueCookie) cookieObject;
      if (findCookie (cookie.getName ()) != null)
        return true;
    } else
      throw new InvalidCastException ("Parameter needs to be object of type javax.servlet.http.Cookie.");
    return false;
  }

  public Iterator<MultiValueCookie> getCookies () {
    return this.iterator ();
  }

  @Override
  public boolean add (MultiValueCookie cookie) {
    remove (cookie);
    Cookie webCookie = cookie.getUnderlyingWebCookie ();
    _context.getResponse ().addCookie (webCookie);
    return super.add (cookie);
  }

  public boolean addToStore (MultiValueCookie cookie) {
	remove (cookie);
	return super.add (cookie);
  }

  //adding a cookie to the store would not result in it being added to the 
  //http response until this method is explicitly called
  public void writeStore() {
	for (MultiValueCookie cookie : this) {
	  _context.getResponse ().addCookie(cookie.getUnderlyingWebCookie ());
	}
  }
  
  public MultiValueCookie findCookie (String name) {
    Iterator<MultiValueCookie> cookies = this.iterator ();
    while (cookies.hasNext ()) {
      MultiValueCookie cookie = cookies.next ();
      if (StringUtils.equals (name, cookie.getName ()))
        return cookie;
    }
    return null;
  }

  @Override
  public boolean remove (Object cookie) {
    if (cookie instanceof MultiValueCookie) {
      for (int counter1 = 0; counter1 < this.size (); ++counter1) {
        MultiValueCookie existing = this.get (counter1);
        if (StringUtils.equals (existing.getName (), ((MultiValueCookie) cookie).getName ())) {
          this.remove (counter1);
          return true;
        }
      }
    }
    return false;
  }

  private boolean addCookieOnConstructor (Cookie cookie) {
    /*
     * the original code for this method would add those cookies by calling
     * "this.add". however, given cookie semantics, we had to update "this.add"
     * to also add the cookie to the response. but on request processing that
     * task is already carried out by the webserver. that is why we are copying
     * the code this "this.add" except the line where we add the cookie to the
     * response.
     */
    // MultiValueCookie mcookie = new MultiValueCookie (cookie, _context);
    // return this.add (mcookie);
    MultiValueCookie existingCookie = findCookie (cookie.getName ());
    if (existingCookie != null)
      this.remove (existingCookie);
    return super.add (new MultiValueCookie (cookie));
  }
}
