/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplegraph
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.powerpoint;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * create a graph oriented PowerPoint System
 * @author wf
 *
 */
public class PowerPointSystem extends SimpleSystemImpl{

  /**
   * initialize me
   */
  public PowerPointSystem() {
    super.setName("PowerPointSystem");
    super.setVersion("0.0.1");
  }
  
  @Override
  public SimpleNode moveTo(String nodeQuery)  {
    SlideShowNode slideShow=new SlideShowNode(this,nodeQuery);
    if (this.getStartNode()==null)
      this.setStartNode(slideShow);
    return slideShow;
  }

  @Override
  public SimpleSystem connect(String ... params) throws Exception {
    // no connection settings needed
    return this;
  }

}
