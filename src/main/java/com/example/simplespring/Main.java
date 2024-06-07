package com.example.simplespring;

import com.example.simplespring.components.MyController;
import com.example.simplespring.context.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext("com.example.simplespring.components");
        MyController myController = (MyController) context.getBean("MyController");
        myController.doSomething();
    }
}
