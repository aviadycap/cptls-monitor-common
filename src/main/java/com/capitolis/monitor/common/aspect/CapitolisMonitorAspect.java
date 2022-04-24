package com.capitolis.monitor.common.aspect;

import com.capitolis.monitor.common.model.MonitorMessage;
import com.capitolis.monitor.common.service.KafkaMonitorPublisher;
import com.capitolis.monitor.common.service.SignatureElements;
import com.capitolis.monitor.common.util.MonitorMessageCreator;
import com.capitolis.monitor.common.util.SignatureElementsCreator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@ConditionalOnProperty(prefix = "capitolis.monitor", name = "enabled")
@Aspect
@Component
@Slf4j
public class CapitolisMonitorAspect {

    @Value("${SERVICE_NAME}")
    private String serviceName;

    private final KafkaMonitorPublisher kafkaMonitorPublisher;


    public CapitolisMonitorAspect(com.capitolis.monitor.common.service.KafkaMonitorPublisher kafkaMonitorPublisher) {
        this.kafkaMonitorPublisher = kafkaMonitorPublisher;

        log.info("Init CapitolisMonitorAspect");
    }


    @Around(value = "@annotation(com.capitolis.monitor.common.api.CapitolisMonitor)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around method:" + joinPoint.getSignature());
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            handleAfter(joinPoint, duration, result);
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            log.info("aroundAdvice catch unsatisfiedLinkError");
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            handleAfter(joinPoint, duration, e);
            throw e;
        }

        return result;
    }

    private void handleAfter(ProceedingJoinPoint joinPoint, long durationInMillis, Object result) {
        SignatureElements signatureElements = SignatureElementsCreator.create(joinPoint, result);
        MonitorMessage monitorMessage = MonitorMessageCreator.create(signatureElements, serviceName, durationInMillis);

        kafkaMonitorPublisher.publish(monitorMessage);
    }

}
