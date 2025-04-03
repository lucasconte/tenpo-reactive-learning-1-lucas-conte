package cl.tenpo.learning.reactive.tasks.task1;


import cl.tenpo.learning.reactive.utils.model.UserAccount;
import cl.tenpo.learning.reactive.utils.service.AccountService;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class T1Question7 {

    private final UserService userService;
    private final AccountService accountService;

    public Mono<UserAccount> question7(String userId) {
        return userService.getUserById(userId)
                .doFirst(() -> log.info("[T1Question7] Start process for user {}", userId))
                .zipWith(accountService.getAccountByUserId(userId))
                .map(tuple -> new UserAccount(tuple.getT1(), tuple.getT2()))
                .doOnError(error -> log.info("[T1Question7] Error processing user {}", userId, error))
                .doOnSuccess(result -> log.info("[T1Question7] Success on process result: {}", result));
    }
}
