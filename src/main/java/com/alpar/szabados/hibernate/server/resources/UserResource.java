package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

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
        try {
            User user = userRepository.findByUserName(userName);
            if (Objects.equals(user.getUserName(), userName) && Objects.equals(user.getPassword(), password)) {
                return Response.ok(user).build();
            } else {
                return Response.status(BAD_REQUEST).entity("Wrong password").build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Could not find user" + e).build();
        }
    }

    @PUT
    @Path("/createUser/{userName}.{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("userName") String userName, @PathParam("password") String password) {
        try {
            User existingUser = userRepository.findUserByUserName(userName);
            if (existingUser == null) {
                User newUser = new User(userName, password);
                userRepository.save(newUser);
                return Response.ok(newUser).build();
            } else {
                return Response.status(BAD_REQUEST).entity("User already created").build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error occurred" + e).build();
        }
    }

    @DELETE
    @Path("/deleteUser/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("userId") long id) {
        try {
            User user = userRepository.findByUserId(id);
            if (user != null){
                userRepository.delete(user);
                return Response.ok().build();
            }else {
                return Response.status(BAD_REQUEST).entity("Cant find UserId").build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error deleting the user: " + e).build();
        }
    }

    @GET
    @Path("/findUserByName/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserByName(@PathParam("userName") String userName) {
        try {
            User user = userRepository.findUserByUserName(userName);
            if (user != null) {
                return Response.ok(user).build();
            } else {
                return Response.status(BAD_REQUEST).entity("User not Found").build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error occurred: " + e).build();
        }
    }

    @POST
    @Path("/updateUserPassword/{userName}.{newPassword}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserPassword(@PathParam("userName") String userName, @PathParam("newPassword") String newPassword) {
        try {
            User user = userRepository.findByUserName(userName);
            if (user != null) {
                user.setPassword(newPassword);
                userRepository.save(user);
                return Response.ok(user).build();
            } else {
                return Response.status(BAD_REQUEST).entity("User not Found").build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("Error occurred: " + e).build();
        }
    }
}