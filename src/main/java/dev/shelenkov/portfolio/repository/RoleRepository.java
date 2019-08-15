package dev.shelenkov.portfolio.repository;

import dev.shelenkov.portfolio.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role getByName(String name);
}
