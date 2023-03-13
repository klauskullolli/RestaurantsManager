package com.example.restaurantservices.Service;

import com.example.restaurantservices.Exception.NoAuthorizationException;
import com.example.restaurantservices.Exception.ResourceNotFoundException;
import com.example.restaurantservices.Exception.TimeError;
import com.example.restaurantservices.Model.Menu;
import com.example.restaurantservices.Model.Product;
import com.example.restaurantservices.Model.Restaurant;
import com.example.restaurantservices.Model.User;
import com.example.restaurantservices.Repository.MenuRepository;
import com.example.restaurantservices.Repository.ProductRepository;
import com.example.restaurantservices.Repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    UserService userService;

    private DateFormat formatter = new SimpleDateFormat("hh:mm aa");


    public Menu addMenu(Product product, Menu menu) throws ParseException {
        User loggedUser = userService.getCurrentLoggedUser();
        Restaurant restaurant = loggedUser.getRestaurant();
        Date start = formatter.parse(menu.getStartTime());
        Date end = formatter.parse(menu.getEndTime());
        System.out.println(start);
        System.out.println(end);
        if (end.before(start))
            throw new TimeError("Start time: " + menu.getStartTime() + " can not be after end time: " + menu.getEndTime());

        else {

            product.getMenus().add(menu);
            restaurant.getMenus().add(menu);
            menu.getProducts().add(product);
            menu.setRestaurant(restaurant);
            return menuRepository.save(menu);
        }

    }

    public Menu updateMenu(Integer id, Menu menu) throws ParseException {
        Optional<Menu> menu1 = menuRepository.findById(id);
        User loggedUser = userService.getCurrentLoggedUser();
        if (menu1.isEmpty()) {
            throw new ResourceNotFoundException("Menu with id: " + id + " does not exist");
        } else {
            Menu newMenu = menu1.get();
            if (loggedUser.getRestaurant().getMenus().contains(newMenu)) {
                Date start = formatter.parse(menu.getStartTime());
                Date end = formatter.parse(menu.getEndTime());
                System.out.println(start);
                System.out.println(end);
                if (end.before(start))
                    throw new TimeError("Start time: " + menu.getStartTime() + " can not be after end time: " + menu.getEndTime());

                else {

                    newMenu.setName(menu.getName());
                    newMenu.setStartTime(menu.getStartTime());
                    newMenu.setEndTime(menu.getEndTime());

                    return menuRepository.save(newMenu);
                }
            } else {
                throw new NoAuthorizationException("This menu is can not be accessed by this user");
            }

        }

    }
}
