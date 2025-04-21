package app.backend.click_and_buy.services;

import app.backend.click_and_buy.dto.CustomerDTO;
import app.backend.click_and_buy.entities.Cart;
import app.backend.click_and_buy.entities.Customer;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.enums.Roles;
import app.backend.click_and_buy.repositories.CartRepository;
import app.backend.click_and_buy.repositories.CustomerRepository;
import app.backend.click_and_buy.repositories.UserRepository;
import app.backend.click_and_buy.request.UserManagement;
import app.backend.click_and_buy.responses.UserInfos;
import app.backend.click_and_buy.statics.ObjectValidator;
import app.backend.click_and_buy.statics.VerificationCodeGenerator;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class CustomerService {


    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final Argon2PasswordEncoder argon2PasswordEncoder;
    private  final CartRepository cartRepository;

    public Customer save(Customer customer) throws SQLException {
        return customerRepository.save(customer);
    }

    public Customer findById(long id) throws SQLException {
        return customerRepository.findByCustomerId(id);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public boolean updateCustomerProfilePicture(Customer customer, MultipartFile file) {
        try{
            customer.setPicture(file.getBytes());
            save(customer);
            return true;
        } catch (SQLException | IOException e) {
            return false;
        }
    }

    public boolean updateCustomer(Customer customer , CustomerDTO customerDTO) {
        try {
            if(customerDTO.getBirthday() != null)
                customer.setBirthday(customerDTO.getBirthday());

            customer.setFirstName(customerDTO.getFirstName());
            customer.setLastName(customerDTO.getLastName());

            if(ObjectValidator.stringValidator(customerDTO.getAddress()))
                customer.setAddress(customerDTO.getAddress());

            customer.setCity(customerDTO.getCity());

            if(ObjectValidator.stringValidator(customerDTO.getGender()))
                customer.setGender(customerDTO.getGender());

            customer.setPhone(customerDTO.getPhone());

            save(customer);
            return true;
        }catch (Exception Ignore){
            return false;
        }
    }

    public Page<UserInfos> get(int page, int size, Roles roles){
        Page<User> users = userRepository.findByAllRoles(roles.getRoles(), roles.getRoles().size(), PageRequest.of(page,size));
        return users.map(UserInfos::build);
    }

    public void deleteCustomer(long id){
        User user = userRepository.findByUserId(id);
        if (Objects.nonNull(user)) userService.remove(user);
        else throw new EntityNotFoundException(String.format("User with identity %s not found",id));
    }

    public void editCustomerOrCustomerService(long id,UserManagement userManagement){
        if(userRepository.existsByUsernameOrEmail(userManagement.getUsername(), userManagement.getEmail())){
            throw new EntityExistsException(String.format("User with username %s or email %s already exists",userManagement.getUsername(),userManagement.getEmail()));
        }
        User user = userRepository.findByUserId(id);
        if (Objects.nonNull(user)){
            user.setDeleted(!userManagement.isActive());
            user.setEmail(userManagement.getEmail());
            user.setUsername(userManagement.getUsername());
            user.setEmailConfirmed(userManagement.getRoles().equals(Roles.CUSTOMER_SERVICE));
            user.setRoles(userManagement.getRoles().getRoles());
            userRepository.save(user);
            return;
        }
        throw new EntityNotFoundException(String.format("User with identity %s not found",id));
    }

    public String addCustomerOrCustomerService(UserManagement userManagement){
        if(userRepository.existsByUsernameOrEmail(userManagement.getUsername(), userManagement.getEmail())){
            throw new EntityExistsException(String.format("User with username %s or email %s already exists",userManagement.getUsername(),userManagement.getEmail()));
        }
        String password = VerificationCodeGenerator.generatePassword();
        User user = new User();
        user.setDeleted(!userManagement.isActive());
        user.setEmail(userManagement.getEmail());
        user.setUsername(userManagement.getUsername());
        user.setEmailConfirmed(userManagement.getRoles().equals(Roles.CUSTOMER_SERVICE));
        user.setRoles(userManagement.getRoles().getRoles());
        user.setPassword(argon2PasswordEncoder.encode(password));
        try {
            Customer customer = Customer.builder().phone(userManagement.getPhone()).build();
            user.setCustomer(customer);
            userService.save(user);
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
            return password;
        }
        throw new EntityNotFoundException(String.format("User with identity %s not found",id));
    }






}
