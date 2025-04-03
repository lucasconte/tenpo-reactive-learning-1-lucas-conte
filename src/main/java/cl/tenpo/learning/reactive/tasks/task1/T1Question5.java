package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CalculatorService;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question5 {

    private final CalculatorService calculatorService;
    private final UserService userService;

    public Mono<String> question5A() {
        return Flux.range(100, 901)
                .doOnNext(number -> log.info("[question5A] Processing number: {}", number))
                .map(BigDecimal::valueOf)
                .concatMap(calculatorService::calculate)
                .collectList()
                .flatMap(__ -> userService.findFirstName())
                .doOnError(error -> log.error("[question5A] Error processing", error))
                .onErrorReturn("Chuck Norris")
                .doOnSuccess(result -> log.info("[question5A] Success on process result : {}", result));
    }

    public Flux<String> question5B() {
        return Flux.range(100, 901)
                .doOnNext(number -> log.info("[question5B] Processing number: {}", number))
                .map(BigDecimal::valueOf)
                .flatMap(calculatorService::calculate)
                .collectList()
                .flatMapMany(__ -> findFirstNames(3))
                .doOnError(error -> log.error("[question5B] Error processing", error))
                .onErrorComplete()
                .doOnComplete(() -> log.info("[question5B] Success on process"));
    }

    private Flux<String> findFirstNames(int number) {
        return userService.findAllNames()
                .doFirst(() -> log.info("[question5B][findFirstNames] Find first {} names", number))
                .take(number)
                .doOnNext(name -> log.info("[question5B][findFirstNames] Name: {}", name));
    }

}
