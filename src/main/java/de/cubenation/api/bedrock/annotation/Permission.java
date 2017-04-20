package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.command.CommandRole;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Permissions.class)
public @interface Permission {

    String Name();

    CommandRole Role() default CommandRole.NO_ROLE;

    String RoleName() default "NO_ROLE";

    String Description() default "";

}
