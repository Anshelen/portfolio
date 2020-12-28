package dev.shelenkov.portfolio.security.internal;

import dev.shelenkov.portfolio.publisher.EventsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class ExtendedSuccessAuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final EventsPublisher eventsPublisher;

    @SuppressWarnings("CastToConcreteClass")
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        String ip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        long accountId = ((ExtendedUser) authentication.getPrincipal()).getId();

        eventsPublisher.loginCompleted(accountId, ip);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
