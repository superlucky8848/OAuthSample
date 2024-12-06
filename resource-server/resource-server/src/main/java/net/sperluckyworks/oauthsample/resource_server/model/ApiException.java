package net.sperluckyworks.oauthsample.resource_server.model;

public class ApiException extends RuntimeException {
    private String name;

    public ApiException(String name, String message) {
        super(message);
        this.name = name;
    }

    public ApiException(String name, String message, Throwable cause) {
        super(message, cause);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ApiException [name = " + name + ", " + super.toString() + "]";
    }
}