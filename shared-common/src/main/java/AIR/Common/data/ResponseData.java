/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.data;

//import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mpatel
 *
 */
public class ResponseData<T>
{
  
  private long replyCode;
  
  private String replyText;
  
  private T data;

  
  /**
   * @param replyCode
   * @param replyText
   * @param data
   */
  public ResponseData (long replyCode, String replyText, T data) {
    super ();
    this.replyCode = replyCode;
    this.replyText = replyText;
    this.data = data;
  }
  
  public ResponseData() {
    
  }
  

//  @JsonProperty("replyText")
  public String getReplyText () {
    return replyText;
  }

//  @JsonProperty("replyCode")
  public long getReplyCode () {
    return replyCode;
  }

  public void setReplyCode (long replyCode) {
    this.replyCode = replyCode;
  }

  public void setReplyText (String replyText) {
    this.replyText = replyText;
  }

//  @JsonProperty("data")
  public T getData () {
    return data;
  }

  public void setData (T data) {
    this.data = data;
  }
  
}
