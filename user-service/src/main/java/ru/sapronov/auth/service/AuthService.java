package ru.sapronov.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sapronov.auth.event.ExchangeNames;
import ru.sapronov.auth.exception.PersonNotFoundException;
import ru.sapronov.auth.exception.WrongCredentialsException;
import ru.sapronov.auth.model.PasswordCryptor;
import ru.sapronov.auth.model.Person;
import ru.sapronov.auth.model.PersonFactory;
import ru.sapronov.auth.model.PersonRepository;
import ru.sapronov.common.auth.Claims;
import ru.sapronov.common.auth.PrincipalProvider;
import ru.sapronov.common.event.EventService;
import ru.sapronov.common.event.person.NewAccountWasCreatedEvent;
import ru.sapronov.common.provider.UuidProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonFactory factory;
    private final EventService eventService;
    private final PersonRepository repository;
    private final PasswordCryptor cryptor;
    private final AccessTokenFactory accessTokenFactory;
    private final UuidProvider uuidProvider;

    @Transactional
    public void createAccount(String email, String password) {
        repository.findByEmail(email);
        UUID confirmationToken = uuidProvider.getRandom();
        eventService.sendEventToExchange(new NewAccountWasCreatedEvent(
                email,
                confirmationToken
        ), ExchangeNames.NEW_ACCOUNT_WAS_CREATED_EXCHANGE_NAME);
        Person person = factory.create(email, password, confirmationToken);
        repository.save(person);
    }

    @Transactional
    public void confirmEmail(UUID confirmationToken) {
        Person person = repository.findByConfirmationToken(confirmationToken);
        person.confirmEmail();
        repository.save(person);
    }

    @Transactional
    public String createAndGetAccessToken(String email, String password) {
        var optionalPerson = repository.findByEmail(email);
        if (optionalPerson.isEmpty()) {
            throw new PersonNotFoundException();
        }
        var person = optionalPerson.get();
        var personId = person.getId();
        var role = PrincipalProvider.PrincipalRole.valueOf(person.getRole().name());
        var encodedPassword = person.getPassword();
        if (!cryptor.check(encodedPassword, password)) { throw new WrongCredentialsException(); }
        return accessTokenFactory.createToken(email, new Claims(personId, role));
    }
}
