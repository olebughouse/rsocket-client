package com.example.rsocketclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
class GreetingsRestController {

    private final RSocketRequester requester;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/sse/greetings/{n}")
    Publisher<GreetingsResponse> greetingsResponsePublisher(@PathVariable String n) {
        return this.requester.route("greetings")
                .data(new GreetingsRequest(n))
                .retrieveFlux(GreetingsResponse.class);
    }
}

@SpringBootApplication
public class RsocketclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsocketclientApplication.class, args);
    }

//    @Bean
//    SocketAcceptor socketAcceptor(
//            RSocketStrategies strategies,
//            HealthController controller) {
//        return RSocketMessageHandler.responder(strategies, controller);
//    }

    @Bean
    RSocketRequester rSocketRequester(
//            SocketAcceptor acceptor,
            RSocketRequester.Builder builder) {
        return builder
//                .rsocketConnector(connector -> connector.acceptor(acceptor))
                .connectTcp("localhost", 8000)
                .block();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingsRequest {
    private String name;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingsResponse {
    private String greeting;
}

//@Controller
//class HealthController {
//    @MessageMapping("health")
//    Flux<ClientHealthState> health() {
//        var stream = Stream.generate(() -> new ClientHealthState(Math.random() > .2));
//        return Flux.fromStream(stream).delayElements(Duration.ofSeconds(1));
//    }
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class ClientHealthState {
//    private boolean healthy;
//}
