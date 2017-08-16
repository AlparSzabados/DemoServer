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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.alpar.szabados.hibernate.server.entities.TaskStatus.COMPLETED;

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

    @GET
    @Path("/findActivities/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findActivities(@PathParam("userName") String userName) {
        User user = userRepository.findUserByUserName(userName);
        List<Activity> activityList = activityRepository.findActivitiesByUserId(user.getUserId());
        if (activityList.size() == 0) {
            return Response.serverError().entity("Could not find user").build();
        } else {
            return Response.ok().entity(activityList).build();
        }
    }

    @PUT
    @Path("/createActivity/{activityName}.{taskStatus}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createActivity(@PathParam("activityName") String activityName, @PathParam("taskStatus") TaskStatus taskStatus, @PathParam("userName") String userName) {
        try {
            User user = userRepository.findUserByUserName(userName);
            String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // TODO extract
            Activity activity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, user.getUserId(), localDate);
            if (activity == null) {
                Activity newActivity = new Activity();
                newActivity.setActivityDate(localDate);
                newActivity.setActivityName(activityName);
                newActivity.setTaskStatus(taskStatus);
                newActivity.setUserId(user.getUserId());
                activityRepository.save(newActivity);
                return Response.ok().entity(activityRepository.findActivitiesByUserId(user.getUserId())).build();
            } else {
                return Response.ok().entity("Activity already created").build();
            }
        } catch (Exception e) {
            return Response.serverError().entity("Error creating activity" + e.toString()).build();
        }
    }

    @POST
    @Path("/completeTask/{activity}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeTask(@PathParam("activity") String activityName, @PathParam("userName") String userName) {
        try {
            User user = userRepository.findUserByUserName(userName);
            String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Activity activity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, user.getUserId(), localDate);
            if (activity == null) {
                Activity newActivity = new Activity();
                newActivity.setActivityName(activityName);
                newActivity.setUserId(user.getUserId());
                newActivity.setActivityDate(localDate);
                newActivity.setTaskStatus(COMPLETED);
                activityRepository.save(newActivity);
            } else {
                activity.setTaskStatus(COMPLETED);
                activityRepository.save(activity);
            }
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity("Error updating the activity: " + e.toString()).build();
        }
    }
}