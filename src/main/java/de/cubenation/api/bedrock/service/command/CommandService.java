package de.cubenation.api.bedrock.service.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.CommandHandler;
import de.cubenation.api.bedrock.command.AbstractCommand;
import de.cubenation.api.bedrock.command.predefined.*;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.service.AbstractService;
import de.cubenation.api.bedrock.service.settings.SettingsService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class CommandService extends AbstractService {

    private ArrayList<CommandManager> command_manager = new ArrayList<>();

    public CommandService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        // get all commands and their handles from CommandHandler annotations
        Arrays.stream(this.getPlugin().getClass().getAnnotationsByType(CommandHandler.class)).forEach(
                commandHandler -> this.addCommandManager(commandHandler.Command(), commandHandler.Handlers())
        );

        // TODO: doc
        CommandManager pluginCommandManager = null;
        for (CommandManager commandManager : this.getCommandManagers()) {
            if (commandManager.getPluginCommand().getLabel().equalsIgnoreCase(getPlugin().getDescription().getName())) {
                pluginCommandManager = commandManager;
                break;
            }
        }
        
        if (pluginCommandManager == null) {

            pluginCommandManager = new CommandManager(
                    this.getPlugin(),
                    this.getPlugin().getCommand(this.getPlugin().getDescription().getName()),
                    this.getPlugin().getDescription().getName()
            );
            this.addCommandManager(pluginCommandManager);

            try {
                pluginCommandManager.getPluginCommand().setExecutor(pluginCommandManager);
                pluginCommandManager.getPluginCommand().setTabCompleter(pluginCommandManager);
            } catch (Exception e) {
                throw new ServiceInitException("Please add your Pluginname as command in the plugin.yml!");
            }
        }

        // add default commands that all plugins are capable of
        pluginCommandManager.addCommand(new CommandListCommand(getPlugin(), pluginCommandManager));
        pluginCommandManager.addCommand(new ReloadCommand(getPlugin(), pluginCommandManager));
        pluginCommandManager.addCommand(new VersionCommand(getPlugin(), pluginCommandManager));
        pluginCommandManager.addCommand(new PermissionListCommand(getPlugin(), pluginCommandManager));
        pluginCommandManager.addCommand(new PermissionOtherCommand(getPlugin(), pluginCommandManager));
        pluginCommandManager.addCommand(new RegenerateLocaleCommand(getPlugin(), pluginCommandManager));

        SettingsService settingService = plugin.getSettingService();
        if (settingService != null && settingService.getSettingsMap() != null && !settingService.getSettingsMap().isEmpty()) {
            pluginCommandManager.addCommand(new SettingsInfoCommand(getPlugin(), pluginCommandManager));
        }
    }

    private void addCommandManager(String command, Class<? extends AbstractCommand>[] handlers) {
        if (handlers.length == 0) {
                this.getPlugin().log(
                        Level.WARNING,
                        "CommandService: Not registering command " + command + " (no handlers assigned)"
                );
                return;
        }

        CommandManager manager = new CommandManager(
                this.getPlugin(),
                this.getPlugin().getCommand(command),
                this.getPlugin().getDescription().getName()
        );

        for (Class<?> handler : handlers) {
            Constructor<?> constructor;
            try {
                constructor = handler.getConstructor(BasePlugin.class, CommandManager.class);
                manager.addCommand((AbstractCommand) constructor.newInstance(plugin, manager));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        manager.getPluginCommand().setExecutor(manager);
        manager.getPluginCommand().setTabCompleter(manager);

        this.addCommandManager(manager);
    }

    @Override
    public void reload() throws ServiceReloadException {
        // no reloading of commands supported
    }

    private void addCommandManager(CommandManager manager) {
        this.command_manager.add(manager);
    }

    public ArrayList<CommandManager> getCommandManagers() {
        return this.command_manager;
    }

    @Override
    public String toString() {
        return "CommandService{" +
                "command_manager=" + command_manager +
                '}';
    }

}
