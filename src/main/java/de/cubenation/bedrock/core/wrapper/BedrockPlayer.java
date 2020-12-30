package de.cubenation.bedrock.core.wrapper;

import de.cubenation.bedrock.core.exception.WrongBedrockImplementationException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

public interface BedrockPlayer extends BedrockChatSender {

    BedrockPosition getPosition();
    void teleport(BedrockPosition pos) throws WrongBedrockImplementationException;

    String getDisplayName();

    void sendMessage(BaseComponent component);
    void sendMessage(ChatMessageType type, BaseComponent[] components);
}
