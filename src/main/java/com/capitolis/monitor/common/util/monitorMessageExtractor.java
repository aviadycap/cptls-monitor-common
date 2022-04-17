package com.capitolis.monitor.common.util;

import com.capitolis.monitor.common.aspect.CapitolisMonitor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;

public class monitorMessageExtractor {

    public static final int MAX_MESSAGE_SIZE = 511;
    public static final int MAX_SPAN_STATUS_SIZE = 511;

    public static String extractSpanName(JoinPoint joinPoint) {
        // Method Information
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // Method annotation
        Method method = signature.getMethod();
        CapitolisMonitor monitorTrace = method.getAnnotation(CapitolisMonitor.class);

        return monitorTrace.spanName();
    }

    public static String extractMethodData(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        sb.append(joinPoint.getTarget().getClass().getName());
        sb.append(methodName).append('(');
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameterNames[i]).append('=');
            sb.append(parameterValues[i] + "");
        }
        sb.append(')');

        if (sb.length() > MAX_MESSAGE_SIZE) {
            sb.setLength(MAX_MESSAGE_SIZE);
        }

        return sb.toString();
    }

    public static String extractTraceId(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        Integer indexOfTraceId = null;
        int index = 0;
        for (String parameterName : codeSignature.getParameterNames()) {
            if ("requestId".equalsIgnoreCase(parameterName)) {
                indexOfTraceId = index;
            }

            index++;
        }

        if (indexOfTraceId == null) {
            return UUID.randomUUID().toString();
        }

        Object argValue = joinPoint.getArgs()[indexOfTraceId];

        return  argValue == null ? UUID.randomUUID().toString() : argValue.toString();
    }

    public static String extractSpanStatus(Object result) {
        String spanStatus = "NA";
        if (result instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity) result;
            spanStatus = responseEntity.toString();
        }

        if (result instanceof Throwable) {
            Throwable throwable = (Throwable) result;
            spanStatus = String.format("Failed. {} {}", throwable.getMessage(), throwable.getCause()) ;
        }

        if (spanStatus.length() > MAX_SPAN_STATUS_SIZE) {
            spanStatus = spanStatus.substring(0, MAX_SPAN_STATUS_SIZE);
        }

        return spanStatus;
    }

    public static Map<String, Object> getArgsMap(JoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Map<String, Object> args = new LinkedHashMap<>();
        String names[] = signature.getParameterNames();
        for (int i = 0, len = names.length; i < len; i++) {
            args.put(names[i], pjp.getArgs()[i]);
        }
        return args;
    }
}
