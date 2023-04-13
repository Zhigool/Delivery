package ru.sapronov.auth.model.state;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persons")
public class PersonState {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    @Enumerated(EnumType.STRING)
    private PersonRole role;
    private UUID confirmationToken;
    private boolean emailIsConfirmed;
    private String firstName;
    private String lastName;
    private String password;

    public enum PersonRole {
        CLIENT, ADMIN, COURIER
    }

    public PersonState copy() {
        return new PersonState(
            this.id,
            this.email,
            this.role,
            this.confirmationToken,
            this.emailIsConfirmed,
            this.firstName,
            this.lastName,
            this.password
        );
    }
}
