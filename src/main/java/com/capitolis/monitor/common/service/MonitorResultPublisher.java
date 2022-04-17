package com.capitolis.monitor.common.service;

import com.capitolis.monitor.common.model.MonitorMessage;

public interface MonitorResultPublisher {

    boolean publish(MonitorMessage monitorMessage);

}
