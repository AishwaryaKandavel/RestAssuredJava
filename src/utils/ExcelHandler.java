package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHandler {
	
	protected static Properties prop = new Properties();
	public static void initializeProps() throws IOException {
		FileInputStream fis = new FileInputStream(
				new File(System.getProperty("user.dir") + "/src/data/runConfig.properties"));
		prop.load(fis);
	}

	public static List<HashMap<Object, Object>> getExcelDataToMap(String excelName, String sheetName)
			throws Exception {
		initializeProps();
		List<HashMap<Object, Object>> result = new ArrayList<HashMap<Object,Object>>();
		File file;
		XSSFSheet sheet;
		if(excelName.equalsIgnoreCase("download_path")) {
			Path path = Paths.get(prop.getProperty(excelName));
			Optional<Path> opPath = Files.list(path).filter(p->!Files.isDirectory(p)).sorted((p1,p2)->Long.valueOf(p2.toFile().lastModified())
					.compareTo(p1.toFile().lastModified())).findFirst();
			if(opPath.isPresent())
				file = opPath.get().toFile();
			else
				throw new Exception();
			
		}else if(excelName.contains(".") && excelName.contains(":")) {
			file = new File(excelName);
		}else
			file = new File(System.getProperty("user.dir") + prop.getProperty(excelName));
		XSSFWorkbook wf = new XSSFWorkbook(file);
		try {
			int sheetVal = Integer.parseInt(sheetName);
			sheet = wf.getSheetAt(sheetVal);
		}catch(Exception e) { 
			sheet = wf.getSheet(sheetName);
		}
		
		Iterator<Row> rows = sheet.iterator();
		Row headerRow = rows.next();
		while(rows.hasNext()) {
			HashMap<Object, Object> keyValCols = new HashMap<Object, Object>();
			Row row = rows.next();
			for (int i=0; i<row.getPhysicalNumberOfCells();i++) {
				Object headerCell = headerRow.getCell(i).getStringCellValue();
				Object rowCell;
				try{
					rowCell = row.getCell(i).getStringCellValue();
				}catch(Exception e) {
					rowCell = (int)row.getCell(i).getNumericCellValue();
				}
				keyValCols.put(headerCell, rowCell);
			}
			result.add(keyValCols);
		}
		wf.close();
		return result;
	}
	public String updateValueInExcel(String columnToUpdate, String key, String keyColumn, String updatedValue) throws Exception {
		File file;
		Path path = Paths.get(prop.getProperty("download_path"));
		Optional<Path> opPath = Files.list(path).filter(p->!Files.isDirectory(p))
				.sorted((p1,p2)->Long.valueOf(p2.toFile().lastModified())
				.compareTo(p1.toFile().lastModified())).findFirst();
		if(opPath.isPresent())
			file = opPath.get().toFile();
		else
			throw new Exception();
		
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook wf = new XSSFWorkbook(fis);
		XSSFSheet sheet = wf.getSheetAt(0);
		
		Iterator<Row> rows = sheet.iterator();
		Row headerRow = rows.next();
		int columnCount = -1;
		int keyColumnCount = -1;
		for (int i =0; i<headerRow.getPhysicalNumberOfCells(); i++) {
			Cell cell = headerRow.getCell(i);
			if(cell.getStringCellValue().trim().equalsIgnoreCase(columnToUpdate))
				columnCount = i;
			if(cell.getStringCellValue().trim().equalsIgnoreCase(keyColumn))
				keyColumnCount = i;
		}
		while(rows.hasNext()) {
			Row row = rows.next();
			if(row.getCell(keyColumnCount).getStringCellValue().trim().equalsIgnoreCase(key)){
				Cell cellToUpdate = row.getCell(columnCount);
				cellToUpdate.setCellValue(updatedValue);
			}
		}
		FileOutputStream fos= new FileOutputStream(file);
		wf.write(fos);
		wf.close();
		return file.getAbsolutePath();
	}
}
