package io.swagger.api;

public class ApiException extends RuntimeException {  // Changed to RuntimeException
    private final int code;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
