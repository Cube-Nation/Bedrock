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


            // =========================
            // create help for each subcommand
            // =========================
            for (AbstractCommand command : commandManager.getCommands()) {
                MessageHelper.send(
                        this.getPlugin(),
                        sender,
                        command.getBeautifulHelp(sender)
                );
            }

//            for (SubCommand subCommand : commandManager.getSubCommands()) {
//                TextComponent subCommandHelp = commandManager.getHelpForSubCommand(subCommand, sender, label);
//
//                if (subCommandHelp != null)
//                    MessageHelper.send(commandManager.getPlugin(), sender, subCommandHelp);
//            }

        } else if (StringUtils.isNumeric(args[0])) {
            // FIXME: use PageableListService
            String message = "Zeige (irgendwann) Seite: " + args[0] + " der Hilfe.";
            MessageHelper.send(BedrockPlugin.getInstance(), sender, message);

        } else {

            //TODO Für was war das?
//            for (SubCommand subCommand : commandManager.getSubCommands()) {
//                if (Arrays.asList(subCommand.getCommands().get(0)).contains(args[0])) {
//                    MessageHelper.send(
//                            BedrockPlugin.getInstance(),
//                            sender,
//                            commandManager.getHelpForSubCommand(subCommand, sender)
//                    );
//                }
//            } // for
        }

    }

    public void setHelpPrefix(String helpPrefix) {
        this.helpPrefix = helpPrefix;
    }
}
