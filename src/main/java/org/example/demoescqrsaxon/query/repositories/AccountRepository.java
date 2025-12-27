package org.example.demoescqrsaxon.query.repositories;

import org.example.demoescqrsaxon.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
