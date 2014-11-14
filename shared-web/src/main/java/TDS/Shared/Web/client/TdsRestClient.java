package TDS.Shared.Web.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author jmambo
 *
 */
public class TdsRestClient implements ITdsRestClient
{

  private RestOperations _restOperation = new RestTemplate ();

  private String _baseUri;
  
  public TdsRestClient(RestOperations restOperation) {
     _restOperation = restOperation;
  }
  
  @Override
  public <T> T getForObject(String url, Class<T> responseType){
    return (T)_restOperation.getForObject(getRealUrl(url), responseType);
  }
  
  @Override
  public <T> T getForObject(String baseUri, String path, Class<T> responseType){
    return (T)_restOperation.getForObject(baseUri + path, responseType);
  }
  
  @Override
  public <T> T postForObject(String url, Object request, Class<T> responseType) {
    return _restOperation.postForObject(getRealUrl(url), request, responseType);
  }

  @Override
  public <T> T postForObject(String baseUri, String path, Object request, Class<T> responseType) {
    return _restOperation.postForObject(baseUri + path, request, responseType);
  }
  
  @Override
  public void put (String url, Object request) {
    _restOperation.put(getRealUrl(url), request);
  }

  @Override
  public void put (String baseUri, String path, Object request) {
    _restOperation.put(baseUri + path, request);
  }
  
  @Override
  public <T> ResponseEntity<T> exchange (String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
    return _restOperation.exchange (getRealUrl(url), method, requestEntity, responseType);
  }
  
  @Override
  public <T> ResponseEntity<T> exchange (String baseUri, String path, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
    return _restOperation.exchange (baseUri + path, method, requestEntity, responseType);
  }
  
  private String getRealUrl(String url) {
     return (url.startsWith ("http")) ?  url : _baseUri + url;
  }

  public String getBaseUri () {
    return _baseUri;
  }

  public void setBaseUri (String baseUri) {
    _baseUri = baseUri;
  }
  

}
