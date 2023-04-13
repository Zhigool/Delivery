package ru.sapronov.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MailHogService {

    private static final String MAILHOG_URL = "http://localhost:9025";
    private final RestTemplate restTemplate;

    public MailHogService() {
        restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

        restTemplate.setMessageConverters(messageConverters);
    }

    public UUID getConfirmationTokenFromEmail() {
        return getMessagesV1().stream()
                .map(message -> message.getContent().getBody().split(" ")[2])
                .map(UUID::fromString)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    public List<Message> getMessagesV1() {
        ResponseEntity<List<Message>> response = restTemplate.exchange(
                MAILHOG_URL + "/api/v1/messages",
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                new ParameterizedTypeReference<>(){}
        );
        return response.getBody();
    }

    public void clear() {
        restTemplate.delete(MAILHOG_URL + "/api/v1/messages");
    }
}
