package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.entities.UserAndActivityWrapper;
import com.alpar.szabados.hibernate.server.repositories.ActivityRepository;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.alpar.szabados.hibernate.server.entities.TaskStatus.NOT_COMPLETED;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@Component
@RunWith(SpringRunner.class)
public class ActivityResourceTest {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    private ActivityResource activityResource;
    private User dummyUser;
    private Activity dummyActivity;

    private static final String NOW = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Before
    public void setUp() throws Exception {
        activityResource = new ActivityResource(activityRepository, userRepository);

        dummyUser = new User("UserName", ENCODER.encode("Password"));
        userRepository.save(dummyUser);

        long dummyUserId = userRepository.findByUserName(dummyUser.getUserName()).getUserId();

        dummyActivity = new Activity(dummyUserId, "Dummy Activity", NOW, NOT_COMPLETED);
        activityRepository.save(dummyActivity);
    }

    @Test
    public void findActivities() {
        Response foundResponse = activityResource.findActivities(dummyUser);
        assertEquals(200, foundResponse.getStatus());

        User newUser = userRepository.save(new User("New User", ENCODER.encode("Password")));

        Response notFoundResponse = activityResource.findActivities(newUser);
        assertEquals(400, notFoundResponse.getStatus());

        Response errorResponse = activityResource.findActivities(new User("Invalid User", ENCODER.encode("Password")));
        assertEquals(400, errorResponse.getStatus());
    }

    @Test
    public void createActivity() {
        UserAndActivityWrapper wrapper = new UserAndActivityWrapper(dummyUser, new Activity("Dummy Activity"));
        assertEquals(200, activityResource.createOrUpdateActivity(wrapper).getStatus());

        wrapper.setActivity(dummyActivity);
        wrapper.setUser(new User("Invalid User"));
        assertEquals(400, activityResource.createOrUpdateActivity(wrapper).getStatus());
    }

    @Test
    public void deleteActivity() {
        assertEquals(200, activityResource.deleteUserActivities(dummyUser).getStatus());
    }
}