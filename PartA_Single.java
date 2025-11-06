package com.example.a;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// Single-file version containing all classes for Part A
public class PartA_Single {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        GreetingService gs = ctx.getBean(GreetingService.class);
        System.out.println(gs.greet("Harsh"));
        ctx.close();
    }
}

interface GreetingService {
    String greet(String name);
}

class GreetingServiceImpl implements GreetingService {
    private final String prefix;
    public GreetingServiceImpl(String prefix) { this.prefix = prefix; }
    @Override
    public String greet(String name) { return prefix + ", " + name + "!"; }
}

@Configuration
class AppConfig {
    @Bean
    public String greetingPrefix() { return "Hello"; }

    @Bean
    public GreetingService greetingService(String greetingPrefix) {
        return new GreetingServiceImpl(greetingPrefix);
    }
}
