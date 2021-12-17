package application.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;

import application.models.net.database.mysql.MySQL;
import application.models.net.database.mysql.tables.Goods;
import application.models.net.database.mysql.tables.Scales;

/**
 * 
 * @author viralpatel.net
 * 
 */
public class CSVLoader {

	private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String TABLE_REGEX = "\\$\\{table\\}";
	private static final String KEYS_REGEX = "\\$\\{keys\\}";
	private static final String VALUES_REGEX = "\\$\\{values\\}";

	private MySQL db;
	private Scales scale = null;
	/*
	 * private String encoding="windows-1251"; private char seprator=';';
	 */
	private Map<String, String> config = new HashMap<String, String>();
	private Map<String, String> colTable = new HashMap<String, String>();
	private Map<String, String> imgFiles = new HashMap<String, String>();

	/**
	 * Public constructor to build CSVLoader object with Connection details. The
	 * connection is closed on success or failure.
	 */
	public CSVLoader(Scales scale, MySQL db) {
		this.scale = scale;
		this.db = db;
		readConfigFile("configImport.sys");
	}

	public void readConfigFile(String name) {
		try {
			String jsonString = new String(Files.readAllBytes(Paths.get(Utils.getPath("sys", name))));
			String[] nextLine = {};
			if (jsonString != null) {
				if (jsonString.length() > 0)
					nextLine = jsonString.split(System.getProperty("line.separator"));
			}
			for (String v : nextLine) {
				String[] key = v.split("=");
				if (key[0].charAt(0) == '-') {
					String nameConfig = key[0].substring(1, key[0].length());
					if (key.length == 2)
						config.put(nameConfig, key[1]);
					else
						config.put(nameConfig, "");
				} else {
					if (key.length == 2)
						colTable.put(key[0], key[1]);
					else
						colTable.put(key[0], "");
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getHashGoods(String[] line, String file) {
		Map<String, String> hashGoods = new HashMap<String, String>();
		String folder = new File(file).getParent() + "/images/";
		colTable.forEach((k, v) -> {
			int i = -1;
			try {
				i = Integer.parseInt(v) - 1;
				if (line[i].length() == 0)
					i = -1;
			} catch (Exception e) {
				i = -1;
			}
			if (i >= 0) {
				if (k.compareToIgnoreCase("data") == 0) {
					String s = imgFiles.get(line[i]);
					if (s != null)
						hashGoods.put(k, folder + line[i] + "." + s);
				} else
					hashGoods.put(k, line[i]);
			} else {
				if (k.compareToIgnoreCase("id_barcodes") == 0)
					hashGoods.put(k, scale.getConfigItem(k));
				if (k.compareToIgnoreCase("id_templates") == 0)
					hashGoods.put(k, scale.getConfigItem(k));
			}
		});
		return hashGoods;
	}

	public Boolean loadCSV(String csvFile, String tableName, boolean truncateBeforeLoad) throws Exception {
		CSVReader csvReader = null;
		if (null == this.db) {
			throw new Exception("Not a valid connection.");
		}
		try {

			csvReader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8")),
					this.config.get("seprator").charAt(0));

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured while executing file. " + e.getMessage());
		}

		String[] nextLine;
		String fileError = "";
		String fileIgnor = "";
		try {
			if (truncateBeforeLoad) {
				// delete data from table before loading csv
				db.deleteAll(tableName);
			}
			while ((nextLine = csvReader.readNext()) != null) {
				if (!nextLine[0].startsWith("#")) {
					// nextLine[nextLine.length-1] = folder+nextLine[nextLine.length-1];
					int id = 0;
					// id=Goods.save(getHashGoods(nextLine,csvFile),this.db);
					try {
						id = Integer.parseInt(nextLine[Integer.parseInt(this.config.get("status")) - 1]);
					} catch (Exception e) {
						id = 0;
					}
					if (id == 0) {
						id = Goods.save(getHashGoods(nextLine, csvFile), this.db);
					} else {
						if (id != -1) {
							Goods g = new Goods(getHashGoods(nextLine, csvFile), new int[] { 0, 0 });
							id = g.delete(this.db);
						} else {
							db.deleteAll("goods");
						}
					}
					if (id <= 0) {
						fileError += StringUtils.join(nextLine, ";") + "-error\r";
					}
				} else {
					fileIgnor += StringUtils.join(nextLine, ";") + "-ignor\r";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			csvReader.close();
			csvReader = null;
			if (fileError.length() != 0) {
				String folderPath = new File(csvFile).getParent() + "/error/";
				File folder = new File(folderPath);
				if (!folder.exists()) {
					folder.mkdir();
				}
				String filePath = folderPath + "/" + new File(csvFile).getName();
				// out = new PrintWriter(new FileWriter(folder.toString() + "/" + fileName));
				fileError = fileIgnor + "\r" + fileError;
				Utils.writeFile(filePath, fileError, false);
			}
			Path fileToDeletePath = Paths.get(csvFile);
			Files.delete(fileToDeletePath);
			System.gc();
		}
		return true;
	}

	public char getSeprator() {
		return this.config.get("seprator").charAt(0);
	}

	public void setSeprator(char seprator) {
		this.config.put("seprator", String.valueOf(seprator));
	}

	public Boolean importGoods(String csvFile) throws Exception {
		File f = new File(csvFile);
		String csvContent = Utils.getFile(csvFile, this.config.get("encoding"));
		File folder = new File(f.getParent() + "/images/");
		/*
		 * if(!folder.exists()) {
		 * if(parentControler!=null)TextBox.openMessages(parentControler.getThisStage(),
		 * "imgDirNo", "warning", folder.getAbsolutePath());
		 * //System.out.println("Папка с картинками не найдена "+folder.getAbsolutePath(
		 * )); return false; }
		 */
		if (folder.exists()) {
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					String[] filename = file.getName().split("\\.(?=[^\\.]+$)"); // split filename from it's extension
					String patternString = this.config.get("seprator") + filename[0];//
					Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);
					Matcher matcher = pattern.matcher(csvContent);
					// boolean matches = matcher.matches();

					if (matcher.find()) { // matching defined filename
						/*
						 * System.out.println("File exist: "+filename[0]+"."+filename[1]); int code = 0;
						 * try { code = Integer.parseInt(filename[0]); }catch (Exception e) { code = 0;
						 * } /*Images image = new Images(code,f); image.setDescription(file.getName());
						 * int id = image.save(); if(id<1)id = image.getSelectId(); if(id<=0) { String
						 * newPath = new
						 * File(Utils.getPath("img",id+"."+image.getExtension())).getCanonicalPath();
						 * try { Utils.copyFile(file.getAbsolutePath(),newPath);
						 */
						imgFiles.put(filename[0], filename[1]);// csvContent =
																// matcher.replaceAll(this.config.get("seprator")+file.getName()+System.getProperty("line.separator"));
						// csvContent = csvContent.replace(patternString, ";"+id);
						/*
						 * } catch (IOException e) { // TODO Auto-generated catch block
						 * TextBox.openMessages(parentControler.getThisStage(), "addImgNo", "warning",
						 * newPath); //System.out.println("Ошибка при добавлении картинки "+newPath);
						 * e.printStackTrace(); } TextBox.openMessages(parentControler.getThisStage(),
						 * "addDBError", "error", filename[0]+"."+filename[1]);
						 * //System.out.println(filename[0]+"."+filename[1]
						 * +" ошибка при добавлении в БД"); }
						 */
					} else {
						// TextBox.openMessages(parentControler, "connectImgGoodsNo", "warning",
						// filename[0]+"."+filename[1]+" : "+patternString);
						System.out.println("Картинка " + filename[0] + "." + filename[1] + " не связана с товаром ("
								+ patternString + ")");
					}
				}
			}
		}
		// String patternString = ";.+;";
		// final Pattern p = Pattern.compile(patternString, Pattern.MULTILINE);
		// final Matcher m = p.matcher(csvContent);
		// csvContent = m.replaceAll("0");
		Utils.putFile(f.getParent() + "/temp.csv", csvContent);
		if (loadCSV(f.getParent() + "/temp.csv", "goods", false)) {
			return true;
		}
		/*
		 * }catch( Exception e ) {
		 * if(parentControler!=null)TextBox.openMessages(parentControler.getThisStage(),
		 * "importError", "error", e+""); //System.out.println("Exception: "+e); }
		 */
		return false;
	}
}
