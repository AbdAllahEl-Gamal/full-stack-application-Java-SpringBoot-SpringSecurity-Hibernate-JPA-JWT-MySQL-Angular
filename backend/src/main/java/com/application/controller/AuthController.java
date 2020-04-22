package com.application.controller;

import com.application.exception.AppException;
import com.application.model.EmailConfirmationToken;
import com.application.model.Role;
import com.application.model.RoleName;
import com.application.model.User;
import com.application.payload.ApiResponse;
import com.application.payload.JwtAuthenticationResponse;
import com.application.payload.LoginRequest;
import com.application.payload.SignUpRequest;
import com.application.repository.EmailConfirmationTokenRepository;
import com.application.repository.RoleRepository;
import com.application.repository.UserRepository;
import com.application.security.JwtTokenProvider;
import com.application.service.EmailSenderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("abdallah.algamal93@gmail.com");
        mailMessage.setSubject("Complete Registration!");
		mailMessage.setText("Dear,\r\n" + user.getName()+ "\r\n\r\nPlease click on the link below to confirm your account.\r\n\r\n"
        + "http://localhost:5000/api/auth/confirm-account?token=" + emailConfirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
    
    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String emailConfirmationToken) {
        
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
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotUserPassword(ModelAndView modelAndView, User user) {
   
    	User existingUser = userRepository.findByEmail(user.getEmail());
    	
    	System.out.println("existingUser " + existingUser);
    	System.out.println("userEmail " + user.getEmail());

    	if (existingUser != null) {

        	EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(existingUser);
            emailConfirmationTokenRepository.save(emailConfirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setFrom("abdallah.algamal93@gmail.com");
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setText("Dear,\r\n\r\nPlease click on the link below to complete the password reset process.\r\n\r\n"
              + "http://localhost:5000/api/auth/confirm-reset?token=" + emailConfirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);

        } else {
            System.out.println("This email address does not exist!");
        }
    	return ResponseEntity.ok("Check your mail to change your password");
    }
    
    @RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token")String emailConfirmationToken) {
        
    	EmailConfirmationToken token = emailConfirmationTokenRepository.findByConfirmationToken(emailConfirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            userRepository.save(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.addObject("password", user.getPassword());
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
    
    @PostMapping("/reset-password")
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {
    	System.out.println(user.getEmail());
    	System.out.println("Password is " + user.getPassword());
    	if (user.getEmail() != null) {
            // Use email to find user
            User tokenUser = userRepository.findByEmail(user.getEmail());
            tokenUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(tokenUser);
            modelAndView.setViewName("successResetPassword");
        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("accountError");
        }
        return modelAndView;
    }
}