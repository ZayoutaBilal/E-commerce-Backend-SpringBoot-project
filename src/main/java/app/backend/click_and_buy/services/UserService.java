package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.repositories.UserRepository;
import app.backend.click_and_buy.statics.Constants;
import app.backend.click_and_buy.statics.ObjectValidator;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Matcher;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findByUsernameOrEmail(String target) {
        return userRepository.findByUsernameOrEmail(target,target);
    }

    public User findByEmail(String email,boolean includeDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", includeDeleted);
        User user= userRepository.findByEmail(email);
        session.disableFilter("deletedUserFilter");
        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(long id, boolean includeDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", includeDeleted);
        User user= userRepository.findByUserId(id);
        session.disableFilter("deletedUserFilter");
        return user;
    }

    public User checkUser(String login) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", false);
        User user = findByUsernameOrEmail(login);
        session.disableFilter("deletedUserFilter");
        return user;
    }

    public List<User> findAll(boolean includeDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", includeDeleted);
        List<User> products = userRepository.findAll();
        session.disableFilter("deletedUserFilter");
        return products;
    }

    public boolean remove(User user){
        try {
            user.setDeleted(true);
            userRepository.save(user);
            return true;
        }catch (Exception Ignore){
            return false;
        }
    }

    public void confirmEmail(User user){
        user.setEmailConfirmed(true);
        save(user);
    }
}
