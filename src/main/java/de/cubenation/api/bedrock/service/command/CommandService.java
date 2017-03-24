package de.cubenation.api.bedrock.service.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.AbstractCommand;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.command.predefined.*;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.service.AbstractService;
import de.cubenation.api.bedrock.service.ServiceInterface;
import de.cubenation.api.bedrock.service.settings.SettingsService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandService extends AbstractService implements ServiceInterface {

    private ArrayList<CommandManager> command_manager = new ArrayList<>();

    public CommandService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServiceInitException {

        HashMap<String, ArrayList<Class<?>>> commands = new HashMap<>();
        this.getPlugin().setCommands(commands);

        for (Object o : commands.entrySet()) {
            Map.Entry pair = (Map.Entry) o;

            String command = (String) pair.getKey();
            ArrayList<Class<?>> classNames = (ArrayList<Class<?>>) pair.getValue();

            if (classNames.size() == 0) {
                this.getPlugin().log(Level.WARNING, "  command service: Not registering command " + command + " (no CommandManagers assigned)");
                continue;
            }

            //this.getPlugin().log(Level.INFO, "  command service: Registering command " + command + " with " + value.size() + " CommandManager(s)");

            CommandManager manager = new CommandManager(
                    this.getPlugin(),
                    this.getPlugin().getCommand(command),
                    this.getPlugin().getDescription().getName()
            );

            for (Class<?> className : classNames) {
                Constructor<?> constructor;
                try {
                    constructor = className.getConstructor(BasePlugin.class, CommandManager.class);
                    manager.addCommand((AbstractCommand) constructor.newInstance(plugin, manager));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }

            manager.getPluginCommand().setExecutor(manager);
            manager.getPluginCommand().setTabCompleter(manager);

            this.addCommandManager(manager);
        }


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

    @Override
    public void reload() throws ServiceReloadException {
        // no reloading of commands supported
    }

    public void addCommandManager(CommandManager manager) {
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
