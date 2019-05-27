package com.xining.sample;

import com.xining.sample2.Book;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @since 2019/5/27
 */
@Builder
public interface Person {
    @NotNull
    String name();
    @NotNull
    int age();
    @NotNull
    boolean male();
    @NotNull
    List<Book> books();
}
