package com.jagaad.jagaadorderservices;

import com.jagaad.jagaadorderservices.entities.Recipes;
import com.jagaad.jagaadorderservices.entities.Role;
import com.jagaad.jagaadorderservices.entities.Users;
import com.jagaad.jagaadorderservices.services.RecipeService;
import com.jagaad.jagaadorderservices.services.RoleService;
import com.jagaad.jagaadorderservices.services.UserService;
import com.jagaad.jagaadorderservices.utils.RecordStatus;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;

@SpringBootApplication
@AllArgsConstructor
public class JagaadOrderServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JagaadOrderServicesApplication.class, args);
    }



    //save necessary data to the memory db after application start required for the application

    @Bean
    CommandLineRunner run(UserService userService, RoleService roleService, RecipeService recipeService) {

        return args -> {

            //add system roles
            roleService.saveRole(new Role(1L,"ROLE_USER"));
            roleService.saveRole(new Role(2L,"ROLE_MANAGER"));
            roleService.saveRole(new Role(3L,"ROLE_ADMIN"));
            roleService.saveRole(new Role(4L,"ROLE_SUPER_ADMIN"));


            //add system users
            Users user1 = new Users("JOHN", "DOE", "254713171479", "john@gmail.com", "1234", new ArrayList<>());
            user1.setId(1L);
            Users user2 = new Users("AMOS", "MATHU", "255713171479", "amos@gmail.com", "1234", new ArrayList<>());
            user2.setId(2L);
            userService.saveUser(user1);
            userService.saveUser(user2);


            //add users roles
            userService.addRoleToUser("john@gmail.com", "ROLE_USER");
            userService.addRoleToUser("john@gmail.com", "ROLE_MANAGER");
            userService.addRoleToUser("john@gmail.com", "ROLE_USER");
            userService.addRoleToUser("john@gmail.com", "ROLE_ADMIN");
            userService.addRoleToUser("john@gmail.com", "ROLE_SUPER_ADMIN");



            userService.addRoleToUser("amos@gmail.com", "ROLE_MANAGER");
            userService.addRoleToUser("amos@gmail.com", "ROLE_USER");
            userService.addRoleToUser("amos@gmail.com", "ROLE_ADMIN");
            userService.addRoleToUser("amos@gmail.com", "ROLE_SUPER_ADMIN");


            //save some recipe for orders
           Recipes recipes= new Recipes();
           recipes.setId(1l);
           recipes.setDescription("Ground lamb brown sugar bread crumbs chill paste");
           recipes.setName("Moroccan MeatBall Stew");
           recipes.setPriority(100);
           recipes.setPrice(BigDecimal.valueOf(1.33));
           recipes.setStatus(RecordStatus.ACTIVE.toString());

           recipeService.addRecipe(recipes);



            Recipes recipes1= new Recipes();
            recipes1.setId(2l);
            recipes1.setDescription("King  prawn lemon fish fillets olive oil");
            recipes1.setName("Moroccan Fish Stew");
            recipes1.setPriority(90);
            recipes1.setPrice(BigDecimal.valueOf(1.33));
            recipes1.setStatus(RecordStatus.ACTIVE.toString());

            recipeService.addRecipe(recipes1);

        };
    }

    //load security config
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
