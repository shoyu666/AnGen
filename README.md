# Java Annotation Processor  Demo


```java
todo
validation
@Default
合法校验(比如name() name(参数)等情况的处理)
...
自动编译
```

```java
@Builder
public interface Person {
    @NotNull(message = "name should not be null")
    String name();
    @NotNull(message = "age should not be null")
    int age();
    @NotNull(message = "male should not be null")
    boolean male();
    List<Book> books();
}
```
生成

```java
//  This codes are generated automatically. Do not modify!
package com.xining.annotation;

import com.xining.sample2.Book;
import java.lang.IllegalAccessException;
import java.lang.NoSuchFieldException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import javax.validation.constraints.NotNull;

public class PersonBuilder implements InvocationHandler {
  private HashMap<String, Object> fieldMap = new HashMap();

  @NotNull(
      message = "name should not be null"
  )
  private String name;

  @NotNull(
      message = "age should not be null"
  )
  private int age;

  @NotNull(
      message = "male should not be null"
  )
  private boolean male;

  private List<Book> books;

  private PersonBuilder() {
  }

  public static PersonBuilder of() {
    return new PersonBuilder();
  }

  public static PersonBuilder of(Person t) {
    return new PersonBuilder().name(t.name()).age(t.age()).male(t.male()).books(t.books());
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {
    String methodName = method.getName();
    Object value = fieldMap.get(methodName);
    if(value == null) {
      Field field = this.getClass().getDeclaredField(methodName);
      field.setAccessible(true);
      fieldMap.put(methodName, value = field.get(this));
    }
    return value;
  }

  PersonBuilder name(String name) {
    this.name = name;
    return this;
  }

  PersonBuilder age(int age) {
    this.age = age;
    return this;
  }

  PersonBuilder male(boolean male) {
    this.male = male;
    return this;
  }

  PersonBuilder books(List<Book> books) {
    this.books = books;
    return this;
  }

  Person build() {
    ValidatorUtil.validate(this);
    return ProxyUtil.proxy(this,Person.class);
  }
}

```
