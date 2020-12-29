package dev.shelenkov.portfolio.support;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class MockApplicationEventPublisherResetTestExecutionListener
    extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        String[] names = applicationContext.getBeanNamesForType(
            MockApplicationEventPublisher.class, false, false);
        for (String name : names) {
            applicationContext.getBean(name, MockApplicationEventPublisher.class).reset();
        }
    }
}
