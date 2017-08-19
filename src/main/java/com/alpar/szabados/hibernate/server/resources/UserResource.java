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
import java.util.Objects;

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
    public Response validate(User response) {
        try {
            User user = userRepository.findByUserName(response.getUserName());
            if (user == null) {
                return Response.status(BAD_REQUEST).entity("USER NOT FOUND").build();
            } else if (!isValid(response, user)) {
                return Response.status(UNAUTHORIZED).entity("WRONG PASSWORD").build();
            } else {
                return Response.ok().build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }

    private boolean isValid(User response, User user) {
        return Objects.equals(user.getUserName(), response.getUserName())
                && ENCODER.matches(response.getPassword(), user.getPassword());
    }

    @PUT
    @Path("/createUser/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(User response) {
        try {
            User existingUser = userRepository.findUserByUserName(response.getUserName());
            if (existingUser != null) {
                return Response.status(BAD_REQUEST).entity("USER ALREADY EXISTS").build();
            } else if (response.getPassword().isEmpty()) {
                return Response.status(UNAUTHORIZED).entity("INVALID PASSWORD").build();
            } else {
                userRepository.save(response);
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
            User user = userRepository.findByUserName(response.getUserName());
            if (user == null) {
                return Response.status(BAD_REQUEST).entity("USERNAME DOES NOT MATCH").build();
            } else if (!isValid(response, user)) {
                return Response.status(UNAUTHORIZED).entity("INVALID PASSWORD").build();
            } else {
                userRepository.delete(user);
                return Response.ok().build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }

    @POST
    @Path("/updateUserPassword/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserPassword(User response) {
        try {
            User user = userRepository.findByUserName(response.getUserName());
            if (user == null) {
                return Response.status(BAD_REQUEST).entity("USER NOT FOUND").build();
            } else if (!isValid(response, user)){
                return Response.status(UNAUTHORIZED).entity("INVALID PASSWORD").build();
            } else {
                user.setPassword(response.getPassword());
                userRepository.save(user);
                return Response.ok(user).build();
            }
        } catch (RuntimeException e) {
            return Response.serverError().entity("SERVER ERROR OCCURRED " + e).build();
        }
    }
}