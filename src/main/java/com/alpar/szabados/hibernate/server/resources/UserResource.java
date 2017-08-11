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
    @Autowired
    private UserRepository userRepository;

    @GET
    @Path("/createUser/{userName}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("userName") String userName, @PathParam("password") String password) {
        try {
            User user = new User(userName, password);
            userRepository.save(user);
        } catch (Exception e) {
            return Response.status(500).entity("Error creating user" + e.toString()).build();
        }
        return Response.status(200).entity(userRepository.findByUserNameAndPassword(userName, password)).build();
    }

    @DELETE //TODO check why it's not allowed
    @Path("/deleteUser/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("userId")long id) {
        try {
            User user = userRepository.findByUserId(id);
            userRepository.delete(user);
        } catch (Exception e) {
            return Response.status(500).entity("Error deleting the user: " + e.toString()).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/findUser/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("userId")long id) {
        User user;
        try {
            user = userRepository.findByUserId(id);
        } catch (Exception e) {
            return Response.status(500).entity("User not found: " + e.toString()).build();
        }
        return Response.ok(user).build();
    }

    @POST //TODO check why it's not allowed
    @Path("/updateUser/{userName}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("userName") String userName, @PathParam("password") String password) {
        User user = new User(userName, password);
        try {
            List<User> users = userRepository.findByUserNameAndPassword(userName, password);
            if (users.size() == 1) {
                User user1 = users.get(0);
                long userId = user1.getUserId();
                userRepository.findByUserId(userId).setPassword(userName);
                userRepository.findByUserId(userId).setPassword(password);
            } else {
                userRepository.save(user);
            }
        } catch (Exception e) {
            return Response.status(500).entity("Error updating the user: " + e.toString()).build();
        }
        return Response.ok().build();
    }
}