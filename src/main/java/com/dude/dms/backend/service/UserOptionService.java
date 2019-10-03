package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.UserOption;
import com.dude.dms.backend.repositories.UserOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class UserOptionService extends CrudService<UserOption> {

    private final UserOptionRepository userOptionRepository;

    @Autowired
    public UserOptionService(UserOptionRepository userOptionRepository) {
        this.userOptionRepository = userOptionRepository;
    }

    @Override
    public JpaRepository<UserOption, Long> getRepository() {
        return userOptionRepository;
    }

    public UserOption findByKey(String key) {
        return userOptionRepository.findByKey(key).orElse(new UserOption(key, null));
    }

}
