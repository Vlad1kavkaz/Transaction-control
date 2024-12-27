package org.txn.control.fincore.feign;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

public class FinCoreErrorDecoder implements ErrorDecoder {

    private static final ErrorDecoder decoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400, 500 -> new RetryableException(
                    response.status(),
                    "Server error retrying - [%s]".formatted(response.reason()),
                    response.request().httpMethod(),
                    0L,
                    response.request()
            );
            default -> decoder.decode(methodKey, response);
        };
    }
}
