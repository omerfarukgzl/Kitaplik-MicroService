package com.Omer.api;

import com.Omer.entity.User;
import com.Omer.repository.UserRepository;
import com.Omer.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
@ResponseBody
//@RequiredArgsConstructor
public class UserController {



    /*
    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }


    */
    @Autowired
    private UserService userService;


    @PostMapping(value = "/create")
    public ResponseEntity<User> createUser(@RequestBody User user)
    {
        User resultUser=userService.createUser(user);
        return ResponseEntity.ok(resultUser);
    }
}