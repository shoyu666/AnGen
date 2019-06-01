package com.xining.angen.sample;


import com.xining.angen.annotation.Builder;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @since 2019/5/27
 */
@Builder
public interface PersonA {
    @NotNull(message = "name should not be null")
    String name();

    @NotNull(message = "age should not be null")
    int age();

    @NotNull(message = "male should not be null")
    boolean male();

    List<Book> books();
}
