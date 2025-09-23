package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@SpringBootApplication
@RestController
public class DemoApplication {

    private Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping("/spike")
    public String errorSpike(@RequestParam(defaultValue = "0") int spike) {
        // Simulate error spike if spike param > 0
        if (spike > 0 && random.nextInt(100) < spike) {
            throw new RuntimeException("Simulated error for spike demo!");
        }
        return "Hello, world!";
    }
}