package com.pkharat.app.filter;

import com.pkharat.app.service.UserService;
import com.pkharat.app.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get authorization from Header
        String authorization = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if(null !=authorization && authorization.startsWith("Bearer ")) {
            //fetch token from header string
            token = authorization.substring(7);

            //fetch username from token - jwtUtility
            username = jwtUtility.getUsernameFromToken(token);

            if(null !=username && SecurityContextHolder.getContext().getAuthentication() == null) {
                //fetch user details by username
                UserDetails userDetails = userService.loadUserByUsername(username);

                //validate user details against token
                if(jwtUtility.validateToken(token, userDetails)) {
                    //do username and password authentication
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            //every request filter
            filterChain.doFilter(request,response);

        }

    }
}
