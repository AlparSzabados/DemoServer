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
        String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            Activity activity = new Activity();
            activity.setActivityDate(localDate);
            activity.setActivityName(activityName);
            activity.setTaskStatus(taskStatus);
            activity.setUserId(user.getUserId());
            activityRepository.save(activity);
        } catch (Exception e) {
            return Response.status(500).entity("Error creating user" + e.toString()).build();
        }
        return Response.status(200).entity(activityRepository.findActivitiesByUserId(user.getUserId())).build();
    }

    @POST
    @Path("/completeTask/{activity}.{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeTask(@PathParam("activity") String activityName, @PathParam("userName") String userName) {
        User user = userRepository.findUserByUserName(userName);
        try {
            Activity activity = activityRepository.findActivityByActivityNameAndUserId(activityName, user.getUserId());
            Activity activity1 = new Activity();
            activity1.setUserId(activity.getUserId());
            activity1.setActivityName(activity.getActivityName());
            activity1.setActivityDate(activity.getActivityDate());
            activity1.setId(activity.getId());
            activity1.setTaskStatus(COMPLETED);
            activityRepository.save(activity1);
        } catch (Exception e) {
            return Response.status(500).entity("Error updating the activity: " + e.toString()).build();
        }
        return Response.ok().build();
    }
}