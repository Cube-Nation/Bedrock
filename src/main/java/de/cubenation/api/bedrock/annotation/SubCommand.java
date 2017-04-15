package de.cubenation.api.bedrock.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SubCommands.class)
public @interface SubCommand {

    String[] value();

}
