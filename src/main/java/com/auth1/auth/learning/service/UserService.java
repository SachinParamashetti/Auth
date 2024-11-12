package com.auth1.auth.learning.service;

import com.auth1.auth.learning.model.Token;
import com.auth1.auth.learning.model.User;
import com.auth1.auth.learning.repository.TokenRepository;
import com.auth1.auth.learning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired   // as BCryptPasswordEncoder class not present in our project to autowire this Bean has to be created in Configuration class
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User signUp(String email, String password, String name){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            throw new RuntimeException("Already account exist with this email ID please login");
        }
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
         if(userOptional.isEmpty()){
             throw new RuntimeException("Invalid user or password");
         }

         User user = userOptional.get();
        boolean x = bCryptPasswordEncoder.matches(password, user.getPassword());
        System.out.println(x);
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid user or password");
        }

        Token token = new Token();
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());

        Date expireedDate = getExpiredDate();       
        token.setExpireAt(expireedDate);

        return tokenRepository.save(token);
    }


    //expiration date will be 30 days from today
    private Date getExpiredDate() {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(new Date());

        calendarDate.add(Calendar.DAY_OF_MONTH, 30);
        Date expiredDate = calendarDate.getTime();

        return expiredDate;
    }

    public void logout(String token) {
        Optional<Token> tokenOptional= tokenRepository.findByValueAndDeletedEquals(token,false);
        if(tokenOptional.isEmpty()){
            throw new RuntimeException("Token is invalid");
        }
        Token tokenObject = tokenOptional.get();
        tokenObject.setDeleted(true);
        tokenRepository.save(tokenObject);
    }

    public boolean validateToken(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndDeletedEqualsAndExpireAtGreaterThan(token, false, new Date());
        if(tokenOptional.isEmpty()){
            return false;
        }
        return true;
    }
}
