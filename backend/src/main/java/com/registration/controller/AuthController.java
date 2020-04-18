package com.registration.controller;

import com.registration.exception.AppException;
import com.registration.model.EmailConfirmationToken;
import com.registration.model.Role;
import com.registration.model.RoleName;
import com.registration.model.User;
import com.registration.payload.ApiResponse;
import com.registration.payload.JwtAuthenticationResponse;
import com.registration.payload.LoginRequest;
import com.registration.payload.SignUpRequest;
import com.registration.repository.EmailConfirmationTokenRepository;
import com.registration.repository.RoleRepository;
import com.registration.repository.UserRepository;
import com.registration.security.JwtTokenProvider;
import com.registration.service.EmailSenderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    	    	
    	Optional<User> user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail());
    	
    	System.out.println("user is " + user);
    	System.out.println("user.getEnabled() " + user.get().getEnabled());
    	
    	if(user.get().getEnabled() == false) {
    		throw new AppException("This account hasn't been activated yet.");
    	}
    	
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    	
    	if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);
        
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(user);
        
        emailConfirmationTokenRepository.save(emailConfirmationToken);
        
        SimpleMailMessage mailMessage = new SimpleMailMessage();
		
//		Map model = new HashMap();
        
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("abdallah.algamal93@gmail.com");
//		model.put("emailConfirmationToken", emailConfirmationToken.getConfirmationToken());
//		mailMessage.setModel(model);
        
		mailMessage.setText("To confirm your account, please click here: "
        +"http://localhost:5000/api/auth/confirm-account?token="+emailConfirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
    
    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String emailConfirmationToken)
    {
        EmailConfirmationToken token = emailConfirmationTokenRepository.findByConfirmationToken(emailConfirmationToken);
  
        if(token != null)
        {
        	User user = userRepository.findByEmail(token.getUser().getEmail());
        	user.setEnabled(true);
            userRepository.save(user);
            modelAndView.setViewName("accountVerified");
        }
        
        else
        {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("accountError");
        }

        return modelAndView;
    }
}