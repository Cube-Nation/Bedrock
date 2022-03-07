package de.cubenation.bedrock.core.annotation;

import de.cubenation.bedrock.core.annotation.condition.DefaultCondition;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ArgumentConstraints {

    Class Condition() default DefaultCondition.class;
}
