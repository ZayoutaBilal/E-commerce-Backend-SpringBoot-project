package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);

    boolean existsByUsername(String username);
    User findByUsernameOrEmail(String username,String email);
    boolean existsByEmail(String email);

    User findByUserId(long id);
    User findByEmailAndPassword(String email, String password);
    User findByUsernameAndPassword(String username, String password);

    void deleteByUserId(long id);



}
