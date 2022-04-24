package com.capitolis.monitor.common.resolver;

import com.capitolis.monitor.common.service.SignatureElements;
import org.springframework.http.ResponseEntity;

public class IsSuccessResolver {

    public static Boolean getIsSuccess(SignatureElements signatureElements) {
        Boolean isSuccess = signatureElements.getIsSuccess();

        if (isSuccess != null) {
            return isSuccess;
        }

        Object returnValue = signatureElements.getReturnValue();
        if (returnValue instanceof ResponseEntity) {
            isSuccess = handleResponseEntity((ResponseEntity) returnValue);
        }

        return isSuccess;
    }

    private static Boolean handleResponseEntity(ResponseEntity returnValue) {
        Boolean isSuccess = null;
        ResponseEntity responseEntity = returnValue;

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            isSuccess = true;
        }

        return isSuccess;
    }

}
