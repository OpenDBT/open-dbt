package com.highgo.opendbt.experiment.terminal.utils.doc2html.bean.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExcelHtmlResultDto extends DocHtmlDto {

	private List<ExcelSheetDto> sheetList;
	private Integer activeSheetIndex;
  @Override
  public String getHtml() {
    if (sheetList != null && !sheetList.isEmpty()) {
      StringBuilder result = new StringBuilder();
      for (ExcelSheetDto sheet : sheetList) {
        result.append(sheet.getHtml());
      }
      return result.toString();
    }
    return null;
  }
}
