package py.edu.uc.lp32025.dto;

import java.time.LocalDateTime;

public abstract class BaseResponseDto {

    private int status;
    private String error;
    private String userError;
    private LocalDateTime timestamp;

    public BaseResponseDto(int status, String error, String userError) {
        this.status = status;
        this.error = error;
        this.userError = userError;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserError() {
        return userError;
    }

    public void setUserError(String userError) {
        this.userError = userError;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}