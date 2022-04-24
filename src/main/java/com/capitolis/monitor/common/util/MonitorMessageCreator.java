package com.capitolis.monitor.common.util;

import com.capitolis.monitor.common.resolver.IsSuccessResolver;
import com.capitolis.monitor.common.resolver.TaskDescriptionResolver;
import com.capitolis.monitor.common.resolver.TaskOutputResolver;
import com.capitolis.monitor.common.resolver.TraceIdResolver;
import com.capitolis.monitor.common.model.MonitorMessage;
import com.capitolis.monitor.common.service.SignatureElements;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MonitorMessageCreator {


    public static MonitorMessage create(SignatureElements signature, String serviceName, long durationInMillis) {
        String taskDescription = TaskDescriptionResolver.getTaskDescription(signature);
        String taskReturnValue = TaskOutputResolver.getTaskOutput(signature.getReturnValue(), signature.getActionTypeMd());
        String traceId = TraceIdResolver.getTraceId(signature);
        Boolean isSuccess = IsSuccessResolver.getIsSuccess(signature);

        MonitorMessage monitorMessage = MonitorMessage.builder()
                .serviceName(serviceName)
                .taskDescription(taskDescription)
                .taskReturnValue(taskReturnValue)
                .traceId(traceId)
                .isSuccess(isSuccess)
                .message(signature.getMessage())
                .durationInMillis(durationInMillis)
                .build();

        return monitorMessage;
    }

}
