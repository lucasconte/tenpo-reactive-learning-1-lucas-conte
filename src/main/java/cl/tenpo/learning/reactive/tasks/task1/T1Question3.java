package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.model.Page;
import cl.tenpo.learning.reactive.utils.service.CountryService;
import cl.tenpo.learning.reactive.utils.service.TranslatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question3 {

    private final CountryService countryService;
    private final TranslatorService translatorService;

    public Flux<String> question3A(Page<String> page) {
        return Mono.justOrEmpty(page)
                .doOnNext(p -> log.info("[question3A] Page is not null"))
                .mapNotNull(Page::items)
                .flatMapMany(Flux::fromIterable)
                .doOnNext(item -> log.info("[question3A] Page item: {}", item))
                .doOnComplete(() -> log.info("[question3A] Page emitted"))
                .doOnError(error -> log.info("[question3A] Error emitting page {}", page, error));
    }

    public Flux<String> question3B(String country) {
        return Mono.justOrEmpty(country)
                .doFirst(() -> log.info("[question3B] Find currencies by country {}", country))
                .flatMapMany(countryService::findCurrenciesByCountry)
                .doOnNext(currency -> log.info("[question3B][{}] Found currency: {}", country, currency))
                .doOnComplete(() -> log.info("[question3B] Success on process country: {}", country))
                .doOnError(error -> log.info("[question3A] Error on process country: {}", country, error));
    }

    public Flux<String> question3C() {
        return countryService.findAllCountries()
                .doFirst(() -> log.info("[question3C] Start process"))
                .take(3)
                .doOnNext(country -> log.info("[question3C] Country: {}", country))
                .mapNotNull(translatorService::translate)
                .doOnNext(translated -> log.info("[question3C] Country translated: {}", translated))
                .doOnComplete(() -> log.info("[question3C] Success on process"))
                .doOnError(error -> log.info("[question3A] Error on process", error));
    }

}
