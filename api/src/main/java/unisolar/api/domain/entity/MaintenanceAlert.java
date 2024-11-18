package unisolar.api.domain.entity;

import unisolar.api.Priority;

public class MaintenanceAlert {
    private String code;
    private String message;
    private Priority priority;

    // Construtores
    public MaintenanceAlert(String code, String message, Priority priority) {
        this.code = code;
        this.message = message;
        this.priority = priority;
    }

    // Getters e setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}

