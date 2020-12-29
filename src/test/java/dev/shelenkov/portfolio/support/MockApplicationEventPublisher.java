package dev.shelenkov.portfolio.support;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Mock implementation of {@link ApplicationEventPublisher}. Allows to verify that the
 * expected set of events was fired during test execution:
 *
 * <pre class="code">
 * {@literal @}SpringJUnitConfig(Service.class)
 * public class Test {
 *
 *   {@literal @}Autowired
 *   private Service service;
 *
 *   {@literal @}Autowired
 *   private MockApplicationEventPublisher mockPublisher;
 *
 *   {@literal @}Test
 *   public void test() {
 *     Event expectedEvent1 = new Event();
 *     Event expectedEvent2 = new Event();
 *     service.execute();
 *     mockPublisher.assertEventsWereFired(event1, event2);
 *   }
 * }
 * </pre>
 */
public class MockApplicationEventPublisher implements ApplicationEventPublisher {

    private final Queue<Object> events = new ConcurrentLinkedQueue<>();

    public void assertEventsWereFired(Object... expected) {
        if (expected.length != events.size()) {
            throw new AssertionError(errorMessage(expected));
        }

        Iterator<Object> iterator = events.iterator();
        for (Object expectedEvent : expected) {
            Object actualEvent = iterator.next();
            if (!expectedEvent.equals(actualEvent)) {
                throw new AssertionError(errorMessage(expected));
            }
        }
    }

    public void assertNoEventsWereFired() {
        if (!events.isEmpty()) {
            throw new AssertionError(errorMessage(new Object[] {}));
        }
    }

    public void reset() {
        events.clear();
    }

    @Override
    public void publishEvent(@NonNull ApplicationEvent event) {
        events.add(event);
    }

    @Override
    public void publishEvent(@NonNull Object event) {
        events.add(event);
    }

    private String errorMessage(Object[] expected) {
        return "Expected events:\n" + Arrays.toString(expected) + "\nbut actual:\n" + events;
    }
}
