package com.xining.sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PersonBuilderTest {

    @Test
    public void personBuilder() {
        Person person = PersonBuilder.of().name("super woman").age(33).build();
        assertEquals("super woman", person.name());
        assertEquals(33, person.age());
        assertFalse(person.male());
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
