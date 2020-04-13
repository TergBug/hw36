package org.mycode.errorhandler;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ErrorHandler {
    public void receiveError(byte[] error) {
        System.out.println(new String(error, StandardCharsets.UTF_8));
    }
}
