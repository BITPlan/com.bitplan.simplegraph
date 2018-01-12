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
package com.bitplan.filesystem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tinkerpop.gremlin.structure.T;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * a File in the File system
 * 
 * @author wf
 *
 */
public class FileNode extends SimpleNodeImpl {

  // File to be wrapped in a Node
  File file;
  // System to be used
  FileSystem fileSystem;
  
  transient String ext;
  
  final public static SimpleDateFormat isoDateFormat = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss");

  /**
   * create me from a path
   * @param fileSystem - the FileSystem to create the FileNode for
   * @param path
   */
  public FileNode(FileSystem fileSystem,String path) {
    this(fileSystem,new File(path));
  }

  /**
   * create me from a file
   * @param fileSystem
   * @param file
   */
  public FileNode(FileSystem fileSystem,File file) {
    super(fileSystem);
    this.fileSystem=fileSystem;
    this.file = file;
    this.ext = FilenameUtils.getExtension(file.getName());
    Date modified = new Date(file.lastModified());
    super.setVertex(fileSystem.graph().addVertex(T.label, "file", "path", file.getAbsolutePath(),
        "name", file.getName(), "size", file.length(), "ext", ext, "modified",
        modified));
  }

  @Override
  public Map<String, Object> getMap() {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("file", file);
    result.put("path", file.getAbsolutePath());
    result.put("name", file.getName());
    result.put("size", file.length());
    result.put("ext", ext);
    result.put("lastModified", isoDateFormat.format(file.lastModified()));
    return result;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    return inOrOut(edgeName);
  }
  
  @Override
  public Stream<SimpleNode> in(String edgeName) {
    return inOrOut(edgeName);
  }

  protected Stream<SimpleNode> inOrOut(String edgeName) {
    Stream<SimpleNode> links = Stream.of();
    switch (edgeName) {
    case "parent":
      FileNode parent = new FileNode(fileSystem,file.getParent());
      links = Stream.of(parent);
      break;
    case "files":
      if (file.isDirectory()) {
        List<SimpleNode> files = new ArrayList<SimpleNode>();
        for (File childFile : file.listFiles()) {
          files.add(new FileNode(fileSystem,childFile));
        }
        links = files.stream();
      }
      break;
    }
    return links;
  }

}
