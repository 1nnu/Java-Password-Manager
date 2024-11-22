package ee.taltech.passman.security;

import static org.springframework.security.config.Customizer.withDefaults;

import ee.taltech.passman.security.handlers.CustomAuthenticationFailureHandler;
import ee.taltech.passman.security.jwt.JwtTokenFilter;
import ee.taltech.passman.security.services.UserDetailsServiceImpl;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final PasswordEncoder passwordEncoder;
  private final UserDetailsServiceImpl userDetailsService;
  private final JwtTokenFilter tokenFilter;
  private final CustomAuthenticationFailureHandler authenticationFailureHandler;

  public SecurityConfig(
      PasswordEncoder passwordEncoder,
      UserDetailsServiceImpl userDetailsService,
      JwtTokenFilter tokenFilter,
      CustomAuthenticationFailureHandler authenticationFailureHandler) {
    this.passwordEncoder = passwordEncoder;
    this.userDetailsService = userDetailsService;
    this.tokenFilter = tokenFilter;
    this.authenticationFailureHandler = authenticationFailureHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .cors(withDefaults()) // Enable CORS support with defaults
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            (authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/api/login", "/api/register", "/api/recovery")
                    .permitAll()
                    .requestMatchers(
                        "/api/insertmaster",
                        "/api/setmaster",
                        "/api/ismasterset")
                    .authenticated()
                    .requestMatchers("/api/logoutmaster", "/api/keys", "/api/keys/**")
                    .hasAuthority("vaultaccess")
                    .requestMatchers("/h2-console/**")
                    .permitAll()))
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

    httpSecurity.authenticationProvider(authenticationProvider());
    httpSecurity.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return authProvider;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("https://localhost:3000", "https://localhost:80", "http://localhost:3000")); // Allow your frontend origin
    config.addAllowedHeader("*"); // Allow all headers
    config.addAllowedMethod("*"); // Allow all methods (GET, POST, PUT, DELETE, etc.)
    config.setAllowCredentials(true); // Allow credentials if needed

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config); // Apply to all endpoints
    return source;
  }
}
