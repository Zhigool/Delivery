package ru.sapronov.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sapronov.auth.dto.AppointCourierRequestDto;
import ru.sapronov.auth.dto.CreateAccountRequestDto;
import ru.sapronov.auth.dto.CreateAndGetAccessTokenRequestDto;
import ru.sapronov.auth.dto.CreateAndGetAccessTokenResponseDto;
import ru.sapronov.auth.exception.ConfirmationTokenIsNotValid;
import ru.sapronov.auth.exception.EmailNotValid;
import ru.sapronov.auth.exception.WrongCredentialsException;
import ru.sapronov.auth.service.AuthService;
import ru.sapronov.auth.service.PersonService;
import ru.sapronov.common.auth.PrincipalProvider;
import ru.sapronov.common.exception.AccessDeniedException;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/v1.0")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PersonService personService;
    private final PrincipalProvider principalProvider;

    @PostMapping("/create-account")
    ResponseEntity<Void> createAccount(@RequestBody CreateAccountRequestDto request) {
        if (request == null || !EmailValidator.getInstance().isValid(request.email())) { throw new EmailNotValid(); }
        authService.createAccount(request.email(), request.password());
        return ResponseEntity.ok(null);
    }

    @GetMapping("/activate-account/{token}")
    ResponseEntity<Void> activateAccount(@PathVariable("token") UUID confirmationToken) {
        if (Objects.isNull(confirmationToken)) { throw new ConfirmationTokenIsNotValid(); }
        authService.confirmEmail(confirmationToken);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/create-and-get-access-token")
    ResponseEntity<CreateAndGetAccessTokenResponseDto> createAndGetToken(
            @RequestBody CreateAndGetAccessTokenRequestDto request
    ) {
        if (Objects.isNull(request) || request.email() == null || request.password() == null) {
            throw new WrongCredentialsException();
        }
        return ResponseEntity.ok(new CreateAndGetAccessTokenResponseDto(
                authService.createAndGetAccessToken(request.email(), request.password())
        ));
    }

    @PostMapping("/appoint-courier")
    ResponseEntity<Void> appointCourier(@Valid @RequestBody AppointCourierRequestDto request) {
        personService.appointCourier(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new),
                request.email()
        );
        return ResponseEntity.ok(null);
    }
}
