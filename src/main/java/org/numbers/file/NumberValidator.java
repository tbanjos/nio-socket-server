package org.numbers.file;

import org.numbers.message.MessageValidator;

public class NumberValidator implements MessageValidator {

    private static final String NUMBER_REGEX = "[0-9]{9}";

    public boolean isValid(String message){
        return message.matches(NUMBER_REGEX);
        //TODO Deduplicate
    }
}
