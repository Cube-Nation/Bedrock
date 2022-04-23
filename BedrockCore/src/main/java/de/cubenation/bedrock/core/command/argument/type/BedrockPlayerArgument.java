package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ArgumentTypeCastException;
import de.cubenation.bedrock.core.helper.UUIDUtil;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.model.wrapper.BedrockPlayer;

import java.util.UUID;
import java.util.stream.Collectors;

public class BedrockPlayerArgument extends ArgumentType<BedrockPlayer>{

    public BedrockPlayerArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public BedrockPlayer tryCast(String input) throws ArgumentTypeCastException {
        BedrockPlayer player = UUIDUtil.isUUID(input) ?
                plugin.getBedrockServer().getPlayer(UUID.fromString(input)) :
                plugin.getBedrockServer().getPlayer(input);

        if (player == null) {
            throw new ArgumentTypeCastException(plugin.messages().getNoSuchPlayer(input));
        }

        return player;
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return plugin.getBedrockServer().getOnlinePlayers().stream()
                .map(BedrockPlayer::getDisplayName)
                .collect(Collectors.toList());
    }
}
