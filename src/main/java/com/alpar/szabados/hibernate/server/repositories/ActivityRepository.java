package com.alpar.szabados.hibernate.server.repositories;

import com.alpar.szabados.hibernate.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<User, Long> {
}
