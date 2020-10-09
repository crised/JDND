package com.example.demo.controllers;

import static com.example.demo.LogTags.*;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private String[] logEvents;

    @Autowired
    private Logger logger;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        ResponseEntity<User> responseEntity = ResponseEntity.of(userRepository.findById(id));
        if (responseEntity.getStatusCode().isError())
            logger.error(logEvents[APP_EXCEPTION.ordinal()]);
        return responseEntity;
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) logger.error(logEvents[APP_EXCEPTION.ordinal()]);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            User user = new User();
            user.setUsername(createUserRequest.getUsername());
            Cart cart = new Cart();
            cartRepository.save(cart);
            user.setCart(cart);
            userRepository.save(user);
            logger.info(logEvents[CREATE_USER_SUCCESS.ordinal()]);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error(logEvents[CREATE_USER_FAILURE.ordinal()]);
            return ResponseEntity.badRequest().build();
        }

    }

}
