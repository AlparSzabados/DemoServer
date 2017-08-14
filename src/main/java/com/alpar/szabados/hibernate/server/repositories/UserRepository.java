package com.alpar.szabados.hibernate.server.repositories;

import com.alpar.szabados.hibernate.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserNameAndPassword(String userName, String password);

    User findByUserId(long id);

    User findUserByUserName(String name);
}
