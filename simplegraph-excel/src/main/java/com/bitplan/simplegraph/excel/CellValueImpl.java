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
package com.bitplan.simplegraph.excel;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * Excel specific implementation of the CellValue
 * @author wf
 *
 */
public class CellValueImpl implements CellValue {

  private Object value;
  private String formula=null;
  
  @Override
  public Object getValue() {
    return value;
  }
  
  @Override
  public String getFormula() {
    return formula;
  }

  /**
   * create me from a given Excel cell
   * @param cell
   */
  public CellValueImpl(XSSFCell cell) {
    CellType cellType = cell.getCellType();
    if (CellType.FORMULA == cellType) {
      formula=cell.getCellFormula();
      cellType = cell.getCachedFormulaResultType();
    }
    switch (cellType) {
    case BOOLEAN:
      value = cell.getBooleanCellValue();
      break;
    case NUMERIC:
      value = cell.getNumericCellValue();
      XSSFCellStyle cellStyle = cell.getCellStyle();
      if (cellStyle != null) {
        String format = cellStyle.getDataFormatString();
        if ("0".equals(format)) {
          Double d = (Double) value;
          value = d.longValue();
        }
      }
      break;
    case STRING:
      value = cell.getStringCellValue();
      break;
    case BLANK:
      break;
    case ERROR:
      value = cell.getErrorCellValue();
      break;

    // CELL_TYPE_FORMULA will never occur since we handle this
    // above
    case FORMULA:
      break;
    case _NONE:
      value = cell.toString();
      break;
    default:
      break;
    }

  }


}
