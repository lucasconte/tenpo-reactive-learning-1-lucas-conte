package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.exception.AuthorizationTimeoutException;
import cl.tenpo.learning.reactive.utils.exception.PaymentProcessingException;
import cl.tenpo.learning.reactive.utils.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class T1Question8 {

    private final TransactionService transactionService;

    public Mono<String> question8() {
        return transactionService.authorizeTransaction(11111)
                .doFirst(() -> log.info("[question8] Start process"))
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(TimeoutException.class, ex -> new AuthorizationTimeoutException("Timeout"))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(this::isNotTimeoutException))
                .onErrorMap(ex -> isNotTimeoutException(ex), ex -> new PaymentProcessingException("Payment error", ex))
                .doOnError(AuthorizationTimeoutException.class, error -> log.error("[question8] Timeout error", error))
                .doOnError(PaymentProcessingException.class, error -> log.error("[question8] Payment error", error))
                .doOnSuccess(result -> log.info("[question8] Success on process result: {}", result));
    }

    private boolean isNotTimeoutException(Throwable error) {
        return !(error instanceof AuthorizationTimeoutException);
    }
}
