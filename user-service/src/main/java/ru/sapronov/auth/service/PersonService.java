package ru.sapronov.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sapronov.auth.event.ExchangeNames;
import ru.sapronov.auth.exception.PersonNotFoundException;
import ru.sapronov.auth.model.Person;
import ru.sapronov.auth.model.PersonRepository;
import ru.sapronov.common.auth.PrincipalProvider;
import ru.sapronov.common.event.EventService;
import ru.sapronov.common.event.person.NewCourierWasAppointedEvent;
import ru.sapronov.common.exception.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final EventService eventService;

    public void appointCourier(PrincipalProvider.Principal principal, String courierEmail) {
        if (principal.role() != PrincipalProvider.PrincipalRole.ADMIN) {
            throw new AccessDeniedException();
        }
        Person courier = personRepository.findByEmail(courierEmail)
                .orElseThrow(PersonNotFoundException::new);
        courier.appointAsCourier();
        personRepository.save(courier);
        eventService.sendEventToExchange(new NewCourierWasAppointedEvent(
                courier.getId(),
                courier.getFirstname(),
                courier.getLastname()
        ), ExchangeNames.NEW_COURIER_WAS_APPOINTED_EXCHANGE_NAME);
    }
}
