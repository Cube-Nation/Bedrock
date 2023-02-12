/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.command.argument;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Range;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.authorization.Permission;
import de.cubenation.bedrock.core.command.argument.type.ArgumentType;
import de.cubenation.bedrock.core.command.argument.type.ArgumentTypeWithRange;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.injection.Component;
import de.cubenation.bedrock.core.service.command.ArgumentTypeService;
import de.cubenation.bedrock.core.translation.Translation;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 2.0
 */

@ToString
public class Argument extends Component {

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String placeholder;

    @Getter
    private final boolean optional;

    @Getter
    private final boolean array;

    @Getter
    private Permission permission = null;

    @Getter
    private final Class<?> dataType;

    @Getter
    private final ArgumentType<?> argumentType;

    public Argument(FoundationPlugin plugin, String description, String placeholder, boolean optional, boolean array, Class<?> dataType, Range rangeAnnotation) throws CommandInitException {
        super(plugin);
        this.setDescription(description);
        this.setPlaceholder(placeholder);
        this.optional = optional;
        this.array = array;
        this.dataType = dataType;
        this.argumentType = createArgumentTypeInstance(rangeAnnotation);
    }

    private ArgumentType<?> createArgumentTypeInstance(Range rangeAnnotation) throws CommandInitException {
        if (dataType == null) {
            return null;
        } else {
            // Get class type
            // TODO: use injection instead
            Class<? extends ArgumentType<?>> argumentType = ((ArgumentTypeService) plugin.getServiceManager().getService(ArgumentTypeService.class)).getType(dataType);
            if (argumentType == null) {
                throw new CommandInitException(getClass().getName() + ": " + dataType.getSimpleName() + " is not an allowed command argument type. Please contact plugin author.");
            }

            // Create instance
            ArgumentType<?> instance;
            try {
                Constructor<? extends ArgumentType<?>> constructor = argumentType.getConstructor(FoundationPlugin.class);
                instance = constructor.newInstance(plugin);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new CommandInitException(e.getMessage());
            }

            // Set range if applicable
            if (instance instanceof ArgumentTypeWithRange<?> && rangeAnnotation != null) {
                ((ArgumentTypeWithRange<?>) instance).setRange(rangeAnnotation.min(), rangeAnnotation.max());
            }

            return instance;
        }
    }

    public String getRuntimeDescription() {
        return new Translation(this.plugin, this.description).getTranslation();
    }

    public String getRuntimePlaceholder() {
        if (this.placeholder == null)
            return null;
        return new Translation(this.plugin, this.placeholder).getTranslation();
    }

    public void setPermission(Permission permission) {
        if (this.permission != null) {
            permission.setPlugin(this.plugin);
        }
        this.permission = permission;
    }

    public boolean userHasPermission(BedrockChatSender sender) {
        return this.permission == null || this.permission.userHasPermission(sender);
    }
}

