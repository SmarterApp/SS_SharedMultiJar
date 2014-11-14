/*************************************************************************
 * Educational Online Test Delivery System
 * Copyright (c) 2014 American Institutes for Research
 *
 * Distributed under the AIR Open Source License, Version 1.0
 * See accompanying file AIR-License-1_0.txt or at 
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 *************************************************************************/

package TDS.Shared.Web.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * @author jmambo
 *
 */
public interface ITdsRestClient
{

  /**
   * @param url
   * @param method
   * @param requestEntity
   * @param responseType
   * @return
   */
  <T> ResponseEntity<T> exchange (String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType);

  /**
   * @param baseUri
   * @param path
   * @param method
   * @param requestEntity
   * @param responseType
   * @return
   */
  <T> ResponseEntity<T> exchange (String baseUri, String path, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType);

  /**
   * @param url
   * @param responseType
   * @return
   */
  <T> T getForObject (String url, Class<T> responseType);

  /**
   * @param baseUri
   * @param path
   * @param responseType
   * @return
   */
  <T> T getForObject (String baseUri, String path, Class<T> responseType);

  /**
   * @param url
   * @param request
   * @param responseType
   * @return
   */
  <T> T postForObject (String url, Object request, Class<T> responseType);

  /**
   * @param baseUri
   * @param path
   * @param request
   * @param responseType
   * @return
   */
  <T> T postForObject (String baseUri, String path, Object request, Class<T> responseType);

  /**
   * @param url
   * @param request
   */
  void put (String url, Object request);

  /**
   * @param baseUri
   * @param path
   * @param request
   */
  void put (String baseUri, String path, Object request);
  
}
