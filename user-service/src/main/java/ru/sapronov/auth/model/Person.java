package ru.sapronov.auth.model;

import ru.sapronov.auth.model.state.PersonState;
import ru.sapronov.common.model.Stateful;

import java.util.UUID;

public record Person(PersonState state) implements Stateful<PersonState> {

    public void confirmEmail() {
        if (state.isEmailIsConfirmed()) return;
        state.setEmailIsConfirmed(true);
        state.setConfirmationToken(null);
    }

    public UUID getId() {
        return state.copy().getId();
    }

    public String getPassword() {
        return state.copy().getPassword();
    }

    public PersonState.PersonRole getRole() {
        return state.getRole();
    }

    @Override
    public PersonState state() {
        return state.copy();
    }

    public void appointAsCourier() {
        state.setRole(PersonState.PersonRole.COURIER);
    }

    public String getFirstname() {
        return state.getFirstName();
    }

    public String getLastname() {
        return state.getLastName();
    }
}
