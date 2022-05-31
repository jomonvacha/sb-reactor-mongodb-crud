package net.exyon.reactor.controller;

import net.exyon.reactor.dto.ProductDto;
import net.exyon.reactor.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getProducts() {
        Flux<ProductDto> productDtoFlux = Flux.just(
                new ProductDto("102", "mobile", 1, 10000),
                new ProductDto("103", "TV", 1, 50000)
        );
        when(productService.getProducts()).thenReturn(productDtoFlux);

        Flux<ProductDto> responseBody = webTestClient.get().uri("/products/102")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(new ProductDto("102", "mobile", 1, 10000))
                .expectNext(new ProductDto("103", "TV", 1, 50000))
                .verifyComplete();
    }

    @Test
    void getProduct() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("102", "mobile", 1, 10000));
        when(productService.getProduct(any())).thenReturn(productDtoMono);

        Flux<ProductDto> responseBody = webTestClient.get().uri("/products/102")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription().expectNextMatches(p -> p.getName().equals("mobile"))
                .verifyComplete();
    }

    @Test
    void getProductBetweenRange() {
    }

    @Test
    void saveProduct() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("102", "mobile", 1, 10000));
        when(productService.saveProduct(productDtoMono)).thenReturn(productDtoMono);

        webTestClient.post().uri("/products")
                .body(Mono.just(productDtoMono), ProductDto.class)
                .exchange()
                .expectStatus().isOk(); // 200
    }

    @Test
    void updateProduct() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("102", "mobile", 1, 10000));
        when(productService.updateProduct(productDtoMono, "102")).thenReturn(productDtoMono);

        webTestClient.put().uri("/products/update/102")
                .body(Mono.just(productDtoMono), ProductDto.class)
                .exchange()
                .expectStatus().isOk(); // 200
    }

    @Test
    void deleteProduct() {
        when(productService.deleteProduct(any())).thenReturn(Mono.empty());
        webTestClient.delete().uri("/products/delete/102")
                .exchange()
                .expectStatus().isOk(); // 200
    }
}