package tds.shared.spring.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * An interceptor for logging REST requests and responses
 */
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);
    private static final String CONTENT_TYPE_SUBTYPE_JSON = "json";
    private final ObjectMapper objectMapper;

    public RestTemplateLoggingInterceptor(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        final UUID traceId = UUID.randomUUID();
        logRequest(request, body, traceId);
        ClientHttpResponse response = clientHttpRequestExecution.execute(request, body);
        logResponse(response, traceId);
        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body, final UUID traceId) {
        String bodyString = new String(body, Charsets.UTF_8);

        MediaType contentType = request.getHeaders().getContentType();

        if (!bodyString.isEmpty() && contentType.getSubtype().equals(CONTENT_TYPE_SUBTYPE_JSON)) {
            try {
                final Object json = objectMapper.readValue(bodyString, Object.class);
                bodyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } catch (IOException e) {
                log.debug("Unable to parse the request as JSON. Printing raw request body string...");
            }
        }

        log.debug("=========================================* REQUEST *==========================================");
        log.debug(" Trace ID    :   {}", traceId);
        log.debug(" Method/URI  :   {} - {}", request.getMethod(), request.getURI());
        log.debug(" Headers     :   {} ", request.getHeaders());
        log.debug(" Body        :   {} ", bodyString);
        log.debug("==============================================================================================");
    }

    private void logResponse(final ClientHttpResponse response, final UUID traceId) throws IOException {
        String bodyString;

        try (final InputStream in = response.getBody()) {
            bodyString = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
        }

        MediaType contentType = response.getHeaders().getContentType();

        if (!bodyString.isEmpty() && contentType.getSubtype().equals(CONTENT_TYPE_SUBTYPE_JSON)) {
            try {
                final Object json = objectMapper.readValue(bodyString, Object.class);
                bodyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } catch (IOException e) {
                log.debug("Unable to parse the response as JSON. Printing raw response body string...");
            }
        }

        log.debug("=========================================* RESPONSE *=========================================");
        log.debug(" Trace ID    :   {}", traceId);
        log.debug(" Status Code :   {}", response.getStatusCode());
        log.debug(" Status Text :   {}", response.getStatusText());
        log.debug(" Headers     :   {}", response.getHeaders());
        log.debug(" Body        :   {}", bodyString);
        log.debug("==============================================================================================");
    }
}
