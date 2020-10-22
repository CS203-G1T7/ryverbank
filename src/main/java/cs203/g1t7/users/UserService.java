package cs203.g1t7.users;

import java.util.List;

public interface UserService {
    List<User> listUsers();
    User getUser(Integer id);
    User addUser(User user);
    User updateUser(Integer id, User user);

    /**
     * Change method's signature: do not return a value for delete operation
     * @param id
     */
    void deleteUser(Integer id);
}