package io.swagger;

public class GlobalExceptionHandler extends RuntimeException {
  public GlobalExceptionHandler(String message) {
    super(message);
  }
}
