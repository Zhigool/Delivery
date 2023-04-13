package ru.sapronov.auth.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sapronov.auth.model.state.PersonState;
import ru.sapronov.auth.model.state.PersonStateRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PersonFactory {

    private final PersonStateRepository repository;
    private final PasswordCryptor cryptor;

    public Person create(String email, String rawPassword, UUID confirmationToken) {
        PersonState state = new PersonState();
        state.setEmail(email);
        state.setRole(PersonState.PersonRole.CLIENT);
        state.setPassword(cryptor.salting(rawPassword));
        state.setEmailIsConfirmed(false);
        state.setConfirmationToken(confirmationToken);
        state = repository.save(state);
        return new Person(state);
    }
}
