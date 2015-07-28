package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super(
                new String[]{"reload", "r"},
                "help.reload",
                "reload"
        );
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        try {
            this.plugin.reloadConfig();

            this.plugin.getColorSchemeService().reload();
            this.plugin.getPermissionService().reload();
            this.plugin.getCustomConfigurationFileService().reload();
            this.plugin.getLocalizationService().reload();

            MessageHelper.send(
                    this.getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "reload.complete"
                    ).getTranslation()
            );
        } catch (ServiceReloadException e) {

            MessageHelper.send(
                    this.getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "reload.failed"
                    ).getTranslation()
            );
            e.printStackTrace();
        }
    }
}
