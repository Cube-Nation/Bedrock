package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.command.CommandRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandArgument {

    String Description();

    String Placeholder();

    boolean Optional() default false;

    String Permission() default "";

    CommandRole Role() default CommandRole.NO_ROLE;

}