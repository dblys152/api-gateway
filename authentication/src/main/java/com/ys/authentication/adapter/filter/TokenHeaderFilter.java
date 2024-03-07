package com.ys.authentication.adapter.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ys.shared.exception.BadRequestException;
import com.ys.shared.exception.UnauthorizedException;
import com.ys.shared.jwt.JwtProvider;
import com.ys.shared.jwt.PayloadInfo;
import com.ys.shared.utils.ApiResponseModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TokenHeaderFilter extends OncePerRequestFilter {
    private final String secret;
    private final ObjectMapper objectMapper;

    public TokenHeaderFilter(String secret, ObjectMapper objectMapper) {
        this.secret = secret;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = resolveToken(request);

            PayloadInfo payloadInfo = JwtProvider.getInstance().getPayload(accessToken, secret);

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(payloadInfo, null));

            filterChain.doFilter(request, response);
        } catch (BadRequestException ex) {
            handleException(response, HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        } catch (UnauthorizedException ex) {
            handleException(response, HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        } catch (Exception ex) {
            handleException(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String authorizationValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationValue == null) {
            throw new BadRequestException("No authorization");
        }

        return authorizationValue.replace("Bearer ", "");
    }

    private void handleException(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponseModel.error(status, message)));
    }
}
