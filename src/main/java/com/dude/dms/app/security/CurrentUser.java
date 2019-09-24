package com.dude.dms.app.security;

import com.dude.dms.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {

    User getUser();
}
