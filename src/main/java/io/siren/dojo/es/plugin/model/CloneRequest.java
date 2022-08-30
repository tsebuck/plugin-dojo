package io.siren.dojo.es.plugin.model;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.IndicesRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.xcontent.ObjectParser;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;

public class CloneRequest extends ActionRequest implements ToXContentObject, IndicesRequest {

  public static final ObjectParser<CloneRequest, Void> PARSER = new ObjectParser<>("clone");

  public CloneRequest() {
  }

  public CloneRequest(StreamInput in) throws IOException {
    super(in);
  }

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  private String index;

  public String getSrcId() {
    return srcId;
  }

  public void setSrcId(String srcId) {
    this.srcId = srcId;
  }

  public String getDstId() {
    return dstId;
  }

  public void setDstId(String dstId) {
    this.dstId = dstId;
  }

  private String srcId;
  private String dstId;

  @Override
  public ActionRequestValidationException validate() {
    return null;
  }

  @Override
  public String[] indices() {
    return new String[]{index};
  }

  @Override
  public IndicesOptions indicesOptions() {
    return null;
  }

  @Override
  public XContentBuilder toXContent(XContentBuilder xContentBuilder, Params params) throws IOException {
    xContentBuilder.startObject("clone");
    xContentBuilder.field("index", this.index);
    xContentBuilder.field("src_id", this.srcId);
    xContentBuilder.field("dst_id", this.dstId);
    xContentBuilder.endObject();
    return xContentBuilder;
  }
  static {
    PARSER.declareString(CloneRequest::setSrcId, new ParseField("src_id"));
    PARSER.declareStringOrNull(CloneRequest::setDstId, new ParseField("dst_id"));
  }
}
