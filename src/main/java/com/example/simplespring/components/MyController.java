package com.example.simplespring.components;

import com.example.simplespring.annotations.Autowired;
import com.example.simplespring.annotations.Component;

@Component
public class MyController {
    @Autowired
    private MyService myService;

    public void doSomething() {
        myService.serve();
    }
}
