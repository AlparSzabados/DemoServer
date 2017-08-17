package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.repositories.ActivityRepository;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static com.alpar.szabados.hibernate.server.entities.TaskStatus.COMPLETED;
import static com.alpar.szabados.hibernate.server.entities.TaskStatus.NOT_COMPLETED;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Component
@Path("/activity")
public class ActivityResource {
    private final String now = LocalDateTime.now().format(ISO_DATE);

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Autowired
    public ActivityResource(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @GET
    @Path("/findActivities/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findActivities(@PathParam("userName") String userName) {
        try {
            long userId = userRepository.findUserByUserName(userName).getUserId();
            List<Activity> activityList = activityRepository.findActivitiesByUserId(userId);
            if (activityList.isEmpty()) {
                return Response.status(BAD_REQUEST).entity("Could not find any activities").build();
            } else {
                return Response.ok(activityList).build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error occurred" + e).build();
        }
    }

    @PUT
    @Path("/createActivity/{activityName}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createActivity(@PathParam("activityName") String activityName, @PathParam("userName") String userName) {
        try {
            long userId = userRepository.findUserByUserName(userName).getUserId();
            Activity activity = getActivity(activityName, userId);
            activity.setTaskStatus(NOT_COMPLETED);
            activityRepository.save(activity);
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error creating activity" + e).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/completeTask/{activity}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeTask(@PathParam("activity") String activityName, @PathParam("userName") String userName) {
        try {
            long userId = userRepository.findUserByUserName(userName).getUserId();
            Activity activity = getActivity(activityName, userId);
            activity.setTaskStatus(COMPLETED);
            activityRepository.save(activity);
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error updating the activity: " + e).build();
        }
        return Response.ok().build();
    }

    private Activity getActivity(String activityName, long userId) {
        Activity activity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, userId, now);
        if (activity == null) {
            activity = new Activity();
            activity.setActivityName(activityName);
            activity.setUserId(userId);
            activity.setActivityDate(now);
        }
        return activity;
    }
}