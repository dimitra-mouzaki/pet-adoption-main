package com.example.distributed_systems.controllers;

import com.example.distributed_systems.entities.Role;
import com.example.distributed_systems.entities.User;
import com.example.distributed_systems.repositories.RoleRepository;
import com.example.distributed_systems.services.UserDetailsImpl;
import com.example.distributed_systems.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//TODO CHECK FOR NECESSITY OF ALL METHODS
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService uService) {
        this.userService = uService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public ResponseEntity<?> showUsers() {

        try {
            List<User> users = this.userService.getUsers();
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users found.");
            }
            return ResponseEntity.ok(users);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving users: " + e.getMessage());
        }
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<?> showUser(@PathVariable Long user_id,
                                      @AuthenticationPrincipal UserDetailsImpl auth) {

        try {
            System.out.println("Requested user_id: " + user_id);
            System.out.println("Authenticated user_id: " + auth.getId());

            if (!user_id.equals(auth.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Optional<User> result = this.userService.getUser(user_id);
            return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());

        }

    }

}

