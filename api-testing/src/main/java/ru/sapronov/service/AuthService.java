package ru.sapronov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import ru.sapronov.auth.dto.AppointCourierRequestDto;
import ru.sapronov.auth.dto.CreateAccountRequestDto;
import ru.sapronov.auth.dto.CreateAndGetAccessTokenRequestDto;
import ru.sapronov.auth.dto.CreateAndGetAccessTokenResponseDto;
import ru.sapronov.client.HttpClient;

import java.util.UUID;

@RequiredArgsConstructor
public class AuthService {

    private final static String AUTH_BASE_URL = "http://localhost:8080/api/auth/v1.0";
    private final static String CREATE_ACCOUNT_URL = AUTH_BASE_URL + "/create-account";
    private final static String ACTIVATE_ACCOUNT_URL = AUTH_BASE_URL + "/activate-account";
    private final static String CREATE_ACCESS_TOKEN_AND_GET = AUTH_BASE_URL + "/create-and-get-access-token";
    private final static String APPOINT_COURIER_URL = AUTH_BASE_URL + "/appoint-courier";
    private final HttpClient httpClient;

    public void createAccount(CreateAccountRequestDto dto) {
        httpClient.anonymousPost(CREATE_ACCOUNT_URL, dto, Void.class);
    }

    public ResponseEntity<Void> activateAccount(UUID confirmationToken) {
        return httpClient.anonymousGet(ACTIVATE_ACCOUNT_URL + "/" + confirmationToken.toString(), Void.class);
    }

    public ResponseEntity<CreateAndGetAccessTokenResponseDto> createAccessTokenAndGet(
            CreateAndGetAccessTokenRequestDto request) {
        return httpClient.anonymousPost(CREATE_ACCESS_TOKEN_AND_GET, request, CreateAndGetAccessTokenResponseDto.class);
    }

    public ResponseEntity<Void> appointCourier(String courierEmail, String accessToken) {
        return httpClient.post(APPOINT_COURIER_URL, new AppointCourierRequestDto(
                courierEmail
        ), Void.class, accessToken);
    }
}
