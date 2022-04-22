package de.cubenation.bedrock.core.annotation;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ArgumentTypes.class)
public @interface ArgumentType {

    Class<? extends de.cubenation.bedrock.core.command.argument.type.ArgumentType<?>> value();

}
