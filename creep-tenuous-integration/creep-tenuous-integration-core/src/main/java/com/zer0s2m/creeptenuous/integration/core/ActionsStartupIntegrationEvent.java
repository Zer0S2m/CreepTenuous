package com.zer0s2m.creeptenuous.integration.core;

/**
 * Interface for implementing the launch of actions at the start of the
 * integration of an internal or third-party service
 * <p>Use with overflowing events such as:</p>
 * <ul>
 *     <li><a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/ContextRefreshedEvent.html">ContextRefreshedEvent</a></li>
 *     <li><a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/ContextStartedEvent.html">ContextStartedEvent</a></li>
 * </ul>
 */
public interface ActionsStartupIntegrationEvent {

    /**
     * Calling a method while listening for context
     * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/package-use.html#org.springframework.context.event">events</a>
     */
    void startup();

}
