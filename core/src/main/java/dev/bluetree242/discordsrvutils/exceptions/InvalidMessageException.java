package dev.bluetree242.discordsrvutils.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InvalidMessageException extends RuntimeException {
    private final String message;
    private final Throwable cause;

    public String getMessage() {
        return "Failed to load message \"" + message + "\"";
    }
}
