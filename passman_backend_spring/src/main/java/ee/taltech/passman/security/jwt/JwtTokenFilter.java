package ee.taltech.passman.security.jwt;

import ee.taltech.passman.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtUtil jwtTokenUtil;
  private final UserDetailsServiceImpl userDetailsService;

  public JwtTokenFilter(JwtUtil jwtTokenUtil, UserDetailsServiceImpl userDetailsService) {
    this.jwtTokenUtil = jwtTokenUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return request.getRequestURI().contains("/login");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = parseJwt(request);
      if (jwt != null && jwtTokenUtil.isValidToken(jwt)) {
        UserDetails userDetails = null;
        String username = jwtTokenUtil.getUsernameFromToken(jwt);
        if (jwtTokenUtil.hasVaultAccess(jwt)) {
          userDetails = userDetailsService.loadUserAndGiveVaultPermissions(username);
        } else {
          userDetails = userDetailsService.loadUserByUsername(username);
        }
        assert userDetails != null;
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    } catch (Exception ignored) {
    }

    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }
}
