package com.farhad.example.reactor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ReactorDemo {
    
    @AllArgsConstructor(staticName = "of")
    @lombok.Data
    static class Application {
        String appName;
    }

    static class Manager{

        Flux<Application> listApplications() {
            return Flux.just(Application.of("App 1"), Application.of("App 1"));
        }

        Flux<String> listApplicationNames() {
            return listApplications().map(app -> app.getAppName() );
        }

        void printApplicationNames() {
            listApplicationNames().subscribe(appName ->  log.info("{}", appName));
        }
    }

    static class MonoManager {

        Flux<Application> listApplications() {
            return Flux.just(Application.of("App 1"), Application.of("App 1"));
        }

        Mono<List<String>> listApplicationNames() {

            return listApplications()
                            .map(app -> app.getAppName())
                            .collectList();
        }

        Mono<Boolean> doseApplicationExist(String appName) {
            return listApplicationNames()
                                    .map(l -> l.contains(appName));
        }

        public static void main(String[] args) {
            MonoManager manager = new MonoManager();
            manager.doseApplicationExist("App 1").subscribe(b -> log.info("{}", b));
            manager.doseApplicationExist("App 4").subscribe(b -> log.info("{}", b));
        }
    }
    public static void main(String[] args) {
        Manager manager  = new Manager();

        manager.printApplicationNames();
    }
}
