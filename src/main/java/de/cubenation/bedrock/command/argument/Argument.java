package de.cubenation.bedrock.command.argument;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class Argument {

    private final String description;
    private final ArrayList<String> placeholder;
    private final boolean optional;

    @SuppressWarnings(value = "unused")
    public Argument(String description, boolean optional , String... placeholder) {
        this.description = description;
        this.optional = optional;
        this.placeholder = new ArrayList<>();
        Collections.addAll(this.placeholder, placeholder);
    }

    @SuppressWarnings(value = "unused")
    public Argument(String description, String... placeholder) {
        this.description = description;
        this.optional = false;
        this.placeholder = new ArrayList<>();
        Collections.addAll(this.placeholder, placeholder);
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getPlaceholder() {
        return placeholder;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "description='" + description + '\'' +
                ", placeholder=" + placeholder +
                ", optional=" + optional +
                '}';
    }
}