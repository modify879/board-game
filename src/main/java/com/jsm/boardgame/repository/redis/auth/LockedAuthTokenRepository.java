package com.jsm.boardgame.repository.redis.auth;

import com.jsm.boardgame.entity.redis.auth.LockedAuthToken;
import org.springframework.data.repository.CrudRepository;

public interface LockedAuthTokenRepository extends CrudRepository<LockedAuthToken, String> {
}
