package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.annotation.condition.DefaultCondition;
import de.cubenation.api.bedrock.command.CommandRole;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(KeyValueArguments.class)
public @interface KeyValueArgument {

    String Key();

    boolean KeyOnly() default false;

    String Description();

    String Placeholder();

    boolean Optional() default false;

    String Permission() default "";

    CommandRole Role() default CommandRole.NO_ROLE;

    String RoleName() default "NO_ROLE";

    String PermissionDescription() default "";

    Class Condition() default DefaultCondition.class;

}
