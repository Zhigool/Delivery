package ru.sapronov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import ru.sapronov.client.HttpClient;
import ru.sapronov.common.dto.PointDto;
import ru.sapronov.tracking.dto.SaveTrackingInfoRequestDto;

import java.util.UUID;

@RequiredArgsConstructor
public class TrackingService {

    private final static String TRACKING_BASE_URL = "http://localhost:8082/api/tracking/v1.0";
    private final static String SAVE_TRACKING_INFO_URL = TRACKING_BASE_URL + "/save-tracking-info";
    private final static String GET_TRACKING_INFO_URL = TRACKING_BASE_URL + "/get-tracking-info";

    private final HttpClient httpClient;

    public ResponseEntity<Void> saveTrackingInfo(SaveTrackingInfoRequestDto request, String accessToken) {
        return httpClient.post(SAVE_TRACKING_INFO_URL, request, Void.class, accessToken);
    }

    public ResponseEntity<PointDto> getTrackingInfo(UUID courierId, String accessToken) {
        return httpClient.get(GET_TRACKING_INFO_URL + "/" + courierId.toString(), PointDto.class, accessToken);
    }
}
