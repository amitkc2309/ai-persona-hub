package com.ai.persona.profiles_conversation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String resource,String fieldName){
        super(String.format("%s not found for given input data %s",resource,fieldName));
    }

}
