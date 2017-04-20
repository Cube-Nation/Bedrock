package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.command.AbstractCommand;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CommandHandlers.class)
public @interface CommandHandler {

    String Command();

    Class<? extends AbstractCommand>[] Handlers();

}
