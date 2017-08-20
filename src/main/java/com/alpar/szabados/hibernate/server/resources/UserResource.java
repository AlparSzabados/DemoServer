package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Component
@Path("/user")
public class UserResource {
    private final UserRepository userRepository;
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @POST
    @Path("/validateUser/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validate(User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null) {
                return Response.status(BAD_REQUEST).entity("USER NOT FOUND").build();
            } else if (!isValid(user, existingUser)) {
                return Response.status(UNAUTHORIZED).entity("WRONG PASSWORD").build();
            } else {
                return Response.ok().build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }

    private boolean isValid(User response, User user) {
        return ENCODER.matches(response.getPassword(), user.getPassword());
    }

    @PUT
    @Path("/createUser/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser != null) {
                return Response.status(BAD_REQUEST).entity("USER ALREADY EXISTS").build();
            } else if (user.getPassword().isEmpty()) {
                return Response.status(UNAUTHORIZED).entity("INVALID PASSWORD").build();
            } else {
                userRepository.save(user);
                return Response.ok().build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }

    @DELETE
    @Path("/deleteUser/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(User response) {
        try {
            userRepository.delete(userRepository.findByUserName(response.getUserName()));
            return Response.ok().build();
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }

    @POST
    @Path("/updateUserPassword/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserPassword(User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null) {
                return Response.status(BAD_REQUEST).entity("USER NOT FOUND").build();
            } else {
                existingUser.setPassword(user.getPassword());
                userRepository.save(existingUser);
                return Response.ok(existingUser).build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }
}