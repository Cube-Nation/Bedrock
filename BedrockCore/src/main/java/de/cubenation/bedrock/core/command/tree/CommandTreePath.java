package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandTreePath {

    private final ArrayList<String> callStack = new ArrayList<>();

    public CommandTreePath(String baseCommand) {
        callStack.add(baseCommand);
    }

    private CommandTreePath(List<String> callStack) {
        callStack.addAll(callStack);
    }

    public CommandTreePath clone() {
        return new CommandTreePath(callStack);
    }

    public String[] getCallStack() {
        return callStack.toArray(String[]::new);
    }

    public void appendCall(String call) {
        callStack.add(call);
    }

    @Override
    public String toString() {
        return "/"+StringUtils.join(callStack, " ");
    }
}
