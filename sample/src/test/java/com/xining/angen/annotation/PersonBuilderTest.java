package com.xining.angen.annotation;

import com.xining.angen.sample.PersonA;
import com.xining.angen.sample.PersonABuilder;
import com.xining.angen.sample.PersonB;
import com.xining.angen.sample.PersonBBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


class PersonBuilderTest {

    @Test
    public void personABuilder() {
        PersonA person = PersonABuilder.of().name("super woman").age(33).build();
        assertEquals("super woman", person.name());
        assertEquals(33, person.age());
        assertFalse(person.male());
    }

    @Test
    public void personBBuilder() {
        PersonB person = PersonBBuilder.of().name("super woman").age(33).build();
        assertEquals("super woman", person.getName());
        assertEquals(33, person.getAge());
        assertFalse(person.isMale());
    }

    @Test
    public void should_return_person_with_default_male() {
//        Person person = PersonBuilder.of().name("super woman").age(33).build();
//        assertEquals("super woman", person.name());
//        assertEquals(33, person.age());
//        assertFalse(person.male());
    }

    @Test
    public void should_complain_missing_name() {
//        Exception e = assertThrows(IllegalArgumentException.class, () -> PersonBuilder.of().build());
//        assertEquals("A person should a name", e.getMessage());
    }

    @Test
    public void should_complain_age_not_greater_than_zero() {
//        Exception e = assertThrows(IllegalArgumentException.class, () -> PersonBuilder.of().name("jason").age(0).build());
//        assertEquals("A person's age should be greater than 0", e.getMessage());
    }

}
