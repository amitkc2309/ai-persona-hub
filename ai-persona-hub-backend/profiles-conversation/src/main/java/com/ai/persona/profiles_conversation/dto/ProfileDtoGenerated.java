package com.ai.persona.profiles_conversation.dto;

import com.ai.persona.profiles_conversation.constants.Gender;
import lombok.Data;

@Data
public class ProfileDtoGenerated {
    String firstName;
    String lastName;
    String email;
    int age;
    String ethnicity;
    Gender gender;
    String bio;
    String myersBriggsPersonalityType;
}
