package com.jsm.boardgame.user.domain.exception

class UserAlreadyExistsException(username: String) :
    IllegalStateException("username '$username' is already taken")
