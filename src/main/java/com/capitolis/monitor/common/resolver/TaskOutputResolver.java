package com.capitolis.monitor.common.resolver;

import com.capitolis.monitor.common.api.md.ActionTypeMd;
import com.capitolis.monitor.common.api.md.IMonitorMdFetcher;
import org.springframework.http.ResponseEntity;

public class TaskOutputResolver {

    public static String getTaskOutput(Object returnValue, ActionTypeMd actionTypeMd) {
        String taskOutput = returnValue == null ? null : returnValue.toString();

        if (returnValue instanceof IMonitorMdFetcher) {
            taskOutput = handleMonitorFetcher((IMonitorMdFetcher) returnValue, actionTypeMd);
        }

        if (returnValue instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity) returnValue;
            taskOutput = responseEntity.getStatusCode().toString();
        }

        if (taskOutput == null && returnValue != null ) {
            taskOutput = returnValue.toString();
        }

        return taskOutput;
    }

    private static String handleMonitorFetcher(IMonitorMdFetcher returnValue, ActionTypeMd actionTypeMd) {
        String taskOutput = null;

        String mdFetcherReturnValue = extractMdFetcherReturnValue(actionTypeMd, returnValue);
        if (mdFetcherReturnValue != null && !mdFetcherReturnValue.isEmpty()) {
            taskOutput = mdFetcherReturnValue;
        }

        return taskOutput;
    }

    private static String extractMdFetcherReturnValue(ActionTypeMd actionTypeMd, IMonitorMdFetcher monitorMdFetcherMessage) {

        if (actionTypeMd == null) {
            return extractFetcherResult(monitorMdFetcherMessage);
        }

        Object monitorMdResultMessage = null;
        switch (actionTypeMd) {
            case FETCH   -> monitorMdResultMessage = monitorMdFetcherMessage.getFetchResult();
            case UPLOAD  -> monitorMdResultMessage = monitorMdFetcherMessage.getUploadResult();
            case PUBLISH -> monitorMdResultMessage = monitorMdFetcherMessage.getPublishResult();
        }

        String mdResult;
        if (monitorMdResultMessage == null) {
            mdResult = null;
        } else {
            mdResult = extractBetterMdResultFrom(monitorMdResultMessage);
        }

        return mdResult;
    }

    private static String extractFetcherResult(IMonitorMdFetcher monitorMdFetcherMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("FetcherResult[");
        sb.append("fetcher=").append(monitorMdFetcherMessage.getFetchResult()).append(", ");
        sb.append("upload=").append(monitorMdFetcherMessage.getUploadResult()).append(", ");
        sb.append("publish=").append(monitorMdFetcherMessage.getPublishResult());
        sb.append("]");

        return sb.toString();
    }

    private static String extractBetterMdResultFrom(Object monitorMdResultMessage) {
        String mdResult = monitorMdResultMessage.toString();
        if (mdResult.contains("success=true")) {
            mdResult = "success";
        }
        if (mdResult.contains("success=false")) {
            mdResult = "failed. " + mdResult;
        }
        return mdResult;
    }

}
