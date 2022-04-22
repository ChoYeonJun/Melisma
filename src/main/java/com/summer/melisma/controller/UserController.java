package com.summer.melisma.controller;

import com.summer.melisma.config.JwtTokenUtil;
import com.summer.melisma.model.dto.LoginReqUserDto;
import com.summer.melisma.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LoginReqUserDto reqDto) {
        String message = "success";
        
        try{
            userService.create(reqDto);
        } catch (DataIntegrityViolationException e) {
            message = "duplicate username";
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReqUserDto reqDto) {
        String message = "";
        JwtResponse jwtResponse = new JwtResponse(message);
        try{
            message = userService.login(reqDto).toString();
            final String token = jwtTokenUtil.generateToken(message);
            jwtResponse.setToken(token);
        } catch (Exception e) {
            message = "fail";
        }

        return ResponseEntity.ok(jwtResponse);
    }

    @Data
    @AllArgsConstructor
    class JwtResponse {
        private String token;
    }
}
