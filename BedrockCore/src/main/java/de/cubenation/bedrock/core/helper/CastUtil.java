package de.cubenation.bedrock.core.helper;

public class CastUtil {

    public static int fromStringToInt(final String inputValue, final int defaultValue){
        try{
            return Integer.parseInt(inputValue);
        } catch (NumberFormatException e){
            return defaultValue;
        }
    }
}
