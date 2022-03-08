package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.helper.UUIDUtil;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BedrockOfflinePlayerArgument extends ArgumentType<BedrockOfflinePlayer>{

    public BedrockOfflinePlayerArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public BedrockOfflinePlayer tryCast(String input) throws ClassCastException {
        CompletableFuture<BedrockOfflinePlayer> player = plugin.getBedrockServer().getOfflinePlayer(input, false);
        // TODO: Bad, because it's not async :c
        try {
            return player.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        plugin.messages().noSuchPlayer(commandSender, input);
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return plugin.getBedrockServer().getPlayers().stream()
                .map(BedrockPlayer::getDisplayName)
                .collect(Collectors.toList());
    }
}
