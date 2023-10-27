package web.dev.webdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.dev.webdev.models.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}
