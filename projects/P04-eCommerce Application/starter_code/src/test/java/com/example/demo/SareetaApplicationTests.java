package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.splunk.logging.SplunkCimLogEvent;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SareetaApplicationTests {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        Logger logger = LoggerFactory.getLogger("splunk.logger");
        String[] events = new SareetaApplication().appLogEvents();
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObjects(userController, "logger", logger);
        TestUtils.injectObjects(userController, "logEvents", events);

    }

    @Test
    public void create_user_happy_path() {
        CreateUserRequest fakeRequest = new CreateUserRequest();
        fakeRequest.setUsername("crised");
        fakeRequest.setPassword("password");
        fakeRequest.setConfirmPassword("password");
        ResponseEntity<User>  responseEntity = userController.createUser(fakeRequest);

        assertNotNull(responseEntity);


    }


    @Test
    @Ignore
    public void createExampleLogSplunkEvents() throws InterruptedException {
        Logger logger = LoggerFactory.getLogger("splunk.logger");
        logger.info("This is a test");
        final String[] event_names = {"event_success", "event_failure"};
        final String[] event_ids = {"0", "1"};
        Random random = new Random();
        int max = 1;
        int min = 0;
        for (int i = 0; i < 50; i++) {
            Thread.sleep(300);
            int x = random.nextInt(max - min + 1) + min;
            SplunkCimLogEvent event = new SplunkCimLogEvent(event_names[x], event_ids[x]);
            logger.info(event.toString());
        }
    }

}
