package pl.wydzials.onlinemusicdatabase.configuration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;

@Component
@Transactional
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Autowired
  private UserRepository userRepository;

  @Override
  public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
      final AuthenticationException e) throws IOException, ServletException {

    final String username = request.getParameter("username");
    final String ip = request.getRemoteAddr();

    userRepository.findByUsername(username)
        .ifPresent(user -> user.addLoginAttempt(ip, false));

    setDefaultFailureUrl("/login-failure");
    super.onAuthenticationFailure(request, response, e);
  }
}
