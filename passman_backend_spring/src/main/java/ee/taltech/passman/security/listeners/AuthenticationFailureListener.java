package ee.taltech.passman.security.listeners;

import ee.taltech.passman.security.services.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener
    implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

  private final HttpServletRequest request;

  private final LoginAttemptService loginAttemptService;

  public AuthenticationFailureListener(
      HttpServletRequest request, LoginAttemptService loginAttemptService) {
    this.request = request;
    this.loginAttemptService = loginAttemptService;
  }

  @Override
  public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent e) {
    final String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
      loginAttemptService.loginFailed(request.getRemoteAddr());
    } else {
      loginAttemptService.loginFailed(xfHeader.split(",")[0]);
    }
  }
}
