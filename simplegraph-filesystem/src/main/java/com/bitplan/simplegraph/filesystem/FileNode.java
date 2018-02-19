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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import com.bitplan.simplegraph.core.SimpleNode;
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
   * default constructor for cache handling
   * @param fileSystem
   * @param keys
   */
  public FileNode(FileSystem fileSystem,String ... keys) {
    super(fileSystem,"file",keys);
  }
  
  /**
   * create me from a path
   * @param fileSystem - the FileSystem to create the FileNode for
   * @param path
   * @param keys 
   */
  public FileNode(FileSystem fileSystem,String path, String ... keys) {
    this(fileSystem,new File(path),keys);
  }

  /**
   * create me from a file
   * @param fileSystem
   * @param file
   */
  public FileNode(FileSystem fileSystem,File file,String ...keys) {
    super(fileSystem,"file",keys);
    this.fileSystem=fileSystem;
    if (file!=null) {
      this.file = file;
      this.ext = FilenameUtils.getExtension(file.getName());
    }
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("file", file);
    map.put("path", file.getPath());
    map.put("name", file.getName());
    map.put("size", file.length());
    map.put("ext", ext);
    map.put("lastModified", isoDateFormat.format(file.lastModified()));
    return map;
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
      FileNode parent = new FileNode(fileSystem,file.getParentFile());
      knit(parent,this);
      links = Stream.of(parent);
      break;
    case "files":
      if (file.isDirectory()) {
        List<SimpleNode> files = new ArrayList<SimpleNode>();
        for (File childFile : file.listFiles()) {
          FileNode childFileNode=new FileNode(fileSystem,childFile);
          knit(this,childFileNode);
          files.add(childFileNode);
        }
        links = files.stream();
      }
      break;
      default:
        throw new RuntimeException("unknown edgeName "+edgeName);
    }
    return links;
  }
  
  protected void knit(FileNode parent,FileNode child) {
    child.getVertex().addEdge("parent", parent.getVertex());
    parent.getVertex().addEdge("files", child.getVertex());
  }

}
