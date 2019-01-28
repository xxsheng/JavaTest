package javautils.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;

/**
 * Microsoft Excel工具类
 */
public class ExcelUtil {

	private static Hashtable<Object, Integer> chHt = new Hashtable<Object, Integer>();

	static {
		char[] ch = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		for (int i = 0; i < ch.length; i++) {
			chHt.put(ch[i], i);
		}
	}

	private static ExcelUtil instance;

	private ExcelUtil() {
		
	}

	private static synchronized void synInit() {
		if (instance == null) {
			instance = new ExcelUtil();
		}
	}

	public static ExcelUtil getInstance() {
		if (instance == null) {
			synInit();
		}
		return instance;
	}

	/**
	 * 打开文件
	 * @param filePath
	 * @return
	 */
	public HSSFWorkbook open(String filePath) {
		File file = new File(filePath);
		return read(file);
	}

	/**
	 * 读取文件
	 * @param file
	 * @return
	 */
	public HSSFWorkbook read(File file) {
		HSSFWorkbook workbook = null;
		FileInputStream is = null;
		POIFSFileSystem fs = null;
		try {
			is = new FileInputStream(file);
			fs = new POIFSFileSystem(is);
			workbook = new HSSFWorkbook(fs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return workbook;
	}

	/**
	 * 保存文件
	 * @param file
	 * @param workbook
	 * @return
	 */
	public boolean save(File file, HSSFWorkbook workbook) {
		if (file != null) {
			String filePath = file.getPath();
			return write(filePath, workbook);
		}
		return false;
	}

	/**
	 * 另存问
	 * @param filePath
	 * @param fileName
	 * @param workbook
	 * @return
	 */
	public boolean saveAs(String filePath, String fileName, HSSFWorkbook workbook) {
		File fileDirs = new File(filePath);
		if (!fileDirs.exists()) {
			fileDirs.mkdirs();
		}
		return write(filePath + fileName, workbook);
	}

	/**
	 * 写文件
	 * @param filePath
	 * @param workbook
	 * @return
	 */
	private boolean write(String filePath, HSSFWorkbook workbook) {
		boolean flag = false;
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(filePath);
			workbook.write(os);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(os != null) {
				try {
					os.flush();
					os.close();
				} catch (Exception e) {
				}
			}
		}
		return flag;
	}
	
	/**
	 * 获取行数
	 * @param sheet
	 * @return
	 */
	public static int getRowNum(HSSFSheet sheet) {
		return sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;
	}

	/**
	 * 获取行
	 * @param sheet
	 * @param rowNum
	 * @return
	 */
	public static HSSFRow getRow(HSSFSheet sheet, int rowNum) {
		HSSFRow row = null;
		if (rowNum > 0) {
			int rowIndex = rowNum - 1;
			row = sheet.getRow(rowIndex);
			if (row == null) {
				row = sheet.createRow(rowIndex);
			}
		}
		return row;
	}

	/**
	 * 获取列
	 * @param row
	 * @param cellName
	 * @return
	 */
	public static HSSFCell getCell(HSSFRow row, String cellName) {
		int cellIndex = getCellIndex(cellName);
		return row.getCell(cellIndex, Row.CREATE_NULL_AS_BLANK);
	}

	/**
	 * 获取列的值
	 * @param row
	 * @param cellName
	 * @return
	 */
	public static String getStringCellValue(HSSFRow row, String cellName) {
		int cellIndex = getCellIndex(cellName);
		HSSFCell cell = row.getCell(cellIndex);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		return cell.getStringCellValue();
	}

	/**
	 * 获取列索引
	 * @param cellName
	 * @return
	 */
	public static int getCellIndex(String cellName) {
		int cellIndex = -1;
		char[] c = cellName.toCharArray();
		if (c.length == 1) {
			cellIndex = chHt.get(c[0]);
		}
		if (c.length == 2) {
			cellIndex = (chHt.get(c[0]) + 1) * 26 + (chHt.get(c[1]) + 1) - 1;
		}
		return cellIndex;
	}

}