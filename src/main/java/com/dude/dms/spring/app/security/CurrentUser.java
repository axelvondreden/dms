package com.dude.dms.spring.app.security;

import com.dude.dms.spring.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {

  User getUser();
}
