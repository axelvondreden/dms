package com.dude.dms.spring.backend.service;

import com.dude.dms.spring.backend.data.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@FunctionalInterface
public interface CrudService<T extends DataEntity> {

  JpaRepository<T, Long> getRepository();

}
