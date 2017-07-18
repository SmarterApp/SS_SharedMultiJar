/***************************************************************************************************
 * Educational Online Test Delivery System
 * Copyright (c) 2017 Regents of the University of California
 *
 * Distributed under the AIR Open Source License, Version 1.0
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 *
 * SmarterApp Open Source Assessment Software Project: http://smarterapp.org
 * Developed by Fairway Technologies, Inc. (http://fairwaytech.com)
 * for the Smarter Balanced Assessment Consortium (http://smarterbalanced.org)
 **************************************************************************************************/

package tds.shared.spring.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private static final String TRACER_ID_HEADER = "X-B3-TraceId";
    private final ObjectMapper objectMapper;
    private boolean prettyPrintJson;

    public RestTemplateLoggingInterceptor(final ObjectMapper objectMapper, final boolean prettyPrintJson) {
        this.objectMapper = objectMapper;
        this.prettyPrintJson = prettyPrintJson;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request,
                                        final byte[] body,
                                        final ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        // get tracer id that used to associate all rest calls that are a child of this request
        final String traceId = Optional.fromNullable(MDC.get(TRACER_ID_HEADER)).or(UUID.randomUUID().toString().replace("-", ""));
        logRequest(request, body, traceId);
        final ClientHttpResponse response;
        try {
            response = clientHttpRequestExecution.execute(request, body);
        } catch (IOException e) {
            log.error(String.format("Exception occurred while executing rest request. METHOD: %s URI: %s", request.getMethod(), request.getURI()), e);
            throw e;
        }
        logResponse(response, traceId);
        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body, final String traceId) {
        String bodyString = new String(body, Charsets.UTF_8);
        final MediaType contentType = request.getHeaders().getContentType();
        MDC.put(TRACER_ID_HEADER, traceId);
        request.getHeaders().add(TRACER_ID_HEADER, traceId);

        if (!bodyString.isEmpty() && prettyPrintJson && contentType.getSubtype().equals(CONTENT_TYPE_SUBTYPE_JSON)) {
            try {
                final Object json = objectMapper.readValue(bodyString, Object.class);
                bodyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } catch (IOException e) {
                log.debug("Unable to parse the request as JSON. Printing raw request body string...", e);
            }
        }

        log.debug("\n=========================================* REQUEST *========================================== \n" +
                " Trace ID    :   {} \n" +
                " Method/URI  :   {} - {} \n" +
                " Headers     :   {} \n" +
                " Body        :   {} \n" +
                "==============================================================================================",
            traceId,
            request.getMethod(), request.getURI(),
            request.getHeaders(),
            bodyString.isEmpty() ? "<no body>" : bodyString);
    }

    private void logResponse(final ClientHttpResponse response, final String traceId) {
        String bodyString = "";
        final MediaType contentType = response.getHeaders().getContentType();

        if (contentType != null && contentType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
            bodyString = "<BINARY>";
        } else {
            try (final InputStream in = response.getBody()) {
                bodyString = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            } catch (IOException e) {
                // An IOException will be thrown when the body is zero-length (e.g. a 404 response)
                log.debug("Unable to open response body", e);
            }

            if (!bodyString.isEmpty() && prettyPrintJson && contentType.getSubtype().equals(CONTENT_TYPE_SUBTYPE_JSON)) {
                try {
                    final Object json = objectMapper.readValue(bodyString, Object.class);
                    bodyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                } catch (IOException e) {
                    log.debug("Unable to parse the response as JSON.", e);
                }
            }
        }

        try {
            log.debug("\n=========================================* RESPONSE *========================================= \n" +
                    " Trace ID    :   {} \n" +
                    " Status Code :   {} \n" +
                    " Status Text :   {} \n" +
                    " Headers     :   {} \n" +
                    " Body        :   {} \n" +
                    "==============================================================================================",
                traceId,
                response.getStatusCode(),
                response.getStatusText(),
                response.getHeaders(),
                bodyString.isEmpty() ? "<no body>" : bodyString);
        } catch (IOException e) {
            log.warn("Exception occurred while logging rest response.", e);
        }
    }
}
