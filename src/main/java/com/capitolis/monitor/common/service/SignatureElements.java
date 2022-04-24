package com.capitolis.monitor.common.service;

import com.capitolis.monitor.common.api.md.ActionTypeMd;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.aspectj.lang.JoinPoint;

@Data
@Builder
public class SignatureElements {

    private Map<String, Object> args2ValueMap;
    private JoinPoint joinPoint;

    private String methodName;
    private Object returnValue;
    private String taskDescription;
    private String message;

    private ActionTypeMd actionTypeMd;
    private Boolean isSuccess;


    public String extractValueFromArgs(String argName) {
        String value = null;
        Map<String, Object> args2ValueMap = this.args2ValueMap;
        Object type = args2ValueMap.get(argName);
        if (type != null) {
            value = type.toString();
        }

        return value;
    }

}
