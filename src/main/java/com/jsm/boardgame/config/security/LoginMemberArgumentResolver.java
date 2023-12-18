package com.jsm.boardgame.config.security;

import com.jsm.boardgame.common.jwt.AccessTokenExtractor;
import com.jsm.boardgame.common.jwt.AuthToken;
import com.jsm.boardgame.common.jwt.AuthTokenProvider;
import com.jsm.boardgame.exception.ApiException;
import com.jsm.boardgame.exception.ErrorCodeType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthTokenProvider authTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String accessToken = AccessTokenExtractor.extract(request)
                .orElseThrow(() -> new ApiException(ErrorCodeType.UNAUTHORIZED));
        AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);

        Long memberId = authToken.getMemberId();
        if (memberId == null) {
            throw new ApiException(ErrorCodeType.UNAUTHORIZED);
        }

        return memberId;
    }
}
