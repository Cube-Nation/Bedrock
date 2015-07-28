package de.cubenation.bedrock.command.argument;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.argument
 */
public class UnsortedArgument extends Argument {

    private final String key;

    @SuppressWarnings(value = "unused")
    public UnsortedArgument(String key, String description, boolean optional, String... placeholder) {
        super(description, optional, placeholder);
        this.key = key;
    }

    @SuppressWarnings(value = "unused")
    public UnsortedArgument(String key, String description, String... placeholder) {
        super(description, false, placeholder);
        this.key = key;
    }


    public String getKey() {
        return key;
    }


    @Override
    public String toString() {
        return "UnsortedArgument{" +
                "key='" + key + '\'' +
                ", description='" + getDescription() + '\'' +
                ", placeholder=" + getPlaceholder() +
                ", optional=" + isOptional() +
                '}';
    }
}
