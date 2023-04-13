package ru.sapronov.tracking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sapronov.common.auth.PrincipalProvider;
import ru.sapronov.common.dto.PointDto;
import ru.sapronov.common.exception.AccessDeniedException;
import ru.sapronov.tracking.dao.TrackingRecord;
import ru.sapronov.tracking.dto.SaveTrackingInfoRequestDto;
import ru.sapronov.tracking.service.TrackingService;

import java.util.UUID;

@RestController
@RequestMapping("/api/tracking/v1.0")
@RequiredArgsConstructor
public class TrackingController {
    private final PrincipalProvider principalProvider;
    private final TrackingService trackingService;
    @PostMapping("/save-tracking-info")
    public ResponseEntity<Void> saveTrackingInfo(@RequestBody SaveTrackingInfoRequestDto request) {
        trackingService.saveTrackingInfo(
                principalProvider.getPrincipal()
                        .orElseThrow(AccessDeniedException::new).id(),
                request.longitude(),
                request.latitude()
        );
        return ResponseEntity.ok(null);
    }

    @GetMapping("/get-tracking-info/{courierId}")
    public ResponseEntity<PointDto> getTrackingInfo(@PathVariable UUID courierId) {
        TrackingRecord trackingRecord = trackingService.getTrackingInfoByOrderId(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new),
                courierId
        );
        return ResponseEntity.ok(new PointDto(
                trackingRecord.getLocation().getX(),
                trackingRecord.getLocation().getY()
        ));
    }
}
