package pl.wydzials.onlinemusicdatabase.configuration;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.wydzials.onlinemusicdatabase.model.User;
import pl.wydzials.onlinemusicdatabase.repository.UserRepository;

@Component
@Transactional
public class LoginSuccesHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  private final UserRepository userRepository;

  public LoginSuccesHandler(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
      final FilterChain chain, final Authentication authentication) throws IOException, ServletException {

    handleCustomLoginLogic(request, authentication);
    response.sendRedirect("/profile");
    super.onAuthenticationSuccess(request, response, chain, authentication);
  }

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
      final Authentication authentication) throws IOException, ServletException {

    handleCustomLoginLogic(request, authentication);
    response.sendRedirect("/profile");
    super.onAuthenticationSuccess(request, response, authentication);
  }

  private void handleCustomLoginLogic(final HttpServletRequest request, final Authentication authentication) {
    final String username = request.getParameter("username");
    final User user = userRepository.findByUsername(username.trim()).orElseThrow();

    user.addLoginAttempt(request.getRemoteAddr(), true);
  }
}
