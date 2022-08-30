package io.siren.dojo.es_plugin.clone;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.testcontainers.shaded.org.hamcrest.core.Is.is;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.cucumber.core.internal.com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;

public class Steps {
  protected static GenericContainer container;

  String baseUrl;

  @Given("the plugin is installed in an elasticsearch cluster")
  public void the_plugin_in_installed_in_an_es_cluster() throws IOException, InterruptedException {
    int exposedPort = 9200;

    final Path dockerFilePath =
        Paths.get(System.getenv("PWD"), "Dockerfile");
    System.out.println(dockerFilePath);
    final ImageFromDockerfile image =
        new ImageFromDockerfile().withDockerfile(dockerFilePath);

    container = new GenericContainer(image);
    container.addEnv("discovery.type", "single-node");
    container.addEnv("xpack.security.enabled", "false");
    container.addExposedPorts(9200);
    container.start();

    Integer mappedPort = container.getMappedPort(exposedPort);
    baseUrl = String.format(
        "http://localhost:%s/", mappedPort
    );
    String healthEndpoint = String.format("%s_cluster/health?wait_for_status=green", baseUrl);

    HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create(healthEndpoint))
        .build();

    HttpClient httpClient = HttpClient.newHttpClient();
    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertTrue(response.body().contains("\"status\":\"green\""));
    assertEquals(response.statusCode(), 200);
  }

  @Given("There are the following data in elasticsearch in the index {string}")
  public void there_are_the_following_data_in_elasticsearch_in_the_index(String index, DataTable data) throws IOException, InterruptedException {
    StringBuilder body = new StringBuilder();
    data.asMaps().forEach(map -> {
      String idxLine = buildIndex(index, map.get("id"));
      String rowLine = buildRowContent(map);
      body.append(idxLine).append("\n").append(rowLine).append("\n");
    });
    String endpoint = String.format("%s_bulk?refresh=true", baseUrl);

    String result = performRequest(endpoint, body.toString());

  }

  private String performRequest(String endpoint, String body) throws IOException, InterruptedException {
    HttpRequest.Builder builder = HttpRequest.newBuilder();
    if (body == null || body.isEmpty()) {
      builder = builder.POST(HttpRequest.BodyPublishers.noBody());
    }
    else {
      builder = builder.POST(HttpRequest.BodyPublishers.ofString(body))
          .header("Content-Type", "application/x-ndjson");
    }
    HttpRequest request = builder
        .uri(URI.create(endpoint))
        .build();

    HttpClient httpClient = HttpClient.newHttpClient();
    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.body());
    assertTrue("Request fail with code:" + response.statusCode(),response.statusCode() >= 200);
    return response.body();
  }

  private String buildIndex(String index, String id) {
    return String.format("""
        { "index" : { "_index" : "%s", "_id" : "%s" } }""", index, id).trim();
  }

  private String buildRowContent(Map<String, String> row) {
    StringBuilder body = new StringBuilder("{");
    String quote = "\"";
    row.forEach((key, value) -> {
      if (key != "id") {
        body.append(quote).append(key).append(quote).append(":").append(quote).append(value).append(quote).append(", ");
      }
    });
    body.delete(body.length() - 2, body.length());
    body.append("}");
    return body.toString();
  }


  @Then("The documents on index {string} are")
  public void the_documents_on_index_are(String index, DataTable data) throws IOException, InterruptedException {
    Map<String, Map<String, String>> expected = data.asMaps().stream().collect(Collectors.toMap(m -> m.get("id"), Function.identity()));
    String endpoint = String.format("%s%s/_search", baseUrl, index);
    String response = performRequest(endpoint, null);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(response);
    JsonNode hits = actualObj.get("hits").get("hits");
    hits.iterator().forEachRemaining(hit -> {
      Map<String, String> result = mapper.convertValue(hit.get("_source"), new TypeReference<Map<String, String>>() {
      });
      MatcherAssert.assertThat(result, is(expected.get(result.get("id"))));
    });

    MatcherAssert.assertThat("The size of data in index does not match",hits.size(), is(expected.size()));
  }

  @AfterAll
  public static void before_or_after_all() {
    container.close();
  }

  @When("a POST request is performed to the endpoint {string} with body")
  public void a_post_request_is_performed_to_the_endpoint_with_body(String path, String body) throws IOException, InterruptedException {
    String endpoint = String.format("%s%s",baseUrl, path);
    String response = performRequest(endpoint, null);
  }

}
