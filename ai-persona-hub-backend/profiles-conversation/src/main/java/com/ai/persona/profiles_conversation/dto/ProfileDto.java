package com.ai.persona.profiles_conversation.dto;

import com.ai.persona.profiles_conversation.constants.Gender;
import lombok.Data;

import java.util.Set;

@Data
public class ProfileDto{
        String id;
        Boolean isBot;
        String firstName;
        String lastName;
        String username;
        String email;
        int age;
        String ethnicity;
        Gender gender;
        String bio;
        String imageUrls;
        String myersBriggsPersonalityType;
        Set<String> matchedProfiles;

}
