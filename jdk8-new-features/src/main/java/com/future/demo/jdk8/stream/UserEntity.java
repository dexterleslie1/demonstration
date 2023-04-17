package com.future.demo.jdk8.stream;

public class UserEntity {
    private String name;
    private int age;

    public UserEntity(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(!(obj instanceof UserEntity)) {
            return false;
        }

        UserEntity userEntityTemp = (UserEntity)obj;
        return userEntityTemp.getAge() == this.age && userEntityTemp.getName().equals(this.name);
    }
}
