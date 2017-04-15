package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.command.AbstractCommand;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CommandHandlers.class)
public @interface CommandHandler {

    String Command();

    Class<? extends AbstractCommand>[] Handlers();

}
