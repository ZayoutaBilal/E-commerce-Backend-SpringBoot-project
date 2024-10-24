package app.backend.click_and_buy.services;

import app.backend.click_and_buy.dto.CustomerDTO;
import app.backend.click_and_buy.entities.Customer;
import app.backend.click_and_buy.repositories.CustomerRepository;
import app.backend.click_and_buy.statics.ObjectValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public Customer save(Customer customer) throws SQLException {
        return customerRepository.save(customer);
    }

    public Customer findById(long id) throws SQLException {
        return customerRepository.findByCustomerId(id);
    }

    public List<Customer> findAll() throws SQLException {
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
}
