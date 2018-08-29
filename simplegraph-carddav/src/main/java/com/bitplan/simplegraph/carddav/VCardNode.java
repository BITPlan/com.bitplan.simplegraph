package com.bitplan.simplegraph.carddav;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.VCardParameters;
import ezvcard.property.VCardProperty;

/**
 * a VCard Node
 * 
 * @author wf
 *
 */
public class VCardNode extends SimpleNodeImpl {
  VCard vcard; // the vcard to be wrapped

  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.carddav");

  /**
   * construct me
   * 
   * @param simpleGraph
   * @param nodeQuery
   * @param keys
   */
  public VCardNode(SimpleGraph simpleGraph, String nodeQuery, String[] keys) {
    super(simpleGraph, nodeQuery, keys);
    InputStream input;
    try {
      input = new URL(nodeQuery).openStream();
      List<VCard> vcards = Ezvcard.parse(input).all();
      if (vcards.size() == 1) {
        vcard = vcards.get(0);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    // set main vertex
    super.setVertexFromMap();
    Collection<VCardProperty> properties = vcard.getProperties();
    for (VCardProperty vproperty : properties) {
      addProperty(vproperty);
    }
  }

  /**
   * add a vertex and edge for the given vproperty
   * 
   * @param vproperty
   */
  protected void addProperty(VCardProperty vproperty) {
    String propName = vproperty.getClass().getSimpleName();
    Vertex propVertex = this.graph().addVertex(propName);
    // get name values
    try {
      Map<String, Object> valueMap = PropertyUtils.describe(vproperty);
      for (String valueName : valueMap.keySet()) {
        Object value = valueMap.get(valueName);
        if (value != null) {
          propVertex.property(valueName, value);
        }
      }
    } catch (IllegalAccessException | InvocationTargetException
        | NoSuchMethodException e) {
      LOGGER.log(Level.WARNING,
          "Can not get property value for bean " + propName, e);
    }
    this.getVertex().addEdge(propName.toLowerCase(), propVertex);

    VCardParameters params = vproperty.getParameters();
    for (Entry<String, List<String>> param : params) {
      propVertex.property(param.getKey(), param.getValue());
    }
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("vcard", vcard);
    return map;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

}
