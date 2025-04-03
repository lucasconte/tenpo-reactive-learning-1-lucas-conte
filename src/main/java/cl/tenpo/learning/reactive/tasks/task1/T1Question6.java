package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.ModuleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class T1Question6 {

    public ConnectableFlux<Double> question6() {
        return Flux.interval(Duration.ofMillis(500))
                .doFirst(() -> log.info("[question6] Start process"))
                .map(tick -> ModuleUtils.faker().random().nextInt(1, 500))
                .map(Integer::doubleValue)
                .doOnNext(price -> log.info("[question6] Price: {}", price))
                .doOnTerminate(() -> log.info("[question6] Process finished"))
                .publish();
    }

}