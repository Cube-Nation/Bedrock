package de.cubenation.bedrock.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ArgumentType {

    Class<? extends de.cubenation.bedrock.core.command.argument.type.ArgumentType> value();
}
