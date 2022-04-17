package com.capitolis.monitor.common.aspect;

import static com.capitolis.monitor.common.util.monitorMessageExtractor.extractMethodData;
import static com.capitolis.monitor.common.util.monitorMessageExtractor.extractSpanName;
import static com.capitolis.monitor.common.util.monitorMessageExtractor.extractSpanStatus;
import static com.capitolis.monitor.common.util.monitorMessageExtractor.extractTraceId;

import com.capitolis.monitor.common.model.MonitorMessage;
import com.capitolis.monitor.common.service.KafkaMonitorPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CapitolisMonitorAspect {

    @Value("${SERVICE_NAME}")
    private String serviceName;

    private final KafkaMonitorPublisher KafkaMonitorPublisher;


    @Around(value = "@annotation(com.capitolis.monitor.common.aspect.CapitolisMonitor)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around method:" + joinPoint.getSignature());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            result = joinPoint.proceed();
            handleAfter(joinPoint, stopWatch, result);
        } catch (Throwable e) {
            handleAfter(joinPoint, stopWatch, e);
            throw e;
        }

        return result;
    }

    private void handleAfter(ProceedingJoinPoint joinPoint, StopWatch stopWatch, Object result) {
        stopWatch.stop();

        String message = extractMethodData(joinPoint);
        String traceId = extractTraceId(joinPoint);
        String spanName = extractSpanName(joinPoint);
        String spanStatus = extractSpanStatus(result);

        MonitorMessage monitorMessage = MonitorMessage.builder()
                .serviceName(serviceName)
                .spanName(spanName)
                .spanStatus(spanStatus)
                .traceId(traceId)
                .message(message)
                .durationInMillis(stopWatch.getTotalTimeMillis())
                .build();

        KafkaMonitorPublisher.publish(monitorMessage);
    }

}
