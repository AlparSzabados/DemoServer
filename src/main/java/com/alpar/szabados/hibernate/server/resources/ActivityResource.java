package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.entities.UserAndActivityWrapper;
import com.alpar.szabados.hibernate.server.repositories.ActivityRepository;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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
    public Response findActivities(User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null) {
                return Response.status(BAD_REQUEST).entity("USER NOT FOUND").build();
            } else {
                List<Activity> activityList = activityRepository.findActivitiesByUserId(existingUser.getUserId());
                if (activityList.isEmpty()) {
                    return Response.status(BAD_REQUEST).entity("ACTIVITIES NOT FOUND").build();
                } else {
                    return Response.ok(activityList).build();
                }
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
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

            User existingUser = userRepository.findByUserName(userResponse.getUserName());
            if (existingUser == null) {
                return Response.status(BAD_REQUEST).entity("USER NOT FOUND").build();
            } else {
                Activity activity = getOrCreateActivity(activityResponse.getActivityName(), existingUser.getUserId(), getCurrentTime());
                activity.setTaskStatus(activityResponse.getTaskStatus());

                activityRepository.save(activity);
                return Response.ok().build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
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

    @DELETE
    @Path("/deleteUserActivities/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUserActivities(User user) {
        try {
            long userId = userRepository.findByUserName(user.getUserName()).getUserId();
            List<Activity> activities = activityRepository.findActivitiesByUserId(userId);
            if (activities.size() > 0) {
                activityRepository.delete(activities);
            }
            return Response.ok().build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }

    private static String getCurrentTime() {
        return LocalDateTime.now().format(ISO_DATE);
    }
}