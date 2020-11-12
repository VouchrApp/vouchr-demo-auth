package com.vouchrtech.demo.auth.controllers;

import com.vouchrtech.demo.auth.models.JwtResponse;
import com.vouchrtech.demo.auth.services.VouchrJwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;

import java.security.GeneralSecurityException;
import java.security.Principal;

@RestController
@RequestMapping("/vouchr")
public class VouchrJwtController {

    private final VouchrJwtService jwtService;

    public VouchrJwtController(final VouchrJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/jwt")
    public JwtResponse createJwt(Principal principal) {
        try {
            String idToken = jwtService.get(principal.getName());

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setIdToken(idToken);

            return jwtResponse;
        } catch (GeneralSecurityException ex) {
            throw new ServerErrorException("Unable to create jwt", ex);
        }

    }


}
