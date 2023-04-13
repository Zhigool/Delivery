package ru.sapronov.api_testing


import org.springframework.http.HttpStatus
import ru.sapronov.auth.dto.CreateAccountRequestDto
import ru.sapronov.auth.dto.CreateAndGetAccessTokenRequestDto

class AuthApiTest extends BaseApiTest {

    def "Create account test"() {
        given: "Request with email and password for create account"
        var dto = new CreateAccountRequestDto(test1email, test1password)

        when: "Request is executing"
        authService.createAccount(dto)

        then: "We should to find an email with confirmation token in mailhog"
        waitBefore()
        mailHogService.confirmationTokenFromEmail
    }

    def "Activate account"() {
        given:
        createAccTest1()
        def confirmationToken = getConfirmationToken()

        when: "Req"
        def response = authService.activateAccount(confirmationToken)

        then:
        waitBefore()
        response.statusCode == HttpStatus.OK
    }

    def "Get access token"() {
        given:
        createAccTest1()
        def token = getConfirmationToken()
        activateAccount(token)
        def request = new CreateAndGetAccessTokenRequestDto(
                test1email,
                test1password
        )

        when:
        def response = authService.createAccessTokenAndGet(request)

        then:
        waitBefore()
        !response.body.accessToken()?.isBlank()
    }
}

