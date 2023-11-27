package com.jsm.boardgame.repository.redis.auth;

import com.jsm.boardgame.entity.redis.auth.LoginAuthToken;
import org.springframework.data.repository.CrudRepository;

public interface LoginAuthTokenRepository extends CrudRepository<LoginAuthToken, String> {
}
