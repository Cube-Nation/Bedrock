package de.cubenation.bedrock.core.command.tree;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandTreePathItem {

    @Getter
    private CommandTreeNode node;

    @Getter
    private String calledLabel;

    @Getter
    private String[] aliases;

    public static CommandTreePathItem create(CommandTreeNode node, String label, String... allLabels) {
        String[] aliases = Arrays.stream(allLabels).filter(s -> !s.equals(label)).toArray(String[]::new);
        return new CommandTreePathItem(node, label, aliases);
    }
}
