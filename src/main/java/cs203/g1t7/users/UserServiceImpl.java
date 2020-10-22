package cs203.g1t7.users;

import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
   
    private UserRepository users;
    

    public UserServiceImpl(UserRepository users){
        this.users = users;
    }

    @Override
    public List<User> listUsers() {
        return users.findAll();
    }

    @Override
    public User getUser(Integer id){
        return users.findById(id).orElse(null);
    }
    
    // @Override
    public User addUser(User user) {
        return users.save(user);
    }
    
    @Override
    public User updateUser(Integer id, User newUser){
        return users.findById(id).map(user -> {user.setUsername(newUser.getUsername());
            return users.save(user);
        }).orElse(null);
    }

    /**
     * Remove a user with the given id
     */
    @Override
    public void deleteUser(Integer id){
        users.deleteById(id);
    }
}