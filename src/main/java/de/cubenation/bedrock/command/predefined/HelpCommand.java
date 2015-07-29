package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.translation.Translation;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.*;


/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends Command {

    private final CommandManager commandManager;

    private String helpPrefix;

    public HelpCommand(CommandManager commandManager, String helpPrefix) {
        super(
                "help",
                "help.plugin",
                null
        );

        this.commandManager = commandManager;
        this.helpPrefix = helpPrefix;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subCommands, String[] args) throws CommandException {
        if (args.length == 0) {
            // Display help for all commands

            // =========================
            // create header
            // =========================
            sendHeader(sender, label);


            // =========================
            // create help for each subcommand
            // =========================

            for (AbstractCommand command : commandManager.getCommands()) {
                TextComponent component_help = command.getBeautifulHelp(sender);
                if (component_help != null)
                    MessageHelper.send(this.plugin, sender, component_help);
            }

        } else if (StringUtils.isNumeric(args[0])) {
            // FIXME: use PageableListService
            String message = "Zeige (irgendwann) Seite: " + args[0] + " der Hilfe.";
            MessageHelper.send(BedrockPlugin.getInstance(), sender, message);

        } else {

            // Send help for special command

            ArrayList<AbstractCommand> helpList = new ArrayList<>();
            for (AbstractCommand command : getCommandManager().getCommands()) {
                if (command instanceof HelpCommand) {
                    continue;
                }

                if (isValidHelpTrigger(command, args)) {
                    helpList.add(command);
                }
            }

            if (helpList.isEmpty()) {
                // If no command is valid, show help for all
                for (AbstractCommand command : commandManager.getCommands()) {
                    MessageHelper.send(
                            this.getPlugin(),
                            sender,
                            command.getBeautifulHelp(sender)
                    );
                }
            } else {
                sendHeader(sender, label);
                for (AbstractCommand command : helpList) {
                    MessageHelper.send(
                            this.getPlugin(),
                            sender,
                            command.getBeautifulHelp(sender));
                }
            }
        }

    }

    private void sendHeader(CommandSender sender, String label) {
        String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);
        if (helpPrefix != null) {
            commandHeaderName = helpPrefix;
        }

        TextComponent header = new TextComponent(
                new Translation(
                        this.getCommandManager().getPlugin(),
                        "help.header",
                        new String[]{"plugin", commandHeaderName}
                ).getTranslation()
        );
        MessageHelper.send(commandManager.getPlugin(), sender, header, null, null);
    }

    private boolean isValidHelpTrigger(AbstractCommand command, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (!Arrays.asList(command.getCommands().get(i)).contains(args[i])) {
                return false;
            }
        }
        return true;
    }

    public void setHelpPrefix(String helpPrefix) {
        this.helpPrefix = helpPrefix;
    }

    @Override
    public ArrayList<String> getTabCompletion(String[] args, CommandSender sender) {

        if (args.length > 1) {
            // Tab COmpletion for each command to display special help
            // like
            // /plugin help version
            // /plugin help reload

            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(args));
            if (arrayList.contains("help")) {
                arrayList.remove("help");
            }

            ArrayList<String> list = new ArrayList<>();
            for (AbstractCommand cmd : getCommandManager().getCommands()) {
                //Ignore Help Command
                if (cmd instanceof HelpCommand) {
                    continue;
                }

                if (!cmd.hasPermission(sender)) {
                    continue;
                }

                ArrayList tabCom = cmd.getTabCompletion(arrayList.toArray(new String[arrayList.size()]), sender);
                if (tabCom != null) {
                    list.addAll(tabCom);
                }
            }

            // Remove duplicates.
            Set<String> set = new HashSet<>(list);
            if (set.isEmpty()) {
                return null;
            } else {
                return new ArrayList<>(set);
            }

        } else {
            return super.getTabCompletion(args, sender);
        }
    }
}
