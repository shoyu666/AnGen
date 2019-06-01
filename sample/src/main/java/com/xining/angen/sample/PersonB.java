package com.xining.angen.sample;


import com.xining.angen.annotation.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @since 2019/5/27
 */
@Builder
@Getter
public class PersonB {
    @NotNull(message = "name should not be null")
    private final String name;
    @NotNull(message = "age should not be null")
    private final int age;
    @NotNull(message = "male should not be null")
    private final boolean male;
    private final List<Book> books;
    public PersonB(String name, int age, boolean male, List<Book> books) {
        this.name = name;
        this.age = age;
        this.male = male;
        this.books = books;
    }
}
