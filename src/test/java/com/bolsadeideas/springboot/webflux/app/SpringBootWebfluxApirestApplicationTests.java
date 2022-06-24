package com.bolsadeideas.springboot.webflux.app;

import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.xml.transform.Source;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootWebfluxApirestApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductoService productoService;


    @Test
    public void listarTest() {

        client.get()
                .uri("/api/productos")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Producto.class)
                .consumeWith((response -> {
                    List<Producto> productos = response.getResponseBody();
                    productos.forEach(p -> {
                        System.out.println(p.getNombre());
                    });

                    Assertions.assertThat(productos.size() > 0).isTrue();

                }));
        //.hasSize(9);
    }

    //Primera forma de test método ver
    @Test
    public void verTest1() {
       /*Para pruebas unitarias debe ser sincrono */
        Producto producto = productoService.findByNombre("TV Sony Bravia OLED 4K Ultra HD").block();

        client.get()
                .uri("/api/productos/{id}", Collections.singletonMap("id", producto.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("TV Sony Bravia OLED 4K Ultra HD");
    }

    //Segunda forma de test método ver
    @Test
    public void verTest2() {
        /*Para pruebas unitarias debe ser sincrono */
        Producto producto = productoService.findByNombre("TV Sony Bravia OLED 4K Ultra HD").block();

        client.get()
                .uri("/api/productos/{id}", Collections.singletonMap("id", producto.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Producto.class)
                .consumeWith((response -> {
                    Producto p = response.getResponseBody();
                    Assertions.assertThat(p.getId()).isNotEmpty();
                    Assertions.assertThat(p.getId().length()>0).isTrue();
                    Assertions.assertThat(p.getNombre()).isEqualTo("TV Sony Bravia OLED 4K Ultra HD");
                    /* Prueba falla al no enconttrar coincidencia */
                    Assertions.assertThat(p.getNombre()).isEqualTo("TV Sony Bravia OLED 8K Ultra HD");
                }));
    }
}
