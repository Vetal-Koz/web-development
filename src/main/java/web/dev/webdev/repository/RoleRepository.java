package web.dev.webdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.dev.webdev.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
