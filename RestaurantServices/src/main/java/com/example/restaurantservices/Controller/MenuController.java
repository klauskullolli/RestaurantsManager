package com.example.restaurantservices.Controller;

import com.example.restaurantservices.Enum.Role;
import com.example.restaurantservices.Exception.NoAuthorizationException;
import com.example.restaurantservices.Exception.ProductExistExeption;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Model.Menu;
import com.example.restaurantservices.Model.Product;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.MenuRepository;
import com.example.restaurantservices.Repository.ProductRepository;
import com.example.restaurantservices.Repository.RestaurantRepository;
import com.example.restaurantservices.Service.MenuService;
import com.example.restaurantservices.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;


    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('CLIENT' ,'SUPERUSER', 'RESTAURANT_MANAGER')")
    public List<Menu> getMenus() {
        User loggedUser = userService.getCurrentLoggedUser();
        if (loggedUser.getRole().equals(Role.RESTAURANT_MANAGER)) {
            return loggedUser.getRestaurant().getMenus();
        }
        return menuRepository.findAll();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT' ,'SUPERUSER', 'RESTAURANT_MANAGER')")
    public ResponseEntity<Menu> getMenu(@PathVariable Integer id) {
        Optional<Menu> menuOptional = menuRepository.findById(id);
        if (menuOptional.isEmpty()) {
            throw new ResourceNotFoundException("Menu with id: " + id + " does not exist");
        } else {
            Menu menu = menuOptional.get();
            User loggedUser = userService.getCurrentLoggedUser();
            if (loggedUser.getRole().equals(Role.RESTAURANT_MANAGER)) {
                List<Menu> menus = loggedUser.getRestaurant().getMenus();
                if (menus.contains(menu)) {
                    return new ResponseEntity<>(menu, HttpStatus.OK);
                } else {
                    throw new NoAuthorizationException("This menu is can not be accessed by this user");
                }
            } else {
                return new ResponseEntity<>(menu, HttpStatus.OK);
            }

        }
    }

    @PostMapping("/product/{productId}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Menu> createMenu(@PathVariable Integer productId, @RequestBody Menu menu) throws ParseException {
        Optional<Product> productOptional = productRepository.findById(productId);
        User loggedUser = userService.getCurrentLoggedUser();
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + productId + "does not exist");
        } else {
            Product product = productOptional.get();
            if (!loggedUser.getRestaurant().getProducts().contains(product))
                throw new NoAuthorizationException("This product in not in this restaurant");
            return new ResponseEntity<>(menuService.addMenu(product, menu), HttpStatus.CREATED);
        }

    }


    @PostMapping("{id}/product/{productId}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Menu> addProductToMenu(@PathVariable Integer id, @PathVariable Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Optional<Menu> menuOptional = menuRepository.findById(id);
        User loggeduser = userService.getCurrentLoggedUser();
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + productId + "does not exist");
        }
        if (menuOptional.isEmpty()) {
            throw new ResourceNotFoundException("Menu with id: " + id + "does not exist");
        } else {
            Menu menu = menuOptional.get();
            Product product = productOptional.get();
            if (!menu.getProducts().contains(product)) {
                if (!loggeduser.getRestaurant().getProducts().contains(product))
                    throw new NoAuthorizationException("This product is not in your restaurant");
                product.getMenus().add(menu);
                menu.getProducts().add(product);
                return new ResponseEntity<>(menuRepository.save(menu), HttpStatus.CREATED);
            } else {
                throw new ProductExistExeption("Product with id: " + productId + " exist in menu");
            }
        }
    }

    @DeleteMapping("{id}/product/{productId}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Menu> removeProductFromMenu(@PathVariable Integer id, @PathVariable Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Optional<Menu> menuOptional = menuRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with id: " + productId + "does not exist");
        }
        if (menuOptional.isEmpty()) {
            throw new ResourceNotFoundException("Menu with id: " + id + "does not exist");
        } else {
            Menu menu = menuOptional.get();
            Product product = productOptional.get();
            if (loggedUser.getRestaurant().getMenus().contains(menu)) {
                if (menu.getProducts().contains(product)) {
                    product.getMenus().remove(menu);
                    menu.getProducts().remove(product);
                    return new ResponseEntity<>(menuRepository.save(menu), HttpStatus.CREATED);
                } else {
                    throw new ResourceNotFoundException("Product with id: " + productId + " does not exist in menu with id: " + id);
                }
            } else {
                throw new NoAuthorizationException("This menu is can not be accessed by this user");
            }

        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<Menu> updateMenu(@PathVariable Integer id, @RequestBody Menu menu) throws ParseException {
        return new ResponseEntity<>(menuService.updateMenu(id, menu), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RESTAURANT_MANAGER')")
    public ResponseEntity<?> deleteMenu(@PathVariable Integer id) {
        Optional<Menu> menu2 = menuRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();
        if (menu2.isEmpty()) {
            throw new ResourceNotFoundException("Menu with Id: " + id + " does not exist");
        } else {
            if (loggedUser.getRestaurant().getMenus().contains(menu2.get())) {
                menuRepository.deleteById(id);

                return new ResponseEntity<>(menu2.get(), HttpStatus.OK);
            } else {
                throw new NoAuthorizationException("This menu is can not be accessed by this user");
            }

        }

    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/test")
    public String test() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return userDetails.toString();
    }
}
