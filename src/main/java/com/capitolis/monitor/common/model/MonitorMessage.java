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

    private String serviceName;     //microservice name
    private String traceId;         //uniquely identify the request across the system
    private String spanName;        //human-readable descriptive name for the step
    private String spanStatus;      //best effort to provide the span status: success/failed
    private Long durationInMillis;

    private String message;         //short step description

}
