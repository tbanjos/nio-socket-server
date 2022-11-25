package org.numbers.file;

import org.numbers.server.MessageValidator;

public class NumberValidator implements MessageValidator {

    public boolean isValid(String message){
        //TODO validate message

        //TODO Deduplicate

        return true;
    }
}
