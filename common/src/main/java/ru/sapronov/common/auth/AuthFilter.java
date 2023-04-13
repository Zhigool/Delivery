package ru.sapronov.common.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final AccessTokenValidator accessTokenValidator;
    private final PrincipalProvider principalProvider;

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if (accessToken != null) {
            Claims claims = accessTokenValidator.validAndExtractClaims(accessToken);
            principalProvider.setPrincipal(claims.id(), claims.role());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
