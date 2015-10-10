package de.cubenation.bedrock.command.argument;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.argument
 */
public class UnsortedArgument extends Argument {

    private final String key;

    @SuppressWarnings(value = "unused")
    public UnsortedArgument(String key, String description, String placeholder, boolean optional) {
        super(description, placeholder, optional);
        this.key = key;
    }

    @SuppressWarnings(value = "unused")
    public UnsortedArgument(String key, String description, String placeholder) {
        super(description, placeholder, false);
        this.key = key;
    }


    public String getKey() {
        return key;
    }


    @Override
    public String toString() {
        return "UnsortedArgument{" +
                "key='" + key + '\'' +
                ", description='" + getRuntimeDescription() + '\'' +
                ", placeholder=" + getRuntimePlaceholder() +
                ", optional=" + isOptional() +
                '}';
    }
}
