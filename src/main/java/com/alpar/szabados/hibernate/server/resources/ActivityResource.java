package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.entities.TaskStatus;
import com.alpar.szabados.hibernate.server.entities.User;
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
import static java.time.format.DateTimeFormatter.ISO_DATE;

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
        User user = userRepository.findUserByUserName(userName);
        List<Activity> activityList = activityRepository.findActivitiesByUserId(user.getUserId());
        if (activityList.isEmpty()) {
            return Response.serverError().entity("Could not find user").build();
        } else {
            return Response.ok(activityList).build();
        }
    }

    @PUT
    @Path("/createActivity/{activityName}.{taskStatus}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createActivity(@PathParam("activityName") String activityName, @PathParam("taskStatus") TaskStatus taskStatus, @PathParam("userName") String userName) {
        try {
            User user = userRepository.findUserByUserName(userName);
            Activity activity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, user.getUserId(), now);
            if (activity == null) {
                Activity newActivity = new Activity();
                newActivity.setActivityDate(now);
                newActivity.setActivityName(activityName);
                newActivity.setTaskStatus(taskStatus);
                newActivity.setUserId(user.getUserId());
                activityRepository.save(newActivity);
                return Response.ok(activityRepository.findActivitiesByUserId(user.getUserId())).build();
            } else {
                return Response.ok("Activity already created").build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error creating activity" + e).build();
        }
    }

    @POST
    @Path("/completeTask/{activity}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeTask(@PathParam("activity") String activityName, @PathParam("userName") String userName) {
        try {
            User user = userRepository.findUserByUserName(userName);

            Activity activity = getActivity(activityName, user);
            activity.setTaskStatus(COMPLETED);
            activityRepository.save(activity);
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error updating the activity: " + e).build();
        }
        return Response.ok().build();
    }

    private Activity getActivity(String activityName, User user) {
        Activity activity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, user.getUserId(), now);
        if (activity == null) {
            activity = new Activity();
            activity.setActivityName(activityName);
            activity.setUserId(user.getUserId());
            activity.setActivityDate(now);
        }
        return activity;
    }
}