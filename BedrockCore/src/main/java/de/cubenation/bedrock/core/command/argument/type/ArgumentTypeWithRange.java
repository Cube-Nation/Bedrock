package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;

public abstract class ArgumentTypeWithRange<T> extends ArgumentType<T> {

    protected int minRange = -9999;
    protected int maxRange = 9999;

    public ArgumentTypeWithRange(FoundationPlugin plugin) {
        super(plugin);
    }

    public void setRange(int min, int max) {
        this.minRange = min;
        this.maxRange = max;
    }
}
