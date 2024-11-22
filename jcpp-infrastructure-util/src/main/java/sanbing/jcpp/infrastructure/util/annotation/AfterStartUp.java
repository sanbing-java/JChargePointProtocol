/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.annotation;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventListener(ApplicationReadyEvent.class)
@Order
public @interface AfterStartUp {

    int DISCOVERY_SERVICE = 1;
    int REGULAR_SERVICE = 2;

    @AliasFor(annotation = Order.class, attribute = "value")
    int order();
}
