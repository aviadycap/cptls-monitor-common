package com.capitolis.monitor.common.resolver;

import com.capitolis.monitor.common.service.SignatureElements;

public class TaskDescriptionResolver {

    public static String getTaskDescription(SignatureElements signatureElements) {
        String taskDescription = signatureElements.getTaskDescription();

        String typeName = signatureElements.extractValueFromArgs("type");
        if (typeName != null && !typeName.isEmpty()) {
            taskDescription += " " + typeName;
        }

        return taskDescription;
    }

}
