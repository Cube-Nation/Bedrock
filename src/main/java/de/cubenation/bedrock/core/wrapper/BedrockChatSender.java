package de.cubenation.bedrock.core.wrapper;

public interface BedrockChatSender {

    String getDisplayName();

    boolean hasPermission(String permission);

    boolean isOp();

    void sendMessage(String msg);
}
