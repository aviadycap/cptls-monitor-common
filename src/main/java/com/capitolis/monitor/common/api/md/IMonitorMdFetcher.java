package com.capitolis.monitor.common.api.md;


import java.util.UUID;

public interface IMonitorMdFetcher {

    public UUID getRequestId();

    public Object getFetchResult();

    public Object getUploadResult();

    public Object getPublishResult();

}
