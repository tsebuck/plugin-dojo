package io.siren.dojo.es.plugin.rest;

import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.List;

public class CloneRestHandler extends BaseRestHandler {
  @Override
  public String getName() {
    return "doc_clone";
  }

  @Override
  public List<Route> routes() {
    return List.of(new Route(RestRequest.Method.GET, "/{index}/_doc_clone"),
        new Route(RestRequest.Method.POST, "/{index}/_doc_clone"));
  }

  @Override
  protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) throws IOException {
    final String index = restRequest.param("index");
    System.out.println(index);
    return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.ACCEPTED, "Yey!!")) ;
  }

}
