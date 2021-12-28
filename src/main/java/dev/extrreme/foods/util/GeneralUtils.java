package dev.extrreme.foods.util;

import java.util.ArrayList;
import java.util.List;

public class GeneralUtils {

    public static String shuffle(String input) {
        List<Character> characters = new ArrayList<>();
        for(char c : input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (characters.size() != 0){
            int randomChar = (int) (Math.random()*characters.size());
            output.append(characters.remove(randomChar));
        }
        return output.toString();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
