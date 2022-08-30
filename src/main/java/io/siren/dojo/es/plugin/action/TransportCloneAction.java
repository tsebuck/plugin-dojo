package io.siren.dojo.es.plugin.action;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;

import io.siren.dojo.es.plugin.model.CloneRequest;

public class TransportCloneAction extends HandledTransportAction<CloneRequest, IndexResponse> {
  Client client;
  @Inject
  public TransportCloneAction(TransportService transportService, ActionFilters actionFilters, Client client) {
    super(CloneAction.NAME, transportService, actionFilters, CloneRequest::new);
    this.client = client;
  }

  @Override
  protected void doExecute(Task task, CloneRequest request, ActionListener<IndexResponse> actionListener) {
    SearchRequest searchRequest = new SearchRequest();

    searchRequest.indices(request.getIndex());

    IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds(request.getSrcId());
    SearchSourceBuilder builder = new SearchSourceBuilder();
    builder.query(idsQueryBuilder);
    searchRequest.source(builder);

    this.client.search(searchRequest, new Inserter(actionListener, request));
  }

  private class Inserter implements ActionListener<SearchResponse> {

    ActionListener<IndexResponse> listener;
    CloneRequest cloneRequest;

    public Inserter(ActionListener<IndexResponse> listener, CloneRequest cloneRequest) {
      this.listener = listener;
      this.cloneRequest = cloneRequest;
    }

    @Override
    public void onResponse(SearchResponse searchResponse) {
      IndexRequest ir = new IndexRequest();
      ir.index(cloneRequest.getIndex());
      ir.id(cloneRequest.getDstId());
      ir.source(searchResponse.getHits().getHits()[0].getSourceAsMap());
      ir.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
      client.index(ir, listener);
    }

    @Override
    public void onFailure(Exception e) {

    }
  }
}
