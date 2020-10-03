package cs203.g1t7.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock 
    private UserRepository users; 

    @InjectMocks
    private UserServiceImpl userService;
    
    
    @Test
    void addUser_NewUser_ReturnSavedUser(){
        // arrange ***
        User user = new User("jimtan123", "password123", "ROLE_USER", "jimtan", "S9794462H", "81235768", "2 Tanjong Pagar S098344", true);
        // mock the "save" operation 
        when(users.save(any(User.class))).thenReturn(user);

        // act ***
        User savedUser = userService.addUser(user);
        
        // assert ***
        assertNotNull(savedUser);
        verify(users).save(user);
    }

    // @Test
    void updateUser_NotFound_ReturnNull(){
        User user = new User("jeantan123", "password123", "ROLE_USER", "jimtan", "S9794462H", "81235768", "2 Tanjong Pagar S098344", true);
        Long userId = 10L;
        when(users.findById(userId)).thenReturn(Optional.empty());
        
        User updatedUser = userService.updateUser(userId, user);
        
        assertNull(updatedUser);
        verify(users).findById(userId);
    }
}