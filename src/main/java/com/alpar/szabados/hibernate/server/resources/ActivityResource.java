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
import java.util.Objects;

import static com.alpar.szabados.hibernate.server.entities.TaskStatus.COMPLETED;

@Component
@Path("/activity")
public class ActivityResource {
    public static final String localDate = "2017-07-16";
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @GET
    @Path("/findActivities/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findActivities(@PathParam("userName") String userName) {
        User user = userRepository.findUserByUserName(userName);
        List<Activity> activityList = activityRepository.findActivitiesByUserId(user.getUserId());
        if (activityList.size() == 0) {
            return Response.status(500).entity("Could not find user").build();
        } else {
            return Response.status(200).entity(activityList).build();
        }
    }

    @PUT
    @Path("/createActivity/{activityName}.{taskStatus}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createActivity(@PathParam("activityName") String activityName, @PathParam("taskStatus") TaskStatus taskStatus, @PathParam("userName") String userName) {
        User user = userRepository.findUserByUserName(userName);
//        String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            Activity activity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, user.getUserId(), localDate);
            if (activity == null) {
                Activity newActivity = new Activity();
                newActivity.setActivityDate(localDate);
                newActivity.setActivityName(activityName);
                newActivity.setTaskStatus(taskStatus);
                newActivity.setUserId(user.getUserId());
                activityRepository.save(newActivity);
            } else {
                return Response.status(200).entity("Activity already created").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("Error creating activity" + e.toString()).build();
        }
        return Response.status(200).entity(activityRepository.findActivitiesByUserId(user.getUserId())).build();
    }

    @POST
    @Path("/completeTask/{activity}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeTask(@PathParam("activity") String activityName, @PathParam("userName") String userName) {
        User user = userRepository.findUserByUserName(userName);
//        String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
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
        } catch (Exception e) {
            return Response.status(500).entity("Error updating the activity: " + e.toString()).build();
        }
        return Response.ok().build();
    }
}