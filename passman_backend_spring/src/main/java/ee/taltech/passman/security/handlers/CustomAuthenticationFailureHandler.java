package ee.taltech.passman.security.handlers;

import ee.taltech.passman.security.services.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final HttpServletRequest request;
  private final LoginAttemptService loginAttemptService;

  public CustomAuthenticationFailureHandler(
      HttpServletRequest request, LoginAttemptService loginAttemptService) {
    this.request = request;
    this.loginAttemptService = loginAttemptService;
  }

  @Override
  public void onAuthenticationFailure(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException exception)
      throws ServletException, IOException {

    super.onAuthenticationFailure(request, response, exception);

    String errorMessage = "Bad credentials";

    if (loginAttemptService.isBlocked()) {
      errorMessage = "Account suspended";
    }

    request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
  }
}
