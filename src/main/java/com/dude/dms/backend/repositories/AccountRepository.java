package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
