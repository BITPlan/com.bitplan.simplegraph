package com.bitplan.powerpoint;

import com.bitplan.filesystem.FileNode;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

public class PowerPointSystem extends SimpleSystemImpl{

  /**
   * initialize me
   */
  public PowerPointSystem() {
    super.setName("PowerPointSystem");
    super.setVersion("0.0.1");
  }
  
  @Override
  public SimpleNode moveTo(String nodeQuery) throws Exception {
    SlideShowNode slideShow=new SlideShowNode(this,nodeQuery);
    if (this.getStartNode()==null)
      this.setStartNode(slideShow);
    return slideShow;
  }

  @Override
  public SimpleSystem connect(String connectionString) throws Exception {
    // no connection needed
    return this;
  }

}
