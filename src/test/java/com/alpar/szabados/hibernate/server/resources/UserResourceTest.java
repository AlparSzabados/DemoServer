package com.alpar.szabados.hibernate.server.resources;

import com.alpar.szabados.hibernate.server.entities.User;
import com.alpar.szabados.hibernate.server.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserResourceTest {
    @Autowired
    private UserRepository userRepository;
    private UserResource userResource;

    private User dummyUser;

    @Before
    public void setUp() throws Exception {
        userResource = new UserResource(userRepository);

        dummyUser = new User();
        dummyUser.setUserName("Dummy");
        dummyUser.setPassword("Password");

        try {
            userRepository.save(dummyUser);
        } catch (RuntimeException e) {
            System.out.println("An exception occurred " + e);
        }
    }

    @Test
    public void validate() throws Exception {
        Response validResponse = userResource.validate(dummyUser.getUserName(), dummyUser.getPassword());
        assertEquals(200, validResponse.getStatus());

        Response notValidPasswordResponse = userResource.validate(dummyUser.getUserName(), "1245");
        assertEquals(400, notValidPasswordResponse.getStatus());

        Response notValidUserNameResponse = userResource.validate("John_Doe", dummyUser.getPassword());
        assertEquals(500, notValidUserNameResponse.getStatus());
    }

    @Test
    public void create() throws Exception {
        Response createResponse = userResource.create("Dummy1", "Password");
        assertEquals(200, createResponse.getStatus());

        Response duplicateResponse = userResource.create(dummyUser.getUserName(), dummyUser.getPassword());
        assertEquals(400, duplicateResponse.getStatus());
    }

    @Test
    public void delete() throws Exception {
        Response deleteResponse = userResource.delete(dummyUser.getUserId());
        assertEquals(200, deleteResponse.getStatus());

        Response cantDeleteResponse = userResource.delete(Long.MAX_VALUE);
        assertEquals(400, cantDeleteResponse.getStatus());
    }

    @Test
    public void findUserByName() throws Exception {
        Response foundRequest = userResource.findUserByName(dummyUser.getUserName());
        assertEquals(200, foundRequest.getStatus());

        Response notFoundRequest = userResource.findUserByName("John_Doe");
        assertEquals(400, notFoundRequest.getStatus());
    }

    @Test
    public void updateUserPassword() throws Exception {
        Response updatePasswordResponse = userResource.updateUserPassword(dummyUser.getUserName(), "12345");
        assertEquals(200, updatePasswordResponse.getStatus());
        assertEquals("12345", ((User) updatePasswordResponse.getEntity()).getPassword());

        Response userNotFoundResponse = userResource.updateUserPassword("John_Doe", "12345");
        assertEquals(400, userNotFoundResponse.getStatus());
    }
}