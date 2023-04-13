package ru.sapronov.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.sapronov.order.dto.OrderDto;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class HttpClient {

    private final RestTemplate restTemplate;


    public <T> ResponseEntity<T> anonymousPost(
            String url,
            Object body,
            Class<T> responseType) {

        return post(url, body, responseType, null);
    }

    public <T> ResponseEntity<T> post(
            String url,
            Object body,
            Class<T> responseType,
            String accessToken) {

        return call(url, HttpMethod.POST, body, responseType, accessToken);
    }

    public <T> ResponseEntity<T> anonymousGet(
            String url,
            Class<T> responseType) {

        return get(url, responseType, null);
    }

    public <T> ResponseEntity<T> get(
            String url,
            Class<T> responseType,
            String accessToken) {

        return call(url, HttpMethod.GET, null, responseType, accessToken);
    }

    public ResponseEntity<List<OrderDto>> get(
            String url,
            ParameterizedTypeReference<List<OrderDto>> type,
            String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null && !accessToken.isBlank()) {
            headers.add("Authorization", accessToken);
        }
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, type, Collections.emptyMap());
    }

    public <T> ResponseEntity<T> call(
            String url,
            HttpMethod method,
            Object body,
            Class<T> responseType,
            String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null && !accessToken.isBlank()) {
            headers.add("Authorization", accessToken);
        }
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, entity, responseType);
    }
}
