/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web.client;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @author mpatel
 *
 */
public class GenericRestAPIClient
{
 
  public String restAPIURL;
  
  private RestTemplate restTemplate = new RestTemplate ();

  
  public GenericRestAPIClient(String restAPIURL) {
    this(restAPIURL, null);
  }
  
  
  public GenericRestAPIClient(String restAPIURL, ResponseErrorHandler errorHandler) {
    if(StringUtils.isEmpty (restAPIURL)) {
      throw new IllegalArgumentException ("restAPIURL is required to create TDSRestAPIClient..");
    }
    this.restAPIURL = restAPIURL;
    
    if (errorHandler != null) {
       restTemplate.setErrorHandler(errorHandler);
    }
  }
  
  public <T> T getForObject(Class<T> responseType){
    return (T)restTemplate.getForObject(restAPIURL, responseType);
  }
  
  public <T> T postForObject(Object request, Class<T> responseType) {
    return restTemplate.postForObject(restAPIURL, request, responseType);
  }

  public void put (Object request) {
    restTemplate.put(restAPIURL, request);
  }
  
  public <T> ResponseEntity<T> exchange(HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
    return restTemplate.exchange (restAPIURL, method, requestEntity, responseType);
  }

}
