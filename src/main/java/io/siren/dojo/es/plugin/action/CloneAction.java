package io.siren.dojo.es.plugin.action;

import org.elasticsearch.action.ActionType;
import org.elasticsearch.action.index.IndexResponse;

public class CloneAction extends ActionType<IndexResponse> {
  public static final CloneAction INSTANCE = new CloneAction();
  public static final String NAME = "indices:admin/doc_clone/clone";
  public CloneAction() {
    super(NAME, IndexResponse::new);
  }
}
