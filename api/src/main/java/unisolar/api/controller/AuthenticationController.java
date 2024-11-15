package unisolar.api.controller;

import jakarta.validation.Valid;
import unisolar.api.domain.dto.securityDTO.AuthenticationData;
import unisolar.api.domain.dto.securityDTO.JWTTokenData;
import unisolar.api.service.TokenService;
import unisolar.api.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity login(@RequestBody @Valid AuthenticationData data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var authentication = manager.authenticate(authenticationToken);

        var jwtToken = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new JWTTokenData(jwtToken));
    }
}

