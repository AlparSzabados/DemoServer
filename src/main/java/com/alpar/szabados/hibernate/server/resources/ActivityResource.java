package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.Activity;
import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.entities.UserAndActivityWrapper;
import com.alpar.szabados.hibernate.server.repositories.ActivityRepository;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static com.alpar.szabados.hibernate.server.utils.ResponseFactory.*;
import static com.alpar.szabados.hibernate.server.utils.Responses.*;
import static java.time.format.DateTimeFormatter.ISO_DATE;

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

    @POST
    @Path("/findActivities/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findActivities(User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser != null) {
                List<Activity> activityList = getActivitiesByUserId(getUserId(user.getUserName()));
                return responseOkAndEntity(OK, activityList);
            } else {
                return responseAndMessage(USER_NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return responseAndException(SERVER_ERROR, e);
        }
    }

    @POST
    @Path("/createOrUpdateActivity/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdateActivity(UserAndActivityWrapper wrapper) {
        try {
            User user = wrapper.getUser();
            Activity activity = wrapper.getActivity();

            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser != null) {
                Activity newActivity = getOrCreateActivity(activity, user);
                newActivity.setTaskStatus(activity.getTaskStatus());

                activityRepository.save(newActivity);
                return responseOk();
            } else {
                return responseAndMessage(USER_NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return responseAndException(SERVER_ERROR, e);
        }
    }

    private Activity getOrCreateActivity(Activity activity, User user) {
        String activityName = activity.getActivityName();
        long userId = getUserId(user.getUserName());
        String now = LocalDateTime.now().format(ISO_DATE);

        Activity existingActivity = activityRepository.findActivityByActivityNameAndUserIdAndActivityDate(activityName, userId, now);
        if (existingActivity == null) {
            existingActivity = new Activity(userId, activityName, now, null);
        }
        return existingActivity;
    }

    @DELETE
    @Path("/deleteUserActivities/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUserActivities(User user) {
        try {
            Response validate = new UserResource(userRepository).validate(user);
            if (validate.getStatus() == 200) {
                List<Activity> activities = getActivitiesByUserId(getUserId(user.getUserName()));
                activityRepository.delete(activities);
                return responseOk();
            } else {
                return validate;
            }
        } catch (RuntimeException e) {
            return responseAndException(SERVER_ERROR, e);
        }
    }

    private List<Activity> getActivitiesByUserId(long id) {
        return activityRepository.findActivitiesByUserId(id);
    }

    private long getUserId(String userName) {
        return userRepository.findByUserName(userName).getUserId();
    }
}