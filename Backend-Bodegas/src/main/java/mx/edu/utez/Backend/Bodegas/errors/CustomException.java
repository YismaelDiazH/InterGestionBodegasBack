package mx.edu.utez.Backend.Bodegas.errors;

public class CustomException extends RuntimeException{
    private final String errorCode;
    private final String developerMessage;

    public CustomException(String message, String errorCode, String developerMessage) {
        super(message);
        this.errorCode = errorCode;
        this.developerMessage = developerMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }
}
