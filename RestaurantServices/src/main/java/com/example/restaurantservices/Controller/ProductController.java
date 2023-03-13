package com.example.restaurantservices.Controller;

import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Exception.NoAuthorizationException;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Model.Menu;
import com.example.restaurantservices.Model.Product;
import com.example.restaurantservices.Model.Restaurant;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.MenuRepository;
import com.example.restaurantservices.Repository.ProductRepository;
import com.example.restaurantservices.Repository.UserRepository;
import com.example.restaurantservices.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        User loggedUser = userService.getCurrentLoggedUser();
        Restaurant restaurant = loggedUser.getRestaurant();
        restaurant.getProducts().add(product);
        product.setRestaurant(restaurant);
        return new ResponseEntity<>(productRepository.save(product), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        Optional<Product> pro = productRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();
        Restaurant restaurant = loggedUser.getRestaurant();
        if (pro.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + id + " does not exist");
        } else {

            Product newProduct = pro.get();
            if (!restaurant.getProducts().contains(newProduct)) {
                throw new NoAuthorizationException("This user can not access this product");
            }
            newProduct.setName(product.getName());
            newProduct.setPrice(product.getPrice());
            newProduct.setAmount(product.getAmount());
            newProduct.setBelonging(product.getBelonging());
            newProduct.setDescription(product.getDescription());
            System.out.println(newProduct);
            return new ResponseEntity<>(productRepository.save(newProduct), HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Product> deleteProduct(@PathVariable Integer id) {
        Optional<Product> prod = productRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();
        Restaurant restaurant = loggedUser.getRestaurant();
        if (prod.isEmpty()) {
            throw new ResourceNotFoundException("Product with Id: " + id + " does not exist");
        } else {
            Product product = prod.get();
            if (!restaurant.getProducts().contains(product)) {
                throw new NoAuthorizationException("This user can not access this product");
            }
            productRepository.deleteById(id);
            return new ResponseEntity<>(prod.get(), HttpStatus.OK);
        }

    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER','CLIENT', 'RESTAURANT_MANAGER')")
    public List<Product> getProducts() {
        User loggedUser = userService.getCurrentLoggedUser();
        if (loggedUser.getRole().equals(Role.RESTAURANT_MANAGER)) {
            return loggedUser.getRestaurant().getProducts();
        } else {
            return productRepository.findAll();
        }

    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN' ,'SUPERUSER','CLIENT','RESTAURANT_MANAGER')")
    public ResponseEntity<Product> getProduct(@PathVariable Integer id) {
        Optional<Product> pro = productRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();
        if (pro.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + id + " does not exist");
        } else {

            Product product = pro.get();
            if (loggedUser.getRole().equals(Role.RESTAURANT_MANAGER)) {
                Restaurant restaurant = loggedUser.getRestaurant();
                if (!restaurant.getProducts().contains(product)) {
                    throw new NoAuthorizationException("This user can not access this product");
                }
                new ResponseEntity<>(product, HttpStatus.OK);
            }
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
    }

    @GetMapping("/menu/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'RESTAURANT_MANAGER')")
    public ResponseEntity<?> getProductByMenuId(@PathVariable Integer id) {
        Optional<Menu> menuOptional = menuRepository.findById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User loggedUser = userRepository.findByUsername(username);
        if (menuOptional.isEmpty()) {
            throw new ResourceNotFoundException("Menu with Id: " + id + " does not exist");
        } else {
            Menu menu = menuOptional.get();
            if (loggedUser.getRole().equals(Role.RESTAURANT_MANAGER)) {
                List<Menu> menus = loggedUser.getRestaurant().getMenus();
                if (menus.contains(menu)) {
                    return new ResponseEntity<>(menu.getProducts(), HttpStatus.OK);
                } else {
                    throw new NoAuthorizationException("This user can not access this menus");
                }
            } else {
                return new ResponseEntity<>(menu.getProducts(), HttpStatus.OK);
            }

        }
    }

}
