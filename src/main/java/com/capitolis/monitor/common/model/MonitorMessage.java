package com.capitolis.monitor.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MonitorMessage {
    public static final int MAX_SIZE_TASK_DESCRIPTION = 255;
    public static final int MAX_SIZE_TASK_OUTPUT = 511;
    public static final int MAX_SIZE_MESSAGE = 511;


    private String  serviceName;     //microservice name
    private String  traceId;         //uniquely identify the request across the system
    private String  taskDescription; //human-readable descriptive name for the step
    private String  taskReturnValue;  //the return value from the monitor method
    private Boolean isSuccess;
    private Long durationInMillis;

    private String message;         //short step description


    public void addTaskDescription(String addDescription) {
        taskDescription += addDescription;
    }

    public void adjustStringLength() {
        if (taskDescription != null && taskDescription.length() > MAX_SIZE_TASK_DESCRIPTION) {
            taskDescription = taskDescription.substring(0, MAX_SIZE_TASK_DESCRIPTION);
        }

        if (taskReturnValue !=null && taskReturnValue.length() > MAX_SIZE_TASK_OUTPUT) {
            taskReturnValue = taskReturnValue.substring(0, MAX_SIZE_TASK_OUTPUT);
        }

        if (message != null && message.length() > MAX_SIZE_MESSAGE) {
            message = message.substring(0, MAX_SIZE_MESSAGE);
        }

    }
}
