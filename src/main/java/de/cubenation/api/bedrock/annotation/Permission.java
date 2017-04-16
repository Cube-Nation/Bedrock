package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.command.CommandRole;

import java.lang.annotation.*;

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
