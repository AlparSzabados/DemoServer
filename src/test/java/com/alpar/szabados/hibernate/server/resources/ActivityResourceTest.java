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
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.alpar.szabados.hibernate.server.entities.TaskStatus.NOT_COMPLETED;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ActivityResourceTest {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    private ActivityResource activityResource;
    private User dummyUser;
    private Activity dummyActivity;

    private static final String NOW = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);

    @Before
    public void setUp() throws Exception {
        activityResource = new ActivityResource(activityRepository, userRepository);

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        dummyUser = new User("Dummy", encoder.encode("Password"));
        userRepository.save(dummyUser);

        long dummyUserId = userRepository.findUserByUserName(dummyUser.getUserName()).getUserId();

        dummyActivity = new Activity();
        dummyActivity.setActivityName("Swimming");
        dummyActivity.setActivityDate(NOW);
        dummyActivity.setUserId(dummyUserId);
        dummyActivity.setTaskStatus(NOT_COMPLETED);

        activityRepository.save(dummyActivity);
    }

    @Test
    public void findActivities() {
        Response foundResponse = activityResource.findActivities(dummyUser);
        assertEquals(200, foundResponse.getStatus());

        User newUser = userRepository.save(new User("Jane_Doe", "Password"));

        Response notFoundResponse = activityResource.findActivities(newUser);
        assertEquals(400, notFoundResponse.getStatus());

        Response errorResponse = activityResource.findActivities(new User("John Doe", "qr2q"));
        assertEquals(500, errorResponse.getStatus());
    }

    @Test
    public void createActivity() {
        Activity newActivity = new Activity();
        newActivity.setActivityName("reading");
        UserAndActivityWrapper wrapper = new UserAndActivityWrapper(dummyUser, newActivity);

        Response successResponse = activityResource.createActivity(wrapper);
        assertEquals(200, successResponse.getStatus());

        User newUser = new User();
        newUser.setUserName("John Doe");
        wrapper.setActivity(dummyActivity);
        wrapper.setUser(newUser);
        Response failResponse = activityResource.createActivity(wrapper);
        assertEquals(500, failResponse.getStatus());
    }
}