package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.entities.TaskStatus;
import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.repositories.ActivityRepository;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Path("/activity")
public class ActivityResource {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @PUT
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

//    @DELETE
//    @Path("/deleteUser/{userId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response delete(@PathParam("userId") long id) {
//        try {
//            User user = activityRepository.findByUserId(id);
//            activityRepository.delete(user);
//        } catch (Exception e) {
//            return Response.status(500).entity("Error deleting the user: " + e.toString()).build();
//        }
//        return Response.ok().build();
//    }

//    @GET
//    @Path("/findUserById/{userId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response findUserById(@PathParam("userId") long id) {
//        User user;
//        try {
//            user = activityRepository.findByUserId(id);
//        } catch (Exception e) {
//            return Response.status(500).entity("User not found: " + e.toString()).build();
//        }
//        return Response.ok(user).build();
//    }

//    @GET
//    @Path("/findUserByName/{userName}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response findUserById(@PathParam("userName") String userName) {
//        User user;
//        try {
//            user = activityRepository.findUserByUserName(userName);
//        } catch (Exception e) {
//            return Response.status(500).entity("User not found: " + e.toString()).build();
//        }
//        return Response.status(200).entity(user).build();
//    }

//    @POST
//    @Path("/updateUserPassword/{userName}.{password}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateUserPassword(@PathParam("userName") String userName, @PathParam("password") String password) {
//        User user = new User(userName, password);
//        try {
//            List<User> users = activityRepository.findByUserNameAndPassword(userName, password);
//            if (users.size() == 1) {
//                User user1 = users.get(0);
//                long userId = user1.getUserId();
//                activityRepository.findByUserId(userId).setPassword(userName);
//                activityRepository.findByUserId(userId).setPassword(password);
//            } else {
//                activityRepository.save(user);
//            }
//        } catch (Exception e) {
//            return Response.status(500).entity("Error updating the user: " + e.toString()).build();
//        }
//        return Response.ok().build();
//    }
}