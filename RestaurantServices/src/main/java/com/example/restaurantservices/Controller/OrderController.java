package com.example.restaurantservices.Controller;

import com.example.restaurantservices.Enum.OrderStatus;
import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Exception.NoAuthorizationException;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Model.Order;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.OrderRepository;
import com.example.restaurantservices.Repository.SellRepository;
import com.example.restaurantservices.Service.OrderService;
import com.example.restaurantservices.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SellRepository sellRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    UserService userService;


    @PostMapping("/menu/{menuId}/product")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Order> createOrder(@PathVariable Integer menuId, @RequestParam("productId") Integer productId, @RequestParam(value = "amount") Integer amount) throws ParseException {

        return new ResponseEntity<>(orderService.createOrder(menuId, productId, amount), HttpStatus.OK);
    }

    @PostMapping("{orderId}/menu/{menuId}/product")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Order> addProductToOrder(@PathVariable Integer orderId, @PathVariable Integer menuId, @RequestParam("productId") Integer productId, @RequestParam(value = "amount") Integer amount) throws ParseException {
        return new ResponseEntity<>(orderService.addProductToOrder(orderId, menuId, productId, amount), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CLIENT', 'RESTAURANT_MANAGER')")
    public ResponseEntity<?> getOrders() {
        User loggedUser = userService.getCurrentLoggedUser();
        if (loggedUser.getRole().equals(Role.CLIENT)) {
            return new ResponseEntity<>(loggedUser.getOrders(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(loggedUser.getRestaurant().getOrders(), HttpStatus.OK);
        }

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'RESTAURANT_MANAGER')")
    public ResponseEntity<?> getOrder(@PathVariable Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();

        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order with id: " + id + " does not exist");
        } else {
            Order order = orderOptional.get();
            if (loggedUser.getRole().equals(Role.CLIENT)) {
                if (!loggedUser.getOrders().contains(order)) {
                    throw new NoAuthorizationException("This order can not be accessed by this user");
                }
                return new ResponseEntity<>(order, HttpStatus.OK);
            } else {
                if (!loggedUser.getRestaurant().getOrders().contains(order)) {
                    throw new NoAuthorizationException("This order can not be accessed by this user");
                }
                return new ResponseEntity<>(order, HttpStatus.OK);
            }
        }

    }


    @PostMapping("{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Order> updateStatus(@PathVariable Integer id, @RequestParam("status") String status) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order with id: " + id + " does not exist");
        } else {
            Order order = orderOptional.get();
            if (!loggedUser.getOrders().contains(order)) {
                throw new NoAuthorizationException("This order can not be accessed by this user");
            } else {
                status = status.toUpperCase(Locale.ROOT);
                OrderStatus orderStatus;
                try {
                    orderStatus = OrderStatus.valueOf(status);
                } catch (Exception e) {
                    throw new ResourceNotFoundException("This status: " + status + " does not exist");
                }
                order.setStatus(orderStatus);
                return new ResponseEntity<>(orderRepository.save(order), HttpStatus.OK);
            }

        }
    }

}
