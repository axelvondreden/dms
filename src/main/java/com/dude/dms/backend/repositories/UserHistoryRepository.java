package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    List<UserHistory> findByUser(User user);

}
