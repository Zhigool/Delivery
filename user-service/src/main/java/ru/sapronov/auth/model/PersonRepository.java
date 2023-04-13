package ru.sapronov.auth.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sapronov.auth.exception.PersonNotFoundException;
import ru.sapronov.auth.model.state.PersonState;
import ru.sapronov.auth.model.state.PersonStateRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PersonRepository {

    private final PersonStateRepository stateRepository;

    public Person findByConfirmationToken(UUID confirmationToken) {
        PersonState state = stateRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(PersonNotFoundException::new);
        return new Person(state);
    }

    public void save(Person person) {
        stateRepository.save(person.state());
    }

    public Optional<Person> findByEmail(String email) {
        return stateRepository.findByEmail(email).map(Person::new);
    }
}
