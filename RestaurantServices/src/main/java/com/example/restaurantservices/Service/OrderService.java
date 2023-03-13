package com.example.restaurantservices.Service;

import com.example.restaurantservices.Enum.OrderStatus;
import com.example.restaurantservices.Exception.NoAuthorizationException;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Exception.TimeError;
import com.example.restaurantservices.Model.*;
import com.example.restaurantservices.Repository.MenuRepository;
import com.example.restaurantservices.Repository.OrderRepository;
import com.example.restaurantservices.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    //    @Autowired
//    private SellRepository sellRepository;
//
//    @Autowired
//    private UserRepository userRepository;
    private DateFormat formatter = new SimpleDateFormat("hh:mm aa");

    @Autowired
    private UserService userService;

    public Order createOrder(Integer menuId, Integer productId, Integer amount) throws ParseException {

        User client = userService.getCurrentLoggedUser();
        Optional<Menu> menuOptional = menuRepository.findById(menuId);
        Optional<Product> productOptional = productRepository.findById(productId);


        if (menuOptional.isEmpty()) {
            throw new ResourceNotFoundException("Menu with id: " + menuId + " does not exist");
        }
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + productId + " does not exist");
        } else {
            Menu menu = menuOptional.get();
            Product product = productOptional.get();
            Restaurant restaurant = menu.getRestaurant();

            if (!menu.getProducts().contains(product)) {
                throw new ResourceNotFoundException("Product with id: " + productId + " does not exist in menu with id: " + menuId);
            } else {

                Date start = formatter.parse(menu.getStartTime());
                Date end = formatter.parse(menu.getEndTime());
                String date = formatter.format(new Date());
                Date now = formatter.parse(date);
                if (now.before(start) || now.after(end)) {
                    throw new TimeError("This menu  is not available in this hour");
                }

                Order order = new Order();

                Sell sell = new Sell();

                if (amount > product.getAmount()) {
                    amount = product.getAmount();
                }

                Double price = amount * product.getPrice();

                sell.setAmount(amount);
                sell.setPrice(price);


                sell.setOrder(order);
                sell.setProduct(product);

                order.getSells().add(sell);
                order.setStatus(OrderStatus.CREATED);
                order.setCreatedDate(new Date());
                client.getOrders().add(order);
                order.setClient(client);

                restaurant.getOrders().add(order);
                order.setRestaurant(restaurant);


                order = orderRepository.save(order);

                Integer prodcut_amaunt = product.getAmount() - sell.getAmount();
                product.setAmount(prodcut_amaunt);
                productRepository.save(product);
                return order;
            }

        }

    }


    public Order addProductToOrder(Integer orderId, Integer menuId, Integer productId, Integer amount) throws ParseException {

        User client = userService.getCurrentLoggedUser();
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<Menu> menuOptional = menuRepository.findById(menuId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order with id: " + orderId + " does not exist");
        }
        if (menuOptional.isEmpty()) {
            throw new ResourceNotFoundException("Menu with id: " + menuId + " does not exist");
        }
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + productId + " does not exist");
        } else {
            Menu menu = menuOptional.get();
            Product product = productOptional.get();
            Order order = orderOptional.get();

            if (!menu.getProducts().contains(product)) {
                throw new ResourceNotFoundException("Product with id: " + productId + " does not exist in menu with id: " + menuId);
            }
            if (!client.getOrders().contains(order)) {
                throw new NoAuthorizationException("This user can not access this order");
            } else {

                Date start = formatter.parse(menu.getStartTime());
                Date end = formatter.parse(menu.getEndTime());
                String date = formatter.format(new Date());
                Date now = formatter.parse(date);

                if (now.before(start) || now.after(end)) {
                    throw new TimeError("This menu  is not available in this hour");
                }

                Sell sell = new Sell();

                if (amount > product.getAmount()) {
                    amount = product.getAmount();
                }

                Double price = amount * product.getPrice();

                sell.setAmount(amount);
                sell.setPrice(price);


                sell.setOrder(order);
                sell.setProduct(product);

                order.getSells().add(sell);
                order.setStatus(OrderStatus.CREATED);


                order = orderRepository.save(order);

                Integer prodcut_amaunt = product.getAmount() - sell.getAmount();
                product.setAmount(prodcut_amaunt);
                productRepository.save(product);
                return order;
            }

        }

    }

}
