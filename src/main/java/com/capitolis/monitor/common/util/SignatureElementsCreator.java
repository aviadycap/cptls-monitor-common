package com.capitolis.monitor.common.util;

import com.capitolis.monitor.common.api.IMonitorTaskStatus;
import com.capitolis.monitor.common.api.CapitolisMonitor;
import com.capitolis.monitor.common.api.md.ActionTypeMd;
import com.capitolis.monitor.common.service.SignatureElements;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;


@Data
public class SignatureElementsCreator {


    public static SignatureElements create(JoinPoint joinPoint, Object returnValue) {
        return SignatureElements.builder()
                .args2ValueMap(extractArgsMap(joinPoint))
                .joinPoint(joinPoint)
                .methodName(extractMethodName(joinPoint))
                .returnValue(returnValue)
                .taskDescription(extractTaskDescription(joinPoint))
                .message(extractMessage(joinPoint))
                .actionTypeMd(getExtractActionTypeMd(joinPoint))
                .isSuccess(extractIsSuccess(returnValue))
                .build();
    }

    public static String extractMethodName(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        return codeSignature.getName();
    }

    public static Map<String, Object> extractArgsMap(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Map<String, Object> args = new LinkedHashMap<>();
        String names[] = signature.getParameterNames();
        for (int i = 0, len = names.length; i < len; i++) {
            args.put(names[i], jp.getArgs()[i]);
        }

        return args;
    }

    public static String extractTaskDescription(JoinPoint joinPoint) {
        // Method Information
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // Method annotation
        Method method = signature.getMethod();
        CapitolisMonitor monitorTrace = method.getAnnotation(CapitolisMonitor.class);

        return monitorTrace.taskDescription();
    }

    public static String extractMessage(JoinPoint joinPoint) {
        String methodName = extractMethodName(joinPoint);

        StringBuilder sb = new StringBuilder();
        sb.append(joinPoint.getTarget().getClass().getName());
        sb.append(".").append(methodName).append('(');

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameterNames[i]).append('=');
            sb.append(parameterValues[i] + "");
        }
        sb.append(')');

        return sb.toString();
    }

    private static ActionTypeMd getExtractActionTypeMd(JoinPoint joinPoint) {
        String methodName = SignatureElementsCreator.extractMethodName(joinPoint);
        ActionTypeMd actionType = null;

        switch (methodName) {
            case "fetch":
                actionType = ActionTypeMd.FETCH;
                break;
            case "upload":
                actionType = ActionTypeMd.UPLOAD;
                break;
            case "publish":
                actionType = ActionTypeMd.PUBLISH;
                break;
        }

        return actionType;
    }

    public static Boolean extractIsSuccess(Object returnValue) {
        Boolean isSuccess = null;

        if (returnValue instanceof IMonitorTaskStatus) {
            IMonitorTaskStatus monitorTaskStatus = (IMonitorTaskStatus) returnValue;
            isSuccess = monitorTaskStatus.isSuccess();
        }

        if (returnValue instanceof Throwable) {
            isSuccess = false;
        }

        if (returnValue instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity)returnValue;
        }

        return isSuccess;
    }

}
