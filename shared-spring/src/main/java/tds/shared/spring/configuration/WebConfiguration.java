package tds.shared.spring.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import tds.shared.spring.interceptors.RestTemplateLoggingInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Spring configuration class for web interaction
 */
@Configuration
public class WebConfiguration {
    @Bean(name = "integrationObjectMapper")
    @Primary
    public ObjectMapper getIntegrationObjectMapper() {
        return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new GuavaModule())
            .registerModule(new JodaModule());
    }

    @Bean(name = "integrationRestTemplate")
    @Primary
    public RestTemplate restTemplate() {
        // Jackson Converters
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getIntegrationObjectMapper());
        final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        final List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);
        restTemplate.setMessageConverters(converters);

        // Request/Response RestTemplate Logging
        final ClientHttpRequestInterceptor loggingInterceptor = new RestTemplateLoggingInterceptor(getIntegrationObjectMapper());
        restTemplate.setInterceptors(Arrays.asList(loggingInterceptor));

        return restTemplate;
    }
}
