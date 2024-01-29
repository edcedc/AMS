package com.yyc.ams.bean;

import com.hj.excellibrary.annotation.CellStyle;
import com.hj.excellibrary.annotation.ExcelReadCell;
import com.hj.excellibrary.annotation.ExcelTable;
import com.hj.excellibrary.annotation.ExcelWriteCell;

import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

@ExcelTable(sheetName = "Sheet1")
public class ExcelBean {

    @ExcelReadCell(name = "#")
    @ExcelWriteCell(writeIndex = 0, writeName = "#")
    @CellStyle(horizontalAlign = HorizontalAlignment.CENTER, verticalAlign = VerticalAlignment.CENTER, underline = FontUnderline.DOUBLE)
    public String uId;

    @ExcelReadCell(name = "EPC")
    @ExcelWriteCell(writeIndex = 1, writeName = "EPC")
    @CellStyle(horizontalAlign = HorizontalAlignment.CENTER, verticalAlign = VerticalAlignment.CENTER, underline = FontUnderline.DOUBLE)
    public String epc;

    @ExcelReadCell(name = "ScanDate")
    @ExcelWriteCell(writeIndex = 2, writeName = "ScanDate")
    @CellStyle(horizontalAlign = HorizontalAlignment.CENTER, verticalAlign = VerticalAlignment.CENTER, underline = FontUnderline.DOUBLE)
    public String crearDate;

    @ExcelReadCell(name = "Title")
    @ExcelWriteCell(writeIndex = 3, writeName = "Title")
    @CellStyle(horizontalAlign = HorizontalAlignment.CENTER, verticalAlign = VerticalAlignment.CENTER, underline = FontUnderline.DOUBLE)
    public String title;

    @ExcelReadCell(name = "Location")
    @ExcelWriteCell(writeIndex = 4, writeName = "Location")
    @CellStyle(horizontalAlign = HorizontalAlignment.CENTER, verticalAlign = VerticalAlignment.CENTER, underline = FontUnderline.DOUBLE)
    public String location;

    @Override
    public String toString() {
        return "ExcelBean{" +
                "uId='" + uId + '\'' +
                ", epc='" + epc + '\'' +
                ", crearDate='" + crearDate + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

}
