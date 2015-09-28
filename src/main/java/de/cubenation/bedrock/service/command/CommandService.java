package de.cubenation.bedrock.service.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;

import java.util.*;
import java.util.logging.Level;

public class CommandService extends AbstractService implements ServiceInterface {

    private ArrayList<CommandManager> command_manager = new ArrayList<>();

    public CommandService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServiceInitException {

        HashMap<String, ArrayList<AbstractCommand>> commands = this.getPlugin().getCommands();
        if (commands == null)
            return;

        for (Object o : commands.entrySet()) {
            Map.Entry pair = (Map.Entry) o;

            String command = (String) pair.getKey();
            ArrayList<AbstractCommand> value = (ArrayList<AbstractCommand>) pair.getValue();

            if (value.size() == 0) {
                this.getPlugin().log(Level.WARNING, "  command service: Not registering command " + command + " (no CommandManagers assigned)");
                continue;
            }

            //this.getPlugin().log(Level.INFO, "  command service: Registering command " + command + " with " + value.size() + " CommandManager(s)");

            CommandManager manager = new CommandManager(
                    this.getPlugin(),
                    this.getPlugin().getCommand(command),
                    this.getPlugin().getDescription().getName(),
                    value
            );
            manager.getPluginCommand().setExecutor(manager);
            manager.getPluginCommand().setTabCompleter(manager);

            this.addCommandManager(manager);
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        // no reloading of commands supported
    }

    public void addCommandManager(CommandManager manager) {
        this.command_manager.add(manager);
    }

    public ArrayList<CommandManager> getCommandMamagers() {
        return this.command_manager;
    }
}
