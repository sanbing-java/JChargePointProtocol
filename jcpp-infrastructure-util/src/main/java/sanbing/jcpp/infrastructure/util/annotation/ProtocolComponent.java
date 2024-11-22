/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;
import sanbing.jcpp.infrastructure.util.annotation.ProtocolComponent.ProtocolCondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author baigod
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Conditional(ProtocolCondition.class)
@Component
public @interface ProtocolComponent {

    @AliasFor(annotation = Component.class)
    String value() default "";

    class ProtocolCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            if (!metadata.isAnnotated(ProtocolComponent.class.getName())) {
                return true;
            }

            String serviceType = context.getEnvironment().getProperty("service.type", "null");

            String protocolName = (String) metadata.getAnnotationAttributes(ProtocolComponent.class.getName()).get("value");

            String enabled = context.getEnvironment().getProperty("service.protocols." + protocolName + ".enabled", "false");

            return ("monolith".equals(serviceType) || "protocol".equals(serviceType)) && "true".equals(enabled);
        }
    }
}