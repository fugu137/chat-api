package zoidnet.dev.chat.repository;

import org.springframework.data.repository.Repository;
import zoidnet.dev.chat.model.User;

import java.util.List;
import java.util.Optional;


@org.springframework.stereotype.Repository
public interface UserRepository extends Repository<User, Long> {

    List<User> findAll();

    Optional<User> findById(Long id);

    void save(User user);

}