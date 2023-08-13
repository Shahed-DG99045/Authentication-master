package com.jwt.example.services;

import com.jwt.example.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private List<User>store=new ArrayList<>();

    public UserService() {
        store.add(new User(UUID.randomUUID().toString(),"Rakib Sharif","rakib@gmail.com"));
        store.add(new User(UUID.randomUUID().toString(),"Ashraf Toha","toha@gmail.com"));
        store.add(new User(UUID.randomUUID().toString(),"Shaker Ullah","shaker@gmail.com"));
        store.add(new User(UUID.randomUUID().toString(),"Md.Jowel","jowel@gmail.com"));

    }
    public List<User>getUsers(){
        return this.store;
    }
}
