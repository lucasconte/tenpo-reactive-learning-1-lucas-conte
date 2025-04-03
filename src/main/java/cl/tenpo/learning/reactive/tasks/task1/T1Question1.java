package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.exception.ResourceNotFoundException;
import cl.tenpo.learning.reactive.utils.exception.UserServiceException;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question1 {

    private final UserService userService;

    public Mono<Integer> question1A() {
        return userService.findFirstName()
                .doOnNext(name -> log.info("[question1A] Name: {}", name))
                .filter(name -> name.startsWith("A"))
                .map(String::length)
                .defaultIfEmpty(-1)
                .doOnSuccess(result -> log.info("[question1A] Success on process result: {}", result))
                .doOnError(error -> log.error("[question1A] Error on process: {}", error));
    }

    public Mono<String> question1B() {
        return userService.findFirstName()
                .doOnNext(name -> log.info("[question1B] Name: {}", name))
                .flatMap(this::saveOrUpdateName)
                .doOnNext(result -> log.info("[question1B] Success on process result: {}", result))
                .doOnError(error -> log.error("[question1B] Error on process: {}", error));
    }

    private Mono<String> saveOrUpdateName(final String name) {
        return Mono.just(name)
                .filterWhen(userService::existByName)
                .flatMap(userService::update)
                .switchIfEmpty(Mono.defer(() -> userService.insert(name)));
    }

    public Mono<String> question1C(String name) {
        return userService.findFirstByName(name)
                .doOnNext(n -> log.info("[question1C] Name: {}", n))
                .onErrorMap(ex -> new UserServiceException()) //
                .switchIfEmpty(Mono.error(new ResourceNotFoundException()))
                .doOnError(ResourceNotFoundException.class, ex -> log.info("[question1C] Name {} not found", name))
                .doOnError(UserServiceException.class, ex -> log.error("[question1C] Error on process name {} : ",
                        name, ex))
                .doOnSuccess(n -> log.info("[question1C] Name {} found", name));
    }

}
