package com.jsm.boardgame.user.domain.exception

class PasswordMismatchException : IllegalArgumentException("password and confirmation do not match")
