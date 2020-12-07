package dev.shelenkov.portfolio.web.support.ip;

import dev.shelenkov.portfolio.web.exception.CorruptedIpException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.IPAddress;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
@Slf4j
public class IpUtils {

    public static @NonNull String extractIp(HttpServletRequest request) {
        /*
         * We use native forward headers strategy (see server.forward-headers-strategy), so Tomcat
         * processes X-Forwarded-* headers. Tomcat splits header value and puts the last part to
         * remote address and all other parts to X-Forwarded-For header.
         *
         * E.g.
         * Initial header ==: "10.10.10.10"
         * request.getHeader("X-Forwarded-For") == null
         * request.getRemoteAddr() == "10.10.10.10"
         *
         * Initial header == "10.10.10.10c"
         * request.getHeader("X-Forwarded-For") == null
         * request.getRemoteAddr() == "10.10.10.10c"
         *
         * Initial header == "10.10.10.11c,12.12.12.12,13k.13.13.13"
         * request.getHeader("X-Forwarded-For") == "10.10.10.11c, 12.12.12.12"
         * request.getRemoteAddr() == "13k.13.13.13"
         *
         * So we need to validate both parts.
         */
        String xfHeader = request.getHeader("X-Forwarded-For");
        String ip = (xfHeader == null)
            ? request.getRemoteAddr()
            : xfHeader.split(",")[0];
        if (IPAddress.isValidIPv4(ip)) {
            return ip;
        }
        throw new CorruptedIpException(ip);
    }
}
