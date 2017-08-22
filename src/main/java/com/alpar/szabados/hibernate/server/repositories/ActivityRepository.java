package com.alpar.szabados.hibernate.server.repositories;

import com.alpar.szabados.hibernate.server.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findActivitiesByUserId(long userId);

    Activity findActivityByActivityNameAndUserIdAndActivityDate(String activityName, long userId, String date);
}
