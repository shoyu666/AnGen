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
    @NotNull
    String name();
    @NotNull
    int age();
    @NotNull
    boolean male();
    @NotNull
    List<Book> books();
}
```
生成

```java
public class PersonBuilder implements InvocationHandler {
  private String name;

  private int age;

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
  public Object invoke(Object proxy, Method method, Object[] args) {
    String methodName = method.getName();;
    if (methodName.equals("name")) {return name;};
    if (methodName.equals("age")) {return age;};
    if (methodName.equals("male")) {return male;};
    if (methodName.equals("books")) {return books;};
    return null;
  }

  @NotNull
  PersonBuilder name(String name) {
    this.name = name;
    return this;
  }

  @NotNull
  PersonBuilder age(int age) {
    this.age = age;
    return this;
  }

  @NotNull
  PersonBuilder male(boolean male) {
    this.male = male;
    return this;
  }

  @NotNull
  PersonBuilder books(List<Book> books) {
    this.books = books;
    return this;
  }

  Person build() {
    return ProxyUtil.proxy(this,Person.class);
  }
}
```
