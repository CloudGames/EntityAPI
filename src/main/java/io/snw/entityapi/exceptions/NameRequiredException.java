package io.snw.entityapi.exceptions;

public class NameRequiredException extends RuntimeException {

    public NameRequiredException() {
        super("This entitytype requires a name!");
    }
}
