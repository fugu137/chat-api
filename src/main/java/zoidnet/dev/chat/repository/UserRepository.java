package zoidnet.dev.chat.repository;

import org.springframework.data.repository.Repository;
import zoidnet.dev.chat.model.User;

import java.util.Optional;


@org.springframework.stereotype.Repository
public interface UserRepository extends Repository<User, Long> {


    Optional<User> findById(Long id);

    void save(User user);

}