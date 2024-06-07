package com.example.simplespring.context;

import com.example.simplespring.annotations.Autowired;
import com.example.simplespring.annotations.Component;
import com.example.simplespring.annotations.Qualifier;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<String, Scope> beanScopes = new HashMap<>();

    public ApplicationContext(String basePackage) {
        try {
            scan(basePackage);
            injectDependencies();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scan(String basePackage) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String path = getClass().getClassLoader().getResource(basePackage.replace('.', '/')).getFile();
        File[] files = new File(path).listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                scan(basePackage + "." + file.getName());
            } else {
                String className = basePackage + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    Component component = clazz.getAnnotation(Component.class);
                    String beanName = component.value().isEmpty() ? clazz.getSimpleName() : component.value();
                    beans.put(beanName, clazz.newInstance());
                    beanScopes.put(beanName, Scope.SINGLETON);
                }
            }
        }

    }

    private void injectDependencies() throws IllegalAccessException {
        for (Object bean : beans.values()) {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object dependency = findBean(field);
                    if (dependency != null) {
                        field.setAccessible(true);
                        field.set(bean, dependency); // default scope
                    }
                }
            }
        }
    }

    private Object findBean(Field field) {
        Qualifier qualifier = field.getAnnotation(Qualifier.class);
        if (qualifier != null) {
            return beans.get(qualifier.value());
        }
        return beans.get(field.getType().getSimpleName());
    }

    public Object getBean(String name) {
        Scope scope = beanScopes.get(name);
        if (scope == Scope.PROTOTYPE) {
            return createBean(name);
        }
        return beans.get(name);
    }

    private Object createBean(String name) {
        // create a new instance of the bean
        Class<?> clazz = beans.get(name).getClass();
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
