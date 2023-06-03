package com.ys.aservice.in;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/a-service")
public class AServiceController {

    @GetMapping("/test")
    public ResponseEntity test() {
        return ResponseEntity.status(HttpStatus.OK).body("I'm A Service!");
    }
}
