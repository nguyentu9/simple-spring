package com.example.simplespring.components;

import com.example.simplespring.annotations.Component;

@Component
public class MyService {
    public void serve() {
        System.out.println("Service is serving...");
    }
}
