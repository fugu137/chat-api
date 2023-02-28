package zoidnet.dev.chat.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import zoidnet.dev.chat.model.User;

import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Modifying
    @Query(value = "INSERT INTO users (username, password) VALUES (:#{#user.username}, :#{#user.password})", nativeQuery = true)
    void insert(@NonNull User user) throws DataIntegrityViolationException;

    @Query(value = "SELECT * FROM users WHERE users.username = :username", nativeQuery = true)
    Optional<User> findByUsername(@NonNull String username);

}