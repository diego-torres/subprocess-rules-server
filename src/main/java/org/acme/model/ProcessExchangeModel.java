package org.acme.model;

public class ProcessExchangeModel {
    private String subprocess;
    private String message;

    public ProcessExchangeModel() {
    }

    public ProcessExchangeModel(String subprocess, String message) {
        this.subprocess = subprocess;
        this.message = message;
    }

    /**
     * @return String return the subprocess
     */
    public String getSubprocess() {
        return subprocess;
    }

    /**
     * @param subprocess the subprocess to set
     */
    public void setSubprocess(String subprocess) {
        this.subprocess = subprocess;
    }

    /**
     * @return String return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{\"subprocess\": \"" + subprocess == null ? ""
                : subprocess + "\", \"message\": \"" + message == null ? "" : message + "\"}";
    }

}