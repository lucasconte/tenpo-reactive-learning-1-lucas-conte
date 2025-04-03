package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question4 {

    private final CountryService countryService;

    public Flux<String> question4A() {
        Flux<String> countries = countryService.findAllCountries()
                .doFirst(() -> log.info("[question4A] Start process"))
                .take(200)
                .publish()
                .autoConnect(2)
                .doOnComplete(() -> log.info("[question4A] Sucess on process"))
                .doOnError(error -> log.error("[question4A] Error on process", error));

        logCountryRepetitions(countries);
        return sortCountries(countries);
    }

    private void logCountryRepetitions(Flux<String> countries) {
        countries
                .doOnNext(country -> log.info("[question4A][logCountryRepetitions] Country: {}", country))
                .collect(Collectors.toMap(Function.identity(), v -> 1, Integer::sum))
                .doOnNext(map -> log.info("[question4A][logCountryRepetitions] result: {}", map))
                .doOnError(error -> log.error("[question4A][logCountryRepetitions] Error processing: {}", error))
                .subscribe();
    }

    private Flux<String> sortCountries(Flux<String> countries) {
        return countries
                .doOnNext(country -> log.info("[question4A][sortCountries] Country: {}", country))
                .distinct()
                .sort()
                .doOnComplete(() ->  log.info("[question4A][sortCountries] Sucess on process"))
                .doOnError(error -> log.error("[question4A][sortCountries] Error processing: {}", error));
    }
}
