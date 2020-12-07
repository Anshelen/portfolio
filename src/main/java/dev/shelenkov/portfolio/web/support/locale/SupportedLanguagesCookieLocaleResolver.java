package dev.shelenkov.portfolio.web.support.locale;

import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * <p>Cookie locale resolver that determines default locale using
 * 'Accept-Language' header with a list of supported languages. If no locale was
 * chosen, then uses a standard
 * {@link CookieLocaleResolver#determineDefaultLocale(HttpServletRequest)}
 * mechanism.</p>
 * <p>E.g. if we support "ru" and "en" languages and user has
 * 'Accept-Language' header equal to 'fr;q=0.9,en-UK;ru,q=0.5', then 'en-UK'
 * locale will be chosen.</p>
 */
public class SupportedLanguagesCookieLocaleResolver extends CookieLocaleResolver {

    private Set<String> supportedLanguages;

    public SupportedLanguagesCookieLocaleResolver(Collection<String> supportedLanguages) {
        this.supportedLanguages = new HashSet<>(supportedLanguages);
    }

    @Override
    protected Locale determineDefaultLocale(HttpServletRequest request) {
        Enumeration<Locale> userLocales = request.getLocales();
        while (userLocales.hasMoreElements()) {
            Locale locale = userLocales.nextElement();
            if (supportedLanguages.contains(locale.getLanguage())) {
                return locale;
            }
        }
        return super.determineDefaultLocale(request);
    }

    public Set<String> getSupportedLanguages() {
        return Collections.unmodifiableSet(supportedLanguages);
    }

    public void setSupportedLanguages(Collection<String> supportedLanguages) {
        this.supportedLanguages = new HashSet<>(supportedLanguages);
    }
}
