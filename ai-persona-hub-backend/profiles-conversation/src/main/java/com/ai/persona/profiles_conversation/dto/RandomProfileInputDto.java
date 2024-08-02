package com.ai.persona.profiles_conversation.dto;

import com.ai.persona.profiles_conversation.constants.Gender;
import lombok.Data;

@Data
public class RandomProfileInputDto {
    Gender gender;
    Integer age;
    String ethnicity;
}
