package ru.sapronov.auth.model.state;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonStateRepository extends CrudRepository<PersonState, UUID> {
    Optional<PersonState> findByConfirmationToken(UUID confirmationToken);

    Optional<PersonState> findByEmail(String email);
}
