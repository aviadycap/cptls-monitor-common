package com.capitolis.monitor.common.resolver;

import com.capitolis.monitor.common.api.md.IMonitorMdFetcher;
import com.capitolis.monitor.common.service.SignatureElements;
import java.util.Map;

public class TraceIdResolver {

    public static String getTraceId(SignatureElements signatureElements) {
        String traceId = signatureElements.extractValueFromArgs("requestId");

        // Failed to extract traceId from args
        if (traceId == null || traceId.isEmpty()) {
            // trying to extract trace ID from input parameters.
            traceId = extractTraceIdFromArgs(signatureElements);
        }

        return traceId;
    }

    /**
     * search for traceId in one of the input objects.
     * @param signatureElements
     * @return traceId extracted from IMonitorMdFetcher, else null
     */
    private static String extractTraceIdFromArgs(SignatureElements signatureElements) {
        String traceId = null;
        Map<String, Object> args2ValueMap = signatureElements.getArgs2ValueMap();
        Object object = args2ValueMap.get("fetcherResult");

        if (object instanceof IMonitorMdFetcher) {
            IMonitorMdFetcher monitorMdFetcherMessage = (IMonitorMdFetcher)object;
            traceId = monitorMdFetcherMessage.getRequestId() + "";
        }

        return traceId;
    }


}
