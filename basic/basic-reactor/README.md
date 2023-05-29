## basic examples

***Note***:

> `Flux` and `Mono` are `Publisher`s, each subsequent operator is under the hood subscribes to the upstream `Publisher`, processes the element according to its implementation, and passes the result downstream. 

> For example, in this case `.map()` subscribes to `Flux#just`, requests 1 element, applies the passed lambda to the element, passes the result down to `.subscribe()`, and requests the next element:

>> Flux.just(1, 2, 3)
>>   .map(integer -> integer + 1)
>>   .subscribe(incrementedInteger -> log.info(String.valueOf(incrementedInteger)));


