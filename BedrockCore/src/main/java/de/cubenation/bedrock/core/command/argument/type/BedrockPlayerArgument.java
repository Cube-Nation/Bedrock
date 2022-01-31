package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.helper.UUIDUtil;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.util.UUID;
import java.util.stream.Collectors;

public class BedrockPlayerArgument extends ArgumentType<BedrockPlayer>{

    public BedrockPlayerArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public BedrockPlayer tryCast(String input) throws ClassCastException {
        return UUIDUtil.isUUID(input) ?
                plugin.getBedrockServer().getPlayer(UUID.fromString(input)) :
                plugin.getBedrockServer().getPlayer(input);
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        plugin.messages().noSuchPlayer(commandSender, input);
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return plugin.getBedrockServer().getOnlinePlayers().stream()
                .map(BedrockPlayer::getDisplayName)
                .collect(Collectors.toList());
    }
}