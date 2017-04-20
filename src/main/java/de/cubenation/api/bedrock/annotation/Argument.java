package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.annotation.condition.DefaultCondition;
import de.cubenation.api.bedrock.command.CommandRole;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Arguments.class)
public @interface Argument {

    String Description();

    String Placeholder();

    boolean Optional() default false;

    Class Condition() default DefaultCondition.class;

    String Permission() default "";

    CommandRole Role() default CommandRole.NO_ROLE;

    String RoleName() default "NO_ROLE";

    String PermissionDescription() default "";

}
