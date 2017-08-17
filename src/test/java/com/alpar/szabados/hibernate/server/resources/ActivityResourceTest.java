package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.repositories.ActivityRepository;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

        dummyUser = new User();
        dummyUser.setUserName("Dummy");
        dummyUser.setPassword("Password");

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
        Response foundResponse = activityResource.findActivities(dummyUser.getUserName());
        assertEquals(200, foundResponse.getStatus());

        User newUser = userRepository.save(new User("Jane_Doe", "Password"));

        Response notFoundResponse = activityResource.findActivities(newUser.getUserName());
        assertEquals(400, notFoundResponse.getStatus());

        Response errorResponse = activityResource.findActivities("John_Doe");
        assertEquals(500, errorResponse.getStatus());
    }

    @Test
    public void createActivity() {
        Response successResponse = activityResource.createActivity("reading", dummyUser.getUserName());
        assertEquals(200, successResponse.getStatus());

        Response failResponse = activityResource.createActivity("reading", "John_Doe");
        assertEquals(500, failResponse.getStatus());
    }

    @Test
    public void completeTask() {
        Response completeTaskResponse = activityResource.completeTask("swimming", dummyUser.getUserName());
        assertEquals(200, completeTaskResponse.getStatus());

        Response createAndCompleteTaskResponse = activityResource.createActivity("reading", dummyUser.getUserName());
        assertEquals(200, createAndCompleteTaskResponse.getStatus());

        Response failResponse = activityResource.createActivity("swimming", "John_Doe");
        assertEquals(500, failResponse.getStatus());
    }
}