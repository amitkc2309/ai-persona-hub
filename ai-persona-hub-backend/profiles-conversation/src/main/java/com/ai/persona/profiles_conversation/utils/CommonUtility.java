package com.ai.persona.profiles_conversation.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CommonUtility {

    static List<String> ethnicities = new ArrayList<>(List.of("White",
            "Black", "Asian", "Indian", "Hispanic"));
    static List<String> myersBriggsPersonalityTypes = generateMyersBriggsTypes();

    public static int getRandomAge() {
        Random rand = new Random();
        int max = 40;
        int min = 23;
        return rand.nextInt(max - min + 1) + min;
    }

    public static String getRandomEthnicity() {
        return ethnicities.get(ThreadLocalRandom.current().nextInt(ethnicities.size()));
    }

    public static String getPersonalityTypes() {
        return myersBriggsPersonalityTypes.get(ThreadLocalRandom.current().nextInt(myersBriggsPersonalityTypes.size()));
    }

    private static List<String> generateMyersBriggsTypes() {
        List<String> myersBriggsPersonalityTypes = new ArrayList<>();

        String[] dimensions = {
                "E,I", // Extraversion or Introversion
                "S,N", // Sensing or Intuition
                "T,F", // Thinking or Feeling
                "J,P"  // Judging or Perceiving
        };

        // Generate all combinations
        for (String e : dimensions[0].split(",")) {
            for (String s : dimensions[1].split(",")) {
                for (String t : dimensions[2].split(",")) {
                    for (String j : dimensions[3].split(",")) {
                        myersBriggsPersonalityTypes.add(e + s + t + j);
                    }
                }
            }
        }

        return myersBriggsPersonalityTypes;
    }
}
