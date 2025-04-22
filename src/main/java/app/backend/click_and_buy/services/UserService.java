package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Cart;
import app.backend.click_and_buy.entities.Customer;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.enums.Roles;
import app.backend.click_and_buy.repositories.CartRepository;
import app.backend.click_and_buy.repositories.CustomerRepository;
import app.backend.click_and_buy.repositories.UserRepository;
import app.backend.click_and_buy.request.UserManagement;
import app.backend.click_and_buy.responses.UserInfos;
import app.backend.click_and_buy.statics.VerificationCodeGenerator;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final Argon2PasswordEncoder argon2PasswordEncoder;
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;

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

    public User findByEmail(String email,boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", isDeleted);
        User user= userRepository.findByEmail(email);
        session.disableFilter("deletedUserFilter");
        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(long id, boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", isDeleted);
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

    public void reportOrUnReport(long userId,boolean reporting){
        User user = userRepository.findByUserId(userId);
        if(Objects.isNull(user)) throw new EntityNotFoundException("The user does not exists with ID "+userId);
        user.setReportedTimes(reporting ? user.getReportedTimes()+1 : 0);
        save(user);
    }

    public Page<UserInfos> getAllWithPage(int page, int size, Roles roles){
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUserFilter");
        filter.setParameter("isDeleted", false);
        Page<User> users = userRepository.findByAllRoles(roles.getRoles(), roles.getRoles().size(), PageRequest.of(page,size));
        session.disableFilter("deletedUserFilter");
        return users.map(UserInfos::build);
    }

    public void deleteUser(long id){
        User user = userRepository.findByUserId(id);
        if (Objects.nonNull(user)) remove(user);
        else throw new EntityNotFoundException(String.format("User with identity %s not found",id));
    }

    public void editUser(long id, UserManagement userManagement){
        if(userRepository.existsByUsernameOrEmailExcludingUserId(userManagement.getUsername(), userManagement.getEmail(), id)){
            throw new EntityExistsException(String.format("User with username %s or email %s already exists",userManagement.getUsername(),userManagement.getEmail()));
        }

        try {
            User user = findById(id, false);
            if (Objects.nonNull(user)) {
                user.setActive(userManagement.isActive());
                user.setEmail(userManagement.getEmail());
                user.setUsername(userManagement.getUsername());
                //user.setRoles(userManagement.getRoles().getRoles());
                save(user);
                return;
            }
        }catch (RuntimeException runtimeException){
            throw new RuntimeException(runtimeException);
        }
        throw new EntityNotFoundException(String.format("User with identity %s not found",id));
    }

    @Transactional
    public String addUser(UserManagement userManagement){
        if(userRepository.existsByUsernameOrEmail(userManagement.getUsername(), userManagement.getEmail())){
            throw new EntityExistsException(String.format("User with username %s or email %s already exists",userManagement.getUsername(),userManagement.getEmail()));
        }
        String password = VerificationCodeGenerator.generatePassword();
        User user = new User();
        user.setEmail(userManagement.getEmail());
        user.setUsername(userManagement.getUsername());
        user.setEmailConfirmed(true);
        user.setRoles(userManagement.getRoles().getRoles());
        user.setPassword(argon2PasswordEncoder.encode(password));
        try {
            Customer customer = Customer.builder().city("Tangier").phone(userManagement.getPhone()).build();
            user.setCustomer(customerRepository.save(customer));
            save(user);
            cartRepository.save(Cart.builder().customer(customer).build());
            return password;
        } catch (RuntimeException e) {
            throw  new RuntimeException(e.getMessage());
        }
    }

    public String resetPassword(long id){
        User user = userRepository.findByUserId(id);
        if (Objects.nonNull(user)){
            String password = VerificationCodeGenerator.generatePassword();
            user.setPassword(argon2PasswordEncoder.encode(password));
            save(user);
            return password;
        }
        throw new EntityNotFoundException(String.format("User with identity %s not found",id));
    }

}
