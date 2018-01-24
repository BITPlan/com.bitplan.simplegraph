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

import java.awt.Color;

import org.apache.poi.xslf.usermodel.XSLFHyperlink;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

/**
 * slide 
 * @author wf
 *
 */
public interface Slide {
  
    public String getTitle();
    public void setTitle(String title);

    public String getNotes(String separator);
    public void setNotes(String text);
    
    public String getName();
    public String getText(String separator);

    public int getPageNo();
    public int getPages();
    
    public XSLFSlide getSlide();
    public XSLFTextBox addTextBox(int x,int y,int width,int height);
    public XSLFTextRun addText(XSLFTextShape shape,String text, double fontSize,Color color);
    public XSLFHyperlink addHyperlink(XSLFTextShape shape,String text,double fontSize, Color color, String link);

}
