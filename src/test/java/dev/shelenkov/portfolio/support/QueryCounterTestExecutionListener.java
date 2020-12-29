package dev.shelenkov.portfolio.support;

import com.yannbriancon.interceptor.HibernateQueryInterceptor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.lang.reflect.AnnotatedElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestExecutionListener which provides support for {@link ExpectQueriesCount} annotation.
 */
public class QueryCounterTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(@NonNull TestContext testContext) {
        HibernateQueryInterceptor interceptor = findHibernateQueryInterceptor(testContext);
        if (interceptor != null) {
            interceptor.startQueryCount();
        }
    }

    @Override
    public void afterTestMethod(@NonNull TestContext testContext) {
        HibernateQueryInterceptor interceptor = findHibernateQueryInterceptor(testContext);
        if (interceptor != null && testContext.getTestException() == null) {
            ExpectQueriesCount classAnnotation = getExpectQueriesCountAnnotation(testContext.getTestClass());
            ExpectQueriesCount methodAnnotation = getExpectQueriesCountAnnotation(testContext.getTestMethod());
            if (classAnnotation == null && methodAnnotation == null) {
                return;
            }

            long expectedQueries = (methodAnnotation != null)
                ? methodAnnotation.value()
                : classAnnotation.value();
            long actualQueries = interceptor.getQueryCount();
            assertThat(actualQueries)
                .withFailMessage(
                    "Expecting queries count <%d> to be equal to <%d> but was not.",
                    actualQueries, expectedQueries)
                .isEqualTo(expectedQueries);
        }
    }

    private ExpectQueriesCount getExpectQueriesCountAnnotation(AnnotatedElement annotatedElement) {
        return AnnotatedElementUtils.findMergedAnnotation(annotatedElement, ExpectQueriesCount.class);
    }

    @Nullable
    private HibernateQueryInterceptor findHibernateQueryInterceptor(TestContext testContext) {
        try {
            return testContext.getApplicationContext().getBean(HibernateQueryInterceptor.class);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }
}
