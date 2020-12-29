package dev.shelenkov.portfolio.support;

import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@UtilityClass
public class WebMvcUtils {

    public static RequestPostProcessor remoteHost(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }
}
