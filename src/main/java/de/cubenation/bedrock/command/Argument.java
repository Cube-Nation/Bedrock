package de.cubenation.bedrock.command;

import java.util.HashMap;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class Argument {

    private String key;

    private String help;

    private HashMap<String, Boolean> arguments;


    public Argument(String key, String help, String... arguments) {
        this.key = key;
        this.help = help;
        this.arguments = new HashMap<>();
        for (String arg : arguments) {
            this.arguments.put(arg, true);
        }
    }

    public Argument(String key, String help, String argument, Boolean required) {
        this.key = key;
        this.help = help;
        this.arguments = new HashMap<>();
        this.arguments.put(argument, required);
    }


    public Argument(String key, String help, HashMap<String, Boolean> arguments) {
        this.key = key;
        this.help = help;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "key='" + key + '\'' +
                ", help='" + help + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
