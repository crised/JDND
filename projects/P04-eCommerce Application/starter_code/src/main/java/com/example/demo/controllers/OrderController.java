package com.example.demo.controllers;

import java.util.List;

import com.example.demo.AppException;
import com.example.demo.LogTags;
import com.splunk.logging.SplunkCimLogEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import static com.example.demo.LogTags.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private String[] logEvents;

    @Autowired
    private Logger logger;


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                logger.error(logEvents[ORDER_REQUEST_FAILURE.ordinal()]);
                return ResponseEntity.notFound().build();
            }
            UserOrder order = UserOrder.createFromCart(user.getCart());
            orderRepository.save(order);
            logger.info(logEvents[ORDER_REQUEST_SUCCESS.ordinal()]);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error(logEvents[ORDER_REQUEST_FAILURE.ordinal()]);
            return ResponseEntity.unprocessableEntity().build();
        }

    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(orderRepository.findByUser(user));
        } catch (Exception e) {
            logger.error(logEvents[APP_EXCEPTION.ordinal()]);
            throw new AppException();
        }

    }
}
