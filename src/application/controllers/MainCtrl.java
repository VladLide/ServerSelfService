package application.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Helper;
import application.LoadConfigFile;
import application.controllers.parts.TemplatePanelCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.enums.TableType;
import application.models.Configs;
import application.models.Info2Col;
import application.models.PackageSend;
import application.models.net.database.mysql.MySQL;
import application.models.net.database.mysql.interface_tables.ScaleItemMenu;
import application.models.net.database.mysql.tables.Codes;
import application.models.net.database.mysql.tables.Distribute;
import application.models.net.database.mysql.tables.Goods;
import application.models.net.database.mysql.tables.Sections;
import application.models.net.database.mysql.tables.Templates;
import application.models.net.ftp.FTP;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainCtrl {
	private Timer refresh;
	private MySQL db = null;
	private ObservableList<ScaleItemMenu> scales = FXCollections.observableArrayList();
	private ObservableList<PackageSend> packs = FXCollections.observableArrayList();
	private static MainCtrl instance = null;
	private static Map<String, String> mainSys;
	private static final Logger logger = LogManager.getLogger(MainCtrl.class);
	private static final String separatorKey = "separator";

	public MainCtrl(/* Stage mainStage */) {
		super();
	}

	public static MainCtrl getInstance() {
		if (instance == null) {
			instance = new MainCtrl();
		}
		return instance;
	}

	public static void openMainWindow(Stage stage) {
		MainWindowCtrl.loadView(stage);
		MainWindowCtrl.showStage();
		MainWindowCtrl.getSidebarCtrl().loadMenu(getScales());
		startRefresh();
	}

	public static void close() {
		MainCtrl me = getInstance();
		if (me.refresh != null)
			me.refresh.cancel();
		System.exit(0);
	}

	public static void startRefresh() {
		Distribute distribute = new Distribute(0, 0, 0, 0, 0);
		MainCtrl me = getInstance();

		AtomicInteger timeout = new AtomicInteger(0);

		me.refresh = new Timer();
		me.refresh.schedule(new TimerTask() {
			@Override
			public void run() {
				if (timeout.get() % 60000 == 0) {
					loadFiles();
					distribute.update();

					timeout.set(0);
				}

				if (!MainCtrl.getPacks().isEmpty()) {
					Platform.runLater(() -> {
						MainWindowCtrl.getFooterCtrl().startTask(MainCtrl.getPacks().get(0));
						MainCtrl.getPacks().remove(0);
					});
				}

				// loadFiles();

				timeout.incrementAndGet();
			}
		}, 0, 1000);
	}

	public static AnchorPane loadAnchorPane(AnchorPane anchorPane, FXMLLoader loader) throws IOException {
		AnchorPane load = loader.load();
		anchorPane.getChildren().add(load);
		AnchorPane.setBottomAnchor(load, 0.0);
		AnchorPane.setTopAnchor(load, 0.0);
		AnchorPane.setLeftAnchor(load, 0.0);
		AnchorPane.setRightAnchor(load, 0.0);
		return load;
	}

	public static BorderPane loadBorderPane(AnchorPane anchorPane, FXMLLoader loader) throws IOException {
		BorderPane load = loader.load();
		anchorPane.getChildren().add(load);
		AnchorPane.setBottomAnchor(load, 0.0);
		AnchorPane.setTopAnchor(load, 0.0);
		AnchorPane.setLeftAnchor(load, 0.0);
		AnchorPane.setRightAnchor(load, 0.0);
		return load;
	}

	public static MySQL getDB() {
		MainCtrl me = getInstance();
		if (me.db == null) {
			me.db = new MySQL(-1);
		}
		me.db.getDBConnection();
		return me.db;
	}

	public static ObservableList<ScaleItemMenu> getScales() {
		MainCtrl me = getInstance();
		if (me.scales.isEmpty()) {
			me.scales = ScaleItemMenu.get();
		}
		return me.scales;
	}

	public static ObservableList<ScaleItemMenu> refreshScales() {
		MainCtrl me = getInstance();
		me.scales.forEach(scale -> scale.getConnect().cancel());
		me.scales.clear();
		return getScales();
	}

	public static ObservableList<PackageSend> getPacks() {
		return getInstance().packs;
	}

	public static void addPacks(PackageSend pack) {
		getInstance().packs.add(pack);
	}

	public static void loadFiles() {
		String pathLoad = Configs.getItemStr("path_load");
		LoadFrom from = getInstance().getLoadFrom(pathLoad);
		String pathWithoutLoadIdentifier = pathLoad.substring(pathLoad.indexOf(":") + 1);
		List<LoadConfigFile> configs = getInstance().loadConfigs();
		getMainSys(configs);

		switch (from) {
		case FTP:
			loadFromFtp(pathWithoutLoadIdentifier);
			break;
		case LOCAL:
			loadFromLocal(pathWithoutLoadIdentifier, configs);
			break;
		default:
			throw new IllegalArgumentException("Wrong loadFrom type");
		}
	}

	/**
	 * Set value for global map variable mainSys
	 *
	 * @param configs is list with configs from "import" folder
	 */
	private static void getMainSys(List<LoadConfigFile> configs) {
		mainSys = configs.stream().filter(configFile -> configFile.getType() == null).findFirst()
				.orElseThrow(() -> new NullPointerException("Was not able to find main.sys file")).getMap();
	}

	/**
	 * Load data to database from local files
	 *
	 * @param path    path where to find data in csv format
	 * @param configs list with config data for data
	 */

	private static void loadFromLocal(String path, List<LoadConfigFile> configs) {
		try {
			// load from given path
			File folder = new File(path);
			File[] files = folder.listFiles();

			// if path is correct and files were found
			if (files != null) {
				for (File file : files) {
					// in load folder will be also images folder, so we need to skip it
					if (file.isDirectory())
						continue;

					String fileName = file.getName();
					// get to which table in database we need to load values
					TableType type = TableType.get(fileName.substring(0, fileName.lastIndexOf(".")));
					// get config file for such table
					LoadConfigFile configForFile = configs.stream().filter(config -> config.getType() == type)
							.findFirst().orElseThrow(() -> new NullPointerException(
									"Was not able to find config file for type " + type));

					String fileData = getInstance().readFile(file).orElseThrow(
							() -> new IOException("Was not able to get lines from file " + file.getAbsolutePath()));
					String[] lines = fileData
							// .replace("\n", "")
							.split(System.lineSeparator());// mainSys.get(separatorKey));

					// each line from file is object which is needed to be saved to database
					for (String line : lines) {
						if (!line.startsWith("#"))
							getInstance().saveObjectFromLine(configForFile, line, path);
					}
				}
			} else {
				throw new NullPointerException("Was not able to get files from path " + path);
			}
		} catch (IOException | NullPointerException | SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Load data to database from files from ftp
	 *
	 * @param path
	 */
	private static void loadFromFtp(String path) {
		// get host, port, user and pass
		String host = Configs.getItemStr("ftpHost");
		String port = Configs.getItemStr("ftpPort");
		String user = Configs.getItemStr("ftpUser");
		String password = Configs.getItemStr("ftpPass");

		// load from ftp
		try {
			FTP ftp = new FTP(host, port, user, password);
			FTPFile[] ftpFiles = ftp.getFtp().listFiles(path);

			// todo add what to do with files
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Get location from which we need to get data Local or FTP files
	 *
	 * @param pathWithIdentifier string which contains path and identifier of place
	 *                           where to get files
	 * @return LoadFrom enum with place where to get files
	 */
	private LoadFrom getLoadFrom(String pathWithIdentifier) {
		String indicator = pathWithIdentifier.substring(0, pathWithIdentifier.indexOf(":"));
		switch (indicator.toLowerCase(Locale.ROOT)) {
		case "ftp":
			return LoadFrom.FTP;
		case "local":
			return LoadFrom.LOCAL;
		default:
			throw new IllegalArgumentException("Wrong indicator in path " + pathWithIdentifier);
		}
	}

	/**
	 * Get configs for csv data file, in configs described how to get data for table
	 * column Each file must be written in such way: key=value key1=value1
	 *
	 * @return list of config files
	 */
	private List<LoadConfigFile> loadConfigs() {
		List<LoadConfigFile> list = new ArrayList<>();
		String path = System.getProperty("user.dir") + "/resources/system/import";
		File folder = new File(path);

		try {
			File[] files = folder.listFiles();
			if (files == null)
				throw new NullPointerException("No files or path is wrong " + folder.getAbsolutePath());

			for (File file : files) {
				Map<String, String> map = new HashMap<>();
				String fileInsides = readFile(file)
						.orElseThrow(() -> new IOException("Was not able to read file " + file.getAbsolutePath()));
				String[] lines = fileInsides.split(System.lineSeparator()/* "\n" */);

				for (String line : lines) {
					String[] keyValue = line.split("=");
					if (keyValue.length > 1) {
						map.put(keyValue[0], keyValue[1]);
					}
				}

				String nameWithExtension = file.getName();
				String name = nameWithExtension.substring(0, nameWithExtension.lastIndexOf("."));

				// get type of table which this config describes
				TableType type = TableType.get(name);
				// create config instance for give table type
				LoadConfigFile config = new LoadConfigFile(type, map);
				list.add(config);
			}
			return list;
		} catch (NullPointerException | IOException e) {
			logger.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Read given file object to one string
	 *
	 * @param file object from which we will read strings
	 * @return string which contains all data from file
	 */
	private Optional<String> readFile(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder builder = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}

			return Optional.of(builder.toString());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	/**
	 * Save object of given type to database table
	 *
	 * @param configFile contains table type and map to map data from line to
	 *                   database table column
	 * @param line       string which contains data of object
	 * @param path       string which contains path to folder where lays files with
	 *                   data and "images" folder
	 * @throws SQLException         if was not able to convert file to BLOB
	 * @throws IOException          if was not able to open/find folder/file
	 * @throws NullPointerException if was not able to find file/data
	 */
	private void saveObjectFromLine(LoadConfigFile configFile, String line, String path)
			throws SQLException, IOException, NullPointerException {
		String[] params = line.split(mainSys.get(separatorKey)/* "," */);// split line to individual parameters
		Map<String, String> paramsMap = configFile.getMap();// get hashMap which maps data in params to database columns
		MiniHelper miniHelper = new MiniHelper(params, paramsMap);

		switch (configFile.getType()) {
		case SCALES: {
			// todo add saving scales
			break;
		}
		case SECTIONS: {
			Sections sections = new Sections();
			int id = miniHelper.getInt("id");
			int id_up = miniHelper.getInt("id_up");
			int level = miniHelper.getInt("level");
			String name = miniHelper.getString("name");
			Blob img_data = miniHelper.getBlob("img_data", path);
			String description = miniHelper.getString("description");
			int number_s = miniHelper.getInt("number_s");
			int number_po = miniHelper.getInt("number_po");

			sections.setId(id);
			sections.setId_up(id_up);
			sections.setLevel(level);
			sections.setName(name);
			sections.setImg_data(img_data);
			sections.setDescription(description);
			sections.setNumber_s(number_s);
			sections.setNumber_po(number_po);

			logger.info("Sections save result id {}", sections.save(db));

			break;
		}
		case GOODS: {
			Map<String, String> map = new HashMap<>();
			for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
				try {
					map.put(entry.getKey(), miniHelper.getString(entry.getKey()));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			Goods goods = new Goods(map, new int[] { 0, 0 });
			Blob data = miniHelper.getBlob("data", path);
			goods.setDataBlob(data);
			logger.info("Goods save result id {}", goods.save(db));
			break;
		}
		case TEMPLATES: {
			int id = miniHelper.getInt("id");
			String name = miniHelper.getString("name");
			Templates templates = new Templates(id, name, null);
			File templateImage = new File(path + "/templates/" + (id + "-" + name + ".templ"));
			if (templateImage.exists()) {
				templates.readObjFile(new TemplatePanelCtrl(), db, templateImage);
			} else {
				// log error to error file
				logError(configFile.getType(), path, "Was not able to get file " + templateImage.getAbsolutePath());
			}
			break;
		}
		case CODES: {
			int id = miniHelper.getInt("id");
			String name = miniHelper.getString("name");
			String prefix_val = miniHelper.getString("prefix_val");
			String mask = miniHelper.getString("mask");

			Codes codes = new Codes(id, name, prefix_val, mask);
			codes.save(db);
			break;
		}
		case USERS:
			// todo fill
			break;
		case ACCESS:
			// todo fill
			break;
		case STOCKS:
			// todo fill
			break;
		case OBJECT_TARA:
			// todo fill
			break;
		case BOTS_TELEGRAM:
			// todo fill
			break;
		case USERS_TELEGRAM:
			// todo fill
			break;
		case DISTRIBUTE: {
			int id = miniHelper.getInt("id");
			int id_command = miniHelper.getInt("id_command");
			int id_type_table = miniHelper.getInt("id_type_table");
			int unique_item = miniHelper.getInt("unique_item");
			int id_scales = miniHelper.getInt("id_scales");
			int id_condition = miniHelper.getInt("id_condition");
			int id_templates = miniHelper.getInt("id_templates");
			int id_barcodes = miniHelper.getInt("id_barcodes");
			float price = miniHelper.getFloat("price");
			String description = miniHelper.getString("description");
			int batch = miniHelper.getInt("batch");

			Distribute distribute = new Distribute(id, id_command, id_type_table, unique_item, id_scales, id_condition,
					id_templates, id_barcodes, price, description, batch);
			logger.info("Distribute save result id {}", distribute.save(db));
			break;
		}
		}
	}

	private void logError(TableType type, String path, String message) throws IOException {
		File errorFolder = new File(path + "/errors");
		if (!errorFolder.mkdir())
			throw new IOException("Was not able to create folder " + errorFolder.getAbsolutePath());

		File errorFile = new File(path + "/errors/" + type.name().toLowerCase(Locale.ROOT) + ".txt");
		if (!errorFile.exists() && !errorFile.createNewFile())
			throw new IOException("Was not able to create file " + errorFile.getAbsolutePath());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile, true))) {
			writer.write(message);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Class contains methods that are frequently used in method saveObjectFromLine
	 * and inner variables params and paramsMap, it is made for passing less
	 * parameters to each method and better readability
	 */
	private static class MiniHelper {
		private final String[] params;
		private final Map<String, String> paramsMap;

		public MiniHelper(String[] params, Map<String, String> paramsMap) {
			this.params = params;
			this.paramsMap = paramsMap;
		}

		public String getString(String name) {
			return params[Integer.parseInt(paramsMap.get(name)) - 1];
		}

		public int getInt(String name) {
			try {
				return Integer.parseInt(params[Integer.parseInt(paramsMap.get(name)) - 1]);
			} catch (Exception e) {
				return 0;
			}
		}

		public float getFloat(String name) {
			return Float.parseFloat(params[Integer.parseInt(paramsMap.get(name)) - 1]);
		}

		public Blob getBlob(String name, String path) throws NullPointerException, IOException, SQLException {
			String pathToImages = path + "/images";
			File[] files = new File(pathToImages).listFiles();
			String fileName = getString(name);

			if (files == null)
				throw new NullPointerException("Was not able to get images from path " + pathToImages);
			for (File file : files) {
				if (removeExtension(file.getName()).equals(fileName)) {
					return Helper.fileToBlob(file);
				}
			}

			return null;
		}

		private String removeExtension(String fileName) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
	}

	private enum LoadFrom {
		FTP, LOCAL;
	}

	public static ObservableList<Object> getSettings() {
		ObservableList<Object> info = FXCollections.observableArrayList();
		for (String key : mainSys.keySet()) {
			info.add(new Info2Col(key, mainSys.get(key)));
		}
		return info;
	}

	public static String getSettings(String key) {
		return mainSys.get(key);
	}

	public static void setSettings(String key, String value) {
		MainCtrl.mainSys.put(key, value);
	}

}
