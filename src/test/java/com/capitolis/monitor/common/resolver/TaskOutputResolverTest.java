package com.capitolis.monitor.common.resolver;


import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import com.capitolis.monitor.common.api.md.ActionTypeMd;
import com.capitolis.monitor.common.api.md.IMonitorMdFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class TaskOutputResolverTest {

    @InjectMocks
    public TaskOutputResolver taskOutputResolver;

    @Spy
    IMonitorMdFetcher monitorMdFetcher;


    @Test
    void shouldHandleObject() {
        Object object = new Object();

        //when
        String taskOutput = taskOutputResolver.getTaskOutput(object, null);

        then(taskOutput)
                .as("expected object.toString() output")
                .contains("java.lang.Object");
    }

    @Test
    void shouldCreateFetchResolverTaskOutput() {
        given(monitorMdFetcher.getFetchResult()).willReturn("GeneralResult(success=true, message=success)");
        given(monitorMdFetcher.getUploadResult()).willReturn("GeneralResult(success=true, message=success)");
        given(monitorMdFetcher.getPublishResult()).willReturn("GeneralResult(success=true, message=success)");

        //when
        String taskOutputFetch = taskOutputResolver.getTaskOutput(monitorMdFetcher, ActionTypeMd.FETCH);
        String taskOutputUpload = taskOutputResolver.getTaskOutput(monitorMdFetcher, ActionTypeMd.UPLOAD);
        String taskOutputPublisher = taskOutputResolver.getTaskOutput(monitorMdFetcher, ActionTypeMd.PUBLISH);

        then(taskOutputFetch).as("expected ActionType.FETCH sanity test to return success").isEqualTo("success");
        then(taskOutputUpload).as("expected ActionType.UPLOAD sanity test to return success").isEqualTo("success");
        then(taskOutputPublisher).as("expected ActionType.PUBLISH sanity test to return success").isEqualTo("success");
    }

    @Test
    void shouldCreateFetchResolverTaskOutputSuccessFalse() {
        given(monitorMdFetcher.getFetchResult()).willReturn("GeneralResult(success=false, message=failed fetch)");
        given(monitorMdFetcher.getUploadResult()).willReturn("GeneralResult(success=false, message=failed upload)");
        given(monitorMdFetcher.getPublishResult()).willReturn("GeneralResult(success=false, message=failed publish)");

        //when
        String taskOutputFetch = taskOutputResolver.getTaskOutput(monitorMdFetcher, ActionTypeMd.FETCH);
        String taskOutputUpload = taskOutputResolver.getTaskOutput(monitorMdFetcher, ActionTypeMd.UPLOAD);
        String taskOutputPublisher = taskOutputResolver.getTaskOutput(monitorMdFetcher, ActionTypeMd.PUBLISH);

        then(taskOutputFetch).isEqualTo("failed. GeneralResult(success=false, message=failed fetch)");
        then(taskOutputUpload).isEqualTo("failed. GeneralResult(success=false, message=failed upload)");
        then(taskOutputPublisher).isEqualTo("failed. GeneralResult(success=false, message=failed publish)");
    }

    @Test
    void shouldHandleFetcherResultObject() {
        //when
        String taskOutput = taskOutputResolver.getTaskOutput(monitorMdFetcher, null);

        then(taskOutput)
                .as("expected FetcherResult pretty toString output")
                .isEqualTo("FetcherResult[fetcher=null, upload=null, publish=null]");
    }

    @Test
    void shouldHandleResponseEntity200Ok() {
        //given
        ResponseEntity<Object> statusOK = ResponseEntity.ok().build();

        //when
        String taskOutput = taskOutputResolver.getTaskOutput(statusOK, null);

        then(taskOutput)
                .as("expected FetcherResult pretty toString output")
                .isEqualTo("200 OK");
    }

    @Test
    void shouldHandleResponseEntityFailed() {
        //given
        ResponseEntity<Object> statusBadRequest = ResponseEntity.badRequest().build();

        //when
        String taskOutput = taskOutputResolver.getTaskOutput(statusBadRequest, null);

        then(taskOutput)
                .as("expected bad request toString output")
                .isEqualTo("400 BAD_REQUEST");
    }

}