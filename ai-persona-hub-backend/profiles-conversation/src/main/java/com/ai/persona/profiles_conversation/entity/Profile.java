package com.ai.persona.profiles_conversation.entity;

import com.ai.persona.profiles_conversation.constants.Gender;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
@Data
public class Profile{
        @Id
        String id;
        Boolean isBot;
        String firstName;
        String lastName;
        String email;
        int age;
        String ethnicity;
        Gender gender;
        String bio;
        String imageUrls;
        String myersBriggsPersonalityType;
        Set<String> matchedProfiles;
}
