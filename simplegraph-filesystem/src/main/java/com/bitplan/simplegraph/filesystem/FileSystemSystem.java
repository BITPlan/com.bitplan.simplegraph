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
package com.bitplan.simplegraph.filesystem;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * implements FileSystem access via a simple graph
 * @author wf
 *
 */
public class FileSystem extends com.bitplan.simplegraph.impl.SimpleSystemImpl {

  /**
   * initialize me
   */
  public FileSystem() {
    super.setName("FileSystem");
    super.setVersion("0.0.1");
  }

  @Override
  public FileNode moveTo(String nodeQuery, String ...keys)  {
    FileNode file=new FileNode(this,nodeQuery,keys);
    if (this.getStartNode()==null)
      this.setStartNode(file);
    return file;
  }
  
  @Override
  public FileSystem connect(String ... params) {
    // TODO might make e.g. remote File systems accessible
    return this;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return FileNode.class;
  }

}
