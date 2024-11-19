package unisolar.api.domain.entity;

import unisolar.api.enums.Priority;

/**
 * Represents a maintenance alert in the system.
 * This class encapsulates details about a maintenance alert, including its code, message, and priority.
 *
 * @param code     the unique code representing the maintenance alert.
 * @param message  the message describing the maintenance alert.
 * @param priority the priority level of the maintenance alert, represented by the Priority enum.
 *
 * This class is used to define maintenance alerts, which can have different priorities and messages
 * to notify relevant users or systems of issues that require attention.
 */
public class MaintenanceAlert {
    private String code;
    private String message;
    private Priority priority;

    /**
     * Constructor to initialize a new MaintenanceAlert with the given code, message, and priority.
     *
     * @param code     the unique code representing the maintenance alert.
     * @param message  the message describing the maintenance alert.
     * @param priority the priority level of the maintenance alert.
     */
    public MaintenanceAlert(String code, String message, Priority priority) {
        this.code = code;
        this.message = message;
        this.priority = priority;
    }

    // Getters and setters for each property

    /**
     * Gets the code of the maintenance alert.
     *
     * @return the code of the maintenance alert.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code for the maintenance alert.
     *
     * @param code the new code for the maintenance alert.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the message of the maintenance alert.
     *
     * @return the message of the maintenance alert.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message for the maintenance alert.
     *
     * @param message the new message for the maintenance alert.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the priority of the maintenance alert.
     *
     * @return the priority of the maintenance alert.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority for the maintenance alert.
     *
     * @param priority the new priority for the maintenance alert.
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
