package com.Omer.services.servicesImpl;

import com.Omer.entity.User;
import com.Omer.repository.UserRepository;
import com.Omer.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServicesImpl implements UserService {

    private final UserRepository userRepository;
    public UserServicesImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    @Override
    public User createUser(User user) {
        user.setCreateData(new Date());
        user.setCreatedBy("Admin");
        return userRepository.save(user);
    }
}
