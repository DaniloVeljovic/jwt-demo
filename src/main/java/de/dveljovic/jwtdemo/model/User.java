package de.dveljovic.jwtdemo.model;

public record User(String email, String password,
                   String firstName, String lastName) {

}
