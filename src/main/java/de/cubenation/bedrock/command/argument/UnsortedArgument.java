package de.cubenation.bedrock.command.argument;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.argument
 */
public class UnsortedArgument extends Argument {

    private final String key;
    private final boolean optional;


    @SuppressWarnings(value = "unused")
    public UnsortedArgument(String key, String description, boolean optional, String... placeholder) {
        super(description, placeholder);
        this.key = key;
        this.optional = optional;
    }

    @SuppressWarnings(value = "unused")
    public UnsortedArgument(String key, String description, String... placeholder) {
        super(description, placeholder);
        this.key = key;
        this.optional = false;
    }


    public String getKey() {
        return key;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return "UnsortedArgument{" +
                "key='" + key + '\'' +
                ", description='" + getDescription() + '\'' +
                ", placeholder=" + getPlaceholder() +
                ", optional=" + optional +
                '}';
    }
}
