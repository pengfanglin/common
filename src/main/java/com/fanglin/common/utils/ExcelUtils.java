package com.fanglin.common.utils;


import com.fanglin.common.core.others.Excel;
import com.fanglin.common.core.others.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Excel导入导出工具类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 16:32
 **/
@Slf4j
public class ExcelUtils {

    private static FormulaEvaluator evaluator;

    /**
     * 导入Excel
     */
    public static List<Map<String, String>> importExcel(String fileName) {
        List<Map<String, String>> mapList = new LinkedList<>();
        //自动关闭资源
        try (
            InputStream in = new FileInputStream(fileName);
            //poi文件流
            POIFSFileSystem fs = new POIFSFileSystem(in)
        ) {
            //获得excel
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            evaluator = new HSSFFormulaEvaluator(wb);
            //获得工作簿
            HSSFSheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                Map<String, String> map = new LinkedHashMap<>();
                for (int j = 0; j < sheet.getRow(0).getPhysicalNumberOfCells(); j++) {
                    Object value = getCellFormatValue(row.getCell((short) j));
                    map.put(getCellFormatValue(sheet.getRow(0).getCell(j)).toString().trim(), value == null ? null : value.toString().trim());
                }
                mapList.add(map);
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BusinessException("excel读取失败:" + e.getMessage());
        }
        return mapList;
    }

    /**
     * 通过cell获取里面的值
     */
    private static Object getCellFormatValue(HSSFCell cell) {
        Object cellValue;
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellTypeEnum()) {
                // 如果当前Cell的Type为NUMERIC
                case NUMERIC:
                    Long longVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    //判断是否含有小数位.0
                    if (Double.parseDouble(longVal + ".0") == doubleVal) {
                        cellValue = longVal.toString();
                    } else {
                        cellValue = doubleVal;
                    }
                    break;
                case FORMULA: {
                    // 取得当前Cell的数值
                    cellValue = evaluator.evaluate(cell).getNumberValue();
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case STRING:
                    // 取得当前的Cell字符串
                    cellValue = cell.getStringCellValue();
                    break;
                case BLANK:
                    cellValue = null;
                    break;
                default:
                    cell.setCellType(CellType.STRING);
                    cellValue = cell.getStringCellValue();
                    break;
            }
        } else {
            cellValue = null;
        }
        return cellValue;
    }

    public static void exportExcel(HttpServletResponse response, String fileName, List<Excel> excels, List<?> data) {
        try {
            //创建一个excel文件
            HSSFWorkbook wb = new HSSFWorkbook();
            evaluator = new HSSFFormulaEvaluator(wb);
            //创建一个工作薄
            HSSFSheet sheet = wb.createSheet();
            //设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet.setColumnWidth((short) 3, 20 * 256);
            //第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short) 4, 20 * 256);
            //有得时候你想设置统一单元格的高度，就用这个方法
            sheet.setDefaultRowHeight((short) 300);
            //创建第一行标题
            HSSFRow head = sheet.createRow(0);
            //设置第一行的每一列的名称
            for (int i = 0; i < excels.size(); i++) {
                head.createCell((short) i).setCellValue(excels.get(i).getName());
            }
            //以下是EXCEL正文数据
            for (int i = 0; i < data.size(); i++) {
                //创建一个新的行
                HSSFRow row = sheet.createRow(i + 1);
                //循环通过反射，为每一列赋值
                for (int j = 0; j < excels.size(); j++) {
                    Field field = data.get(i).getClass().getDeclaredField(excels.get(j).getKey());
                    if (field != null) {
                        field.setAccessible(true);
                        Object va = field.get(data.get(i));
                        if (va == null) {
                            va = "";
                        }
                        //创建一个新的列
                        row.createCell((short) j).setCellValue(va.toString());
                    }
                }
            }
            if (OthersUtils.isEmpty(fileName)) {
                fileName = OthersUtils.createRandom(10) + "xls";
            }
            response.setContentType("application/x-msdownload;");
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
            wb.write(response.getOutputStream());
        } catch (Exception e) {
            log.warn("excel导出失败:{}", e.getMessage());
            throw new BusinessException("excel导出失败");
        }
    }
}
