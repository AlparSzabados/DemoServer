package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/user")
public class UserResource {
    private final UserRepository userRepository;

    @Autowired
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GET
    @Path("/validateUser/{userName}.{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@PathParam("userName") String userName, @PathParam("password") String password) {
        List<User> userList = userRepository.findByUserNameAndPassword(userName, password);
        if (userList.size() != 1) {
            return Response.serverError().entity("Could not find user").build();
        } else {
            return Response.ok(userList.get(0)).build();
        }
    }

    @PUT
    @Path("/createUser/{userName}.{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("userName") String userName, @PathParam("password") String password) {
        try {
            User user = new User(userName, password);
            userRepository.save(user);
            return Response.ok(user).build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error creating user" + e).build();
        }
    }

    @DELETE
    @Path("/deleteUser/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("userId") long id) {
        try {
            User user = userRepository.findByUserId(id);
            userRepository.delete(user);
            return Response.ok().build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error deleting the user: " + e).build();
        }
    }

    @GET
    @Path("/findUserById/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserById(@PathParam("userId") long id) {
        try {
            User user = userRepository.findByUserId(id);
            return Response.ok(user).build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("User not found: " + e).build();
        }
    }

    @GET
    @Path("/findUserByName/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserById(@PathParam("userName") String userName) {
        try {
            User user = userRepository.findUserByUserName(userName);
            return Response.ok(user).build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("User not found: " + e).build();
        }
    }

    @POST
    @Path("/updateUserPassword/{userName}.{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserPassword(@PathParam("userName") String userName, @PathParam("password") String password) {
        try {
            User user = new User(userName, password);
            List<User> users = userRepository.findByUserNameAndPassword(userName, password);
            if (users.size() == 1) {
                User user1 = users.get(0);
                long userId = user1.getUserId();
                userRepository.findByUserId(userId).setPassword(userName);
                userRepository.findByUserId(userId).setPassword(password);
            } else {
                userRepository.save(user);
            }
            return Response.ok().build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error updating the user: " + e).build();
        }
    }
}