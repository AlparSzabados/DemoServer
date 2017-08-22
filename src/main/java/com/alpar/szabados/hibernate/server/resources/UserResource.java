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
import javax.ws.rs.core.Response.Status;

import static com.alpar.szabados.hibernate.server.utils.ResponseFactory.*;
import static com.alpar.szabados.hibernate.server.utils.Responses.*;

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
            if (existingUser != null) {
                if (isValid(user, existingUser)) {
                    return responseOk();
                } else {
                    return responseAndMessage(WRONG_PASSWORD);
                }
            } else {
                return responseAndMessage(USER_NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return responseAndException(SERVER_ERROR, e);
        }
    }

    public boolean isValid(User response, User user) {
        return ENCODER.matches(response.getPassword(), user.getPassword());
    }

    @PUT
    @Path("/createUser/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null) {
                if (user.getPassword() != null) {
                    userRepository.save(user);
                    return responseOk();
                } else {
                    return responseAndMessage(INVALID_PASSWORD);
                }
            } else {
                return responseAndMessage(USER_ALREADY_EXISTS);
            }
        } catch (RuntimeException e) {
            return responseAndException(SERVER_ERROR, e);
        }
    }

    @DELETE
    @Path("/deleteUser/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(User user) {
        try {
            Response validate = validate(user);
            if (isOk(validate.getStatus())) {
                User existingUser = userRepository.findByUserName(user.getUserName());
                userRepository.delete(existingUser);
                return Response.ok().build();
            } else {
                return validate;
            }
        } catch (RuntimeException e) {
            return responseAndException(SERVER_ERROR, e);
        }
    }

    private boolean isOk(int status) {
        return status == Status.OK.getStatusCode();
    }

    @POST
    @Path("/updateUserPassword/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserPassword(User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser != null) {
                existingUser.setPassword(user.getPassword());
                userRepository.save(existingUser);
                return Response.ok().build();
            } else {
                return responseAndMessage(USER_NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return responseAndException(SERVER_ERROR, e);
        }
    }
}