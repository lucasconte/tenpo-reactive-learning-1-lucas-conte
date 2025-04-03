package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question2 {

    private final CountryService countryService;

    public Flux<String> question2A() {
        return countryService.findAllCountries()
                .distinct()
                .doOnNext(country -> log.info("[question2A] Found country: {}", country))
                .take(5)
                .doOnComplete(() -> log.info("[question2A] Success on process"))
                .doOnError(error -> log.error("[question2A] Error on process", error));
    }

    public Flux<String> question2B() {
        return countryService.findAllCountries()
                .doOnNext(country -> log.info("[question2B] Found country: {}", country))
                .takeUntil(country -> "Argentina".equals(country))
                .doOnComplete(() -> log.info("[question2B] Success on process"))
                .doOnError(error -> log.error("[question2B] Error on process", error));
    }

    public Flux<String> question2C() {
        return countryService.findAllCountries()
                .doOnNext(country -> log.info("[question2C] Found country: {}", country))
                .map(this::mapCountry)
                .takeWhile(country -> !"France".equals(country))
                .onErrorResume(err -> Mono.empty())
                .doOnComplete(() -> log.info("[question2C] Success on process"))
                .doOnError(error -> log.error("[question2C] Error on process", error));
    }

    private String mapCountry(final String country) {
        if("Brasil".equals(country)) {
            throw new RuntimeException("Error BR");
        }
        return country;
    }

}
