package de.cubenation.bedrock.core.command.tree;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
public class CommandTreePath {

    private final ArrayList<CommandTreePathItem> callStack = new ArrayList<>();

    public CommandTreePath(CommandTreePathItem item) {
        // This way we always have at least one item in the callstack
        callStack.add(item);
    }

    private CommandTreePath(List<CommandTreePathItem> callStack) {
        this.callStack.addAll(callStack);
    }

    public CommandTreePath clone() {
        return new CommandTreePath(callStack);
    }

    public CommandTreePathItem[] getAll() {
        // create copy
        return callStack.toArray(CommandTreePathItem[]::new);
    }

    public CommandTreePathItem get(int index) {
        return callStack.get(index);
    }

    public CommandTreePathItem getRoot() {
        return callStack.get(0);
    }

    public CommandTreePathItem getHead() {
        return callStack.get(callStack.size()-1);
    }

    public CommandTreePathItem getParent() {
        if (callStack.size() < 2) {
            return null;
        }
        return callStack.get(callStack.size()-1);
    }

    public CommandTreePath getSequence(int startIndex, int endIndex) {
        return new CommandTreePath(callStack.subList(startIndex, endIndex));
    }

    public void append(CommandTreePathItem item) {
        callStack.add(item);
    }

    public int size() {
        return callStack.size();
    }

    public String getCommandAsString() {
        return callStack.stream().map(CommandTreePathItem::getCalledLabel).collect(Collectors.joining(" "));
    }
}
