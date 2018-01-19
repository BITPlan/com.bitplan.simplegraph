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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

/**
 * wrapper for a Slide's image
 * @author wf
 *
 */
public class SlideImage {
  BufferedImage image;
  public Graphics2D graphics;
  double zoom;
  XSLFSlide slide;
  int width=800;
  int height=600;
  double pzoom=2.0;
  private File imageFile;
  static XMLSlideShow ppt = null;
  
  /**
   * create the slideImage
   * @param slide
   */
  public SlideImage(XSLFSlide slide) {
    this.slide=slide;
  }
  
  /**
   * prepare image for the given width and height with the given zoom factor
   * 
   * @param width
   *          - in pixel
   * @param height
   *          - in pixel
   * @param zoom
   *          - zoom factor for e.g. magnification
   */
  public void prepareImage() {
    Dimension dim = new Dimension(width,height);
    image = new BufferedImage((int) Math.ceil(dim.width * zoom),
        (int) Math.ceil(dim.height * zoom), BufferedImage.TYPE_INT_RGB);
    graphics = image.createGraphics();
    AffineTransform at = new AffineTransform();
    at.setToScale(zoom, zoom);
    graphics.setTransform(at);
    graphics.setPaint(Color.white);
    graphics.fill(new Rectangle2D.Float(0, 0, dim.width, dim.height));
  }
  
  private void draw(XSLFSlide slide, Graphics2D graphics) {
    if (ppt==null)
      ppt=new XMLSlideShow();
    XSLFSlide drawSlide = ppt.createSlide();
    drawSlide.importContent(slide);
    drawSlide.draw(graphics);
  }
  
  /**
   * draw the image 
   * @param withBackground
   * @return 
   */
  public BufferedImage drawImage(boolean withBackground) {
    prepareImage();
    if (withBackground)
      slide.draw(graphics);
    else
      draw(slide, graphics);
    return image;
  }
  
  /**
   * save me to the given file path
   * @param filepath
   * @return the file
   * @throws Exception
   */
  public File save(String filepath) throws Exception {
    imageFile = new File(filepath);
    FileOutputStream out = new FileOutputStream(imageFile);
    javax.imageio.ImageIO.write(image, "png", out);
    out.close();
    return imageFile;
  }
  
}
