package com.alpar.szabados.hibernate.server.repositories;

import com.alpar.szabados.hibernate.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
    User findByUserId(long id);
    User findUserByUserName(String name);
}
