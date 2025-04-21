package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.enums.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    User findByUsernameOrEmail(String username, String email);

    boolean existsByEmail(String email);

    User findByUserId(long id);

    User findByEmailAndPassword(String email, String password);

    User findByUsernameAndPassword(String username, String password);

    void deleteByUserId(long id);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r IN :roles " +
            "GROUP BY u " +
            "HAVING COUNT(r) = :roleCount")
    Page<User> findByAllRoles(@Param("roles") List<String> roles,
                              @Param("roleCount") long roleCount,
                              Pageable pageable);
}
