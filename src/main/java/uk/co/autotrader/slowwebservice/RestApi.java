package uk.co.autotrader.slowwebservice;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE)
public class RestApi {

    private static final List<Person> people;

    static {
        people = new LinkedList<>();
        people.add(new Person("Mike", "Rocke"));
        people.add(new Person("Lon", "Chaney"));
        people.add(new Person("Vincent", "Price"));
    }


    @RequestMapping(method = RequestMethod.GET)
    public Resources<Resource<Person>> list(@RequestParam(required = false) String firstName, @RequestParam(required = false) String surname) {
//        Link selfLink = ControllerLinkBuilder.linkTo(methodOn(RestApi.class).list(firstName, surname)).withSelfRel();
        return new Resources<>(people.stream()
                .filter(person -> firstName == null || firstName.equals(person.getFirstName()))
                .filter(person -> surname == null || surname.equals(person.getSurname()))
                .map(this::toResource)
                .collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{firstName}_{surname}")
    public Resource<Person> get(@PathVariable("firstName") String firstName, @PathVariable("surname") String surname) {
        return people.stream()
                .filter(person -> firstName.equals(person.getFirstName()))
                .filter(person -> surname.equals(person.getSurname()))
                .findFirst()
                .map(this::toResource)
                .orElseThrow(() -> new NotFoundException(String.format("The person %s %s was not found", firstName, surname)));
    }

    private Resource<Person> toResource(Person person) {
        Link selfLink = ControllerLinkBuilder.linkTo(methodOn(RestApi.class).get(person.getFirstName(), person.getSurname())).withSelfRel();
        return new Resource<>(person, selfLink);
    }
}
