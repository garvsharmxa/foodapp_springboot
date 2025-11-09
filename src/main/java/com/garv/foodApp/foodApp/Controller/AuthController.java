package com.garv.foodApp.foodApp.Controller;
import com.garv.foodApp.foodApp.Entity.AuthRequest;
import com.garv.foodApp.foodApp.Utlity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest, Principal principal) {
       try{
           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
           );

           return jwtTokenUtil.generateToken(authRequest.getUsername());// return the token
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
}
