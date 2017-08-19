package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.entities.UserAndActivityWrapper;
import com.alpar.szabados.hibernate.server.repositories.ActivityRepository;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Component
@Path("/activity")
public class ActivityResource {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Autowired
    public ActivityResource(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @POST
    @Path("/findActivities/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findActivities(User response) {
        try {
            long userId = userRepository.findUserByUserName(response.getUserName()).getUserId();
            List<Activity> activityList = activityRepository.findActivitiesByUserId(userId);
            if (activityList.size() > 0) {
                return Response.ok(activityList).build();
            } else {
                return Response.status(BAD_REQUEST).entity("Could not find any activities").build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error occurred" + e).build();
        }
    }

    @POST
    @Path("/createOrUpdateActivity/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdateActivity(UserAndActivityWrapper wrapper) {
        try {
            User userResponse = wrapper.getUser();
            Activity activityResponse = wrapper.getActivity();
            long userId = userRepository.findUserByUserName(userResponse.getUserName()).getUserId();

            Activity activity = getOrCreateActivity(activityResponse.getActivityName(), userId, getCurrentTime());
            activity.setTaskStatus(activityResponse.getTaskStatus());

            activityRepository.save(activity);
            return Response.ok(activity).build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error creating activity" + e).build();
        }
    }

    private Activity getOrCreateActivity(String activityName, long userId, String now) {
        Activity activity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, userId, now);
        if (activity == null) {
            activity = new Activity();
            activity.setActivityName(activityName);
            activity.setUserId(userId);
            activity.setActivityDate(now);
        }
        return activity;
    }

    private static String getCurrentTime() {
        return LocalDateTime.now().format(ISO_DATE);
    }
}