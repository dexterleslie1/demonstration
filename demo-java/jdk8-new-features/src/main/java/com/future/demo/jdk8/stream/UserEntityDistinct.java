package com.future.demo.jdk8.stream;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class UserEntityDistinct {
    private String name;
    private int age;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntityDistinct o1 = (UserEntityDistinct) o;
        return Objects.equals(age, o1.age) && Objects.equals(name, o1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.age);
    }

    private List<NestedClass> myList;

    @Data
    @AllArgsConstructor
    public static class NestedClass {
        private Long id;
    }
}
