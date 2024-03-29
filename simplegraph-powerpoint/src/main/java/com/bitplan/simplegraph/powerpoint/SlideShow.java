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
package com.bitplan.simplegraph.powerpoint;

import org.apache.poi.xslf.usermodel.XMLSlideShow;

/**
 * the slide show interface
 * @author wf
 *
 */
public interface SlideShow {
  public String getTitle();
  public void setTitle(String title);
  
  public void save() throws Exception;
  public Slide createSlide();
  
  // get the underlying Apache POI slideshow
  public XMLSlideShow getSlideshow();
}
