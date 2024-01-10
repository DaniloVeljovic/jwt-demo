package de.dveljovic.jwtdemo.repository;

import de.dveljovic.jwtdemo.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public User findUserByEmail(final String email) {
        return new User(email, "dummyPassword", "Test", "Test");
    }
}
