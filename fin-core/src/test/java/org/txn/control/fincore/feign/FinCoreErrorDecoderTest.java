package org.txn.control.fincore.feign;

import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import feign.FeignException;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinCoreErrorDecoderTest {

    private final ErrorDecoder decoder = new FinCoreErrorDecoder();

    private final Request request = Request.create(
            HttpMethod.GET,
            "/test-url",
            Collections.emptyMap(),
            null,
            StandardCharsets.UTF_8
    );

    @Test
    void testDecode_500_ReturnsRetryableException() {
        // Arrange
        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(request)
                .build();

        // Act
        Exception exception = decoder.decode("testMethod", response);

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        })
                .isInstanceOf(RetryableException.class)
                .hasMessageContaining("Server error retrying - [%s]".formatted("Internal Server Error"));
    }

    @Test
    void testDecode_400_ReturnsRetryableException() {
        // Arrange
        Response response = Response.builder()
                .status(400)
                .reason("Bad Request")
                .request(request)
                .build();

        // Act
        Exception exception = decoder.decode("testMethod", response);

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        })
                .isInstanceOf(RetryableException.class)
                .hasMessageContaining("Server error retrying - [%s]".formatted("Bad Request"));
    }

    @Test
    void testDecode_404_ReturnsDefaultException() {
        // Arrange
        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(request)
                .build();

        // Act
        Exception exception = decoder.decode("testMethod", response);

        // Assert
        assertThatThrownBy(() ->{
                throw exception;
        })
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessageContaining("404 Not Found");
    }
}
