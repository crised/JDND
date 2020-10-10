package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SareetaApplicationTests {

    private UserController userController;
    private CartController cartController;
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private static final String USERNAME = "crised";

    @Before
    public void setUp() {
        Logger logger = LoggerFactory.getLogger("splunk.logger");
        String[] events = new SareetaApplication().appLogEvents();

        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObjects(userController, "logger", logger);
        TestUtils.injectObjects(userController, "logEvents", events);

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "logger", logger);
        TestUtils.injectObjects(orderController, "logEvents", events);
    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode("password")).thenReturn("hashedText");
        ResponseEntity<User> responseEntity = create_user();
        assertNotNull(responseEntity);
        User user = responseEntity.getBody();
        assertEquals(0, user.getId());
        assertEquals(USERNAME, user.getUsername());
        assertEquals("hashedText", user.getPassword());
    }

    @Test
    public void create_user_sad_path() {
        CreateUserRequest fakeRequest = new CreateUserRequest();
        fakeRequest.setUsername(USERNAME);
        fakeRequest.setPassword("password");
        fakeRequest.setConfirmPassword("MISMATCH");
        ResponseEntity<User> responseEntity = userController.createUser(fakeRequest);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void addItemToCart() {
        User user = create_user_entity();
        Item item = create_item_entity();
        Cart cart = create_cart_entity(user, item);
        user.setCart(cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<Cart> cartResponseEntity = add_item();
        assertNotNull(cartResponseEntity);
        Cart cartResponse = cartResponseEntity.getBody();
        assertEquals(cartResponse.getItems().size(), 1);
        assertEquals(cartResponse.getItems().get(0).getName(), item.getName());
    }

    @Test
    public void OrderList() {
        User user = create_user_entity();
        Item item = create_item_entity();
        Cart cart = create_cart_entity(user, item);
        cart.addItem(item);
        user.setCart(cart);
        UserOrder fakeUserOrder = UserOrder.createFromCart(cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(fakeUserOrder));
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(USERNAME);
        assertNotNull(responseEntity);
        List<UserOrder> userOrders = responseEntity.getBody();
        assertNotNull(userOrders);
        assertEquals(userOrders.size(), 1);
        assertEquals(userOrders.get(0).getItems().get(0).getName(), item.getName());
    }

    @Test
    public void OrderSubmit(){
        User user = create_user_entity();
        Item item = create_item_entity();
        Cart cart = create_cart_entity(user, item);
        cart.addItem(item);
        user.setCart(cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit(USERNAME);
        assertNotNull(userOrderResponseEntity);
        UserOrder userOrder = userOrderResponseEntity.getBody();
        assertNotNull(userOrder);
        assertEquals(userOrder.getTotal(), cart.getTotal());
    }


    private ResponseEntity<Cart> add_item() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);
        return cartController.addTocart(modifyCartRequest);
    }

    private ResponseEntity<User> create_user() {
        CreateUserRequest fakeRequest = new CreateUserRequest();
        fakeRequest.setUsername(USERNAME);
        fakeRequest.setPassword("password");
        fakeRequest.setConfirmPassword("password");
        return userController.createUser(fakeRequest);
    }

    private User create_user_entity() {
        User user = new User();
        user.setId(1);
        user.setUsername(USERNAME);
        user.setPassword("hashedPassword");
        return user;
    }

    private Item create_item_entity() {
        Item item = new Item();
        item.setId(1L);
        item.setDescription("red plastic");
        item.setName("Arrow");
        item.setPrice(BigDecimal.TEN);
        return item;
    }

    private Cart create_cart_entity(User user, Item item) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
//        cart.addItem(item);
        return cart;
    }


//    /*
//    This method was created when testing splunk logging.
//     */
//    @Test
//    @Ignore
//    public void createExampleLogSplunkEvents() throws InterruptedException {
//        Logger logger = LoggerFactory.getLogger("splunk.logger");
//        logger.info("This is a test");
//        final String[] event_names = {"event_success", "event_failure"};
//        final String[] event_ids = {"0", "1"};
//        Random random = new Random();
//        int max = 1;
//        int min = 0;
//        for (int i = 0; i < 50; i++) {
//            Thread.sleep(300);
//            int x = random.nextInt(max - min + 1) + min;
//            SplunkCimLogEvent event = new SplunkCimLogEvent(event_names[x], event_ids[x]);
//            logger.info(event.toString());
//        }
//    }

}
