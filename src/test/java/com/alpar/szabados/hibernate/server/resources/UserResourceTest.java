package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@Component
@RunWith(SpringRunner.class)
public class UserResourceTest {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;
    private UserResource userResource;

    private User dummyUser;

    @Before
    public void setUp() throws Exception {
        userResource = new UserResource(userRepository);
        dummyUser = new User("UserName", ENCODER.encode("Password"));
        userRepository.save(dummyUser);
    }

    @Test
    public void validate() {
        Response validResponse = userResource.validate(new User("UserName", "Password"));
        assertEquals(200, validResponse.getStatus());

        Response notValidPasswordResponse = userResource.validate(new User("UserName", ENCODER.encode("Invalid Password")));
        assertEquals(401, notValidPasswordResponse.getStatus());

        Response notValidUserNameResponse = userResource.validate(new User("Invalid User", ENCODER.encode("Password")));
        assertEquals(400, notValidUserNameResponse.getStatus());
    }

    @Test
    public void create() {
        Response createResponse = userResource.create(new User("NewUser", ENCODER.encode("Password")));
        assertEquals(200, createResponse.getStatus());

        Response duplicateResponse = userResource.create(dummyUser);
        assertEquals(400, duplicateResponse.getStatus());
    }

    @Test
    public void delete() {
        Response deleteResponse = userResource.delete(dummyUser);
        assertEquals(200, deleteResponse.getStatus());

        Response cantDeleteResponse = userResource.delete(new User("Invalid User", ENCODER.encode("Password")));
        assertEquals(400, cantDeleteResponse.getStatus());
    }

    @Test
    public void updateUserPassword() {
        String encodedPassword = ENCODER.encode("New Password");

        dummyUser.setPassword(encodedPassword);
        Response updatePasswordResponse = userResource.updateUserPassword(dummyUser);

        assertEquals(200, updatePasswordResponse.getStatus());
        assertEquals(200, userResource.validate(new User("UserName", "New Password")).getStatus());

        Response userNotFoundResponse = userResource.updateUserPassword(new User("Invalid User", encodedPassword));
        assertEquals(400, userNotFoundResponse.getStatus());
    }
}