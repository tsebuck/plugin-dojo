package io.siren.dojo.es.plugin;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.siren.dojo.es.plugin.action.CloneAction;
import io.siren.dojo.es.plugin.action.TransportCloneAction;
import io.siren.dojo.es.plugin.rest.CloneRestHandler;

public class DocumentClonePlugin extends Plugin implements ActionPlugin  {
  @Override
  public List<RestHandler> getRestHandlers(Settings settings, RestController restController, ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings, SettingsFilter settingsFilter,
                                           IndexNameExpressionResolver indexNameExpressionResolver, Supplier<DiscoveryNodes> nodesInCluster) {
    return List.of(new CloneRestHandler());
  }

  @Override
  public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
    List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> actionsHandlers = new ArrayList<>();

   actionsHandlers.add(new ActionHandler<>(CloneAction.INSTANCE, TransportCloneAction.class));
   return actionsHandlers;
  }
}
