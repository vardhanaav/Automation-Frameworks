package tester;

/**
 * @author Ashwin A. Vardhan
 */

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel {
	
	ArrayList<ArrayList<String>> al = new ArrayList<>();

	/**
	 * Reads the excel file and store it into 2D ArrayList
	 * @param filePath the path of the excel file to be read 
	 * @param fileName the name of the excel file to be read
	 * @return 2D ArrayList containing all the sheets inside the workbook
	 */
	ArrayList<ArrayList<String>> readFile(String filePath, String fileName) {
		
		int numSheet = 0;
		try {
			FileInputStream file = new FileInputStream(new File(filePath+fileName));

			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			
			Iterator<Sheet> it = workbook.iterator();
			while (it.hasNext()) {
				XSSFSheet sheet = (XSSFSheet) it.next();
				al.add(readSheet(sheet));
			}
			
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;

	}

	/**
	 * Reads each sheet inside an excel file (workbook) and converts it into an ArrayList
	 * @param sheet the particular sheet to be read
	 * @return ArrayList containing the sheet data
	 */
	ArrayList<String> readSheet(XSSFSheet sheet) {
//		System.out.println(sheet.getSheetName());
		DataFormatter formatter = new DataFormatter();
		ArrayList<String> al = new ArrayList<>();
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			//For each row, iterate through all the columns
			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {

				Cell cell = cellIterator.next();
				//Check the cell type and format accordingly
				switch (cell.getCellTypeEnum()) 
				{
				case NUMERIC:
//					System.out.print(cell.getNumericCellValue() + "\t");
//					al.add(String.valueOf(cell.getNumericCellValue()));
					al.add(String.valueOf(formatter.formatCellValue(cell)));
					break;
				case STRING:
					String val = String.valueOf(formatter.formatCellValue(cell));
//					System.out.print(val + "\t");
					if (!val.isEmpty() && !val.toUpperCase().equals(val)) {
						al.add(val);
					}
					break;
				default:
					break;
				}
			}
//			System.out.println();
		}
//		System.out.println();
		return al;
	}
	

	public static void main(String[] args) {

		String filePath = ""; //current path
		String fileName = "dummy.xlsx";

		ReadExcel obj = new ReadExcel();
		obj.al = new ArrayList<>();
		obj.al = obj.readFile(filePath, fileName);
		System.out.println(obj.al);
	}
}
