package com.etree.harness;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "management.server.port=0")
class ActuatorSmokeTest {

    @Autowired
    private Environment environment;

    @Test
    void healthEndpointReturnsUp() throws Exception {
        String mPort = environment.getProperty("local.management.port");
        assertThat(mPort).isNotNull();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:" + mPort + "/actuator/health")).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(resp.statusCode()).isBetween(200, 299);
        assertThat(resp.body()).contains("status");
    }

    @Test
    void prometheusEndpointReturnsMetrics() throws Exception {
        String mPort = environment.getProperty("local.management.port");
        assertThat(mPort).isNotNull();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:" + mPort + "/actuator/metrics")).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(resp.statusCode()).isBetween(200, 299);
        String body = resp.body();
        assertThat(body).isNotNull();
        assertThat(body).contains("names");
    }
}
