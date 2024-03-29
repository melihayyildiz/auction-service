package com.ayyildizbank.auctionservice.auth.config;

import com.ayyildizbank.auctionservice.auth.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;
    private List<String> excludeUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String jwt = getJwt(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            User user = jwtUtils.getUserFromJwtToken(jwt);
            List<GrantedAuthority> authorities
                = AuthorityUtils.createAuthorityList(user.getRoles());
            PreAuthenticatedAuthenticationToken authentication
                = new PreAuthenticatedAuthenticationToken(
                user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else{
            if(Pattern.compile("/api.*").matcher(request.getRequestURI()).matches()){
                throw new InsufficientAuthenticationException("");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return excludeUrls.stream().map(Pattern::compile).collect(Collectors.toSet()).
            stream()
            .anyMatch(pattern -> pattern.matcher(request.getRequestURI()).matches());
    }

    private String getJwt(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}