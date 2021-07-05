package application.controllers;

import application.Helper;
import application.LoadConfigFile;
import application.controllers.parts.TemplatePanelCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.enums.TableType;
import application.models.Configs;
import application.models.PackageSend;
import application.models.net.ConfigNet;
import application.models.net.ftp.FTP;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainCtrl {
	private Timer refresh;
	private MySQL db = null;
	private ObservableList<ScaleItemMenu> scales = FXCollections.observableArrayList();
	private ObservableList<PackageSend> packs = FXCollections.observableArrayList();
	private static MainCtrl instance = null;
	private final static Logger logger = LogManager.getLogger(MainCtrl.class);
	private final static String separatorKey = "separator";
	private static Map<String, String> mainSys;

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
					distribute.update();
					timeout.set(0);
				}

				if (!MainCtrl.getPacks().isEmpty()) {
					Platform.runLater(() -> {
						MainWindowCtrl.getFooterCtrl().startTask(MainCtrl.getPacks().get(0));
						MainCtrl.getPacks().remove(0);
					});
				}

				loadFiles();

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
		String path = pathLoad.substring(pathLoad.indexOf(":") + 1);
		List<LoadConfigFile> configs = getInstance().loadConfigs();
		mainSys = configs
				.stream()
				.filter(configFile -> configFile.getType() == null)
				.findFirst()
				.orElseThrow(() -> new NullPointerException("Was not able to find main.sys file"))
				.getMap();

		assert configs != null;

		switch (from) {
			case FTP:
				loadFromFtp(path);
				break;
			case LOCAL:
				loadFromLocal(path, configs);
				break;
			default:
				throw new IllegalArgumentException("Wrong loadFrom type");
		}
	}

	private static void loadFromLocal(String path, List<LoadConfigFile> configs) {
		try {
			//load from given path
			File folder = new File(path);
			File[] files = folder.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) continue;

					String fileName = file.getName();
					TableType type = TableType.get(fileName.substring(0, fileName.lastIndexOf(".")));
					LoadConfigFile configForFile = configs
							.stream()
							.filter(config -> config.getType() == type)
							.findFirst()
							.orElseThrow(() -> new NullPointerException("Was not able to find config file for type " + type));
					String fileInsides = getInstance().readFile(file)
							.orElseThrow(() -> new IOException("Was not able to get lines from file " + file.getAbsolutePath()));
					String[] lines = fileInsides.replace("\n", "")
							.split(mainSys.get(separatorKey));

					for (String line : lines) {
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

	private static void loadFromFtp(String path) {
		//get host, port, user and pass
		String host = Configs.getItemStr("ftpHost");
		String port = Configs.getItemStr("ftpPort");
		String user = Configs.getItemStr("ftpUser");
		String password = Configs.getItemStr("ftpPass");

		//load from ftp
		try {
			FTP ftp = new FTP(host, port, user, password);
			FTPFile[] ftpFiles = ftp.getFtp().listFiles(path);

			//todo add what to do with files
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private LoadFrom getLoadFrom(String path) {
		String indicator = path.substring(0, path.indexOf(":"));
		switch (indicator.toLowerCase(Locale.ROOT)) {
			case "ftp":
				return LoadFrom.FTP;
			case "local":
				return LoadFrom.LOCAL;
			default:
				throw new IllegalArgumentException("Wrong indicator in path " + path);
		}
	}

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
				String[] lines = fileInsides.split("\n");

				for (String line : lines) {
					String[] keyValue = line.split("=");

					map.put(keyValue[0], keyValue[1]);
				}

				String nameWithExtension = file.getName();
				String name = nameWithExtension.substring(0, nameWithExtension.lastIndexOf("."));
				list.add(new LoadConfigFile(TableType.get(name), map));
			}
			return list;
		} catch (NullPointerException | IOException e) {
			logger.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

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

	private void saveObjectFromLine(LoadConfigFile configFile, String line, String path)
			throws SQLException, IOException {
		String[] params = line.split(",");
		Map<String, String> paramsMap = configFile.getMap();

		switch (configFile.getType()) {
			case SCALES: {
				//todo add saving scales
				break;
			}
			case SECTIONS: {
				Sections sections = new Sections();
				int id = getInt(params, paramsMap, "id");
				int id_up = getInt(params, paramsMap, "id_up");
				int level = getInt(params, paramsMap, "level");
				String name = getString(params, paramsMap, "name");
				Blob img_data = getBlob(params, paramsMap, "img_data", path);
				String description = getString(params, paramsMap, "description");
				int number_s = getInt(params, paramsMap, "number_s");
				int number_po = getInt(params, paramsMap, "number_po");

				sections.setId(id);
				sections.setId_up(id_up);
				sections.setLevel(level);
				sections.setName(name);
				sections.setImg_data(img_data);
				sections.setDescription(description);
				sections.setNumber_s(number_s);
				sections.setNumber_po(number_po);

				logger.info("Savingn sections with id {}", sections.save(db));
				break;
			}
			case GOODS: {
				Map<String, String> map = new HashMap<>();
				for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
					map.put(entry.getKey(), getString(params, paramsMap, entry.getKey()));
				}

				Goods goods = new Goods(map, new int[]{0, 0});
				Blob data = getBlob(params, paramsMap, "data", path);
				goods.setDataBlob(data);
				logger.info("Goods save result id {}", goods.save(db));
				break;
			}
			case TEMPLATES: {
//				int id = getInt(params, paramsMap, "id");
//				String name = getString(params, paramsMap, "name");
//				File idName = new File(path + "/templates/" + (id + "-" + name + ".tmpl"));
//
//				if (idName.exists()) {
//					Templates templates = new Templates(id, name, null);
//					templates.readObjFile(new TemplatePanelCtrl(), db, idName);
//				} else {
//					//save error to error folder
//				}

				break;
			}
			case CODES: {

				break;
			}
			case USERS:
				//todo fill
				break;
			case ACCESS:
				//todo fill
				break;
			case STOCKS:
				//todo fill
				break;
			case OBJECT_TARA:
				//todo fill
				break;
			case BOTS_TELEGRAM:
				//todo fill
				break;
			case USERS_TELEGRAM:
				//todo fill
				break;
			case DISTRIBUTE: {
				int id = getInt(params, paramsMap, "id");
				int id_command = getInt(params, paramsMap, "id_command");
				int id_type_table = getInt(params, paramsMap, "id_type_table");
				int unique_item = getInt(params, paramsMap, "unique_item");
				int id_scales = getInt(params, paramsMap, "id_scales");
				int id_condition = getInt(params, paramsMap, "id_condition");
				int id_templates = getInt(params, paramsMap, "id_templates");
				int id_barcodes = getInt(params, paramsMap, "id_barcodes");
				float price = getFloat(params, paramsMap, "price");
				String description = getString(params, paramsMap, "description");
				int batch = getInt(params, paramsMap, "batch");

				Distribute distribute = new Distribute(
						id, id_command, id_type_table,
						unique_item, id_scales, id_condition,
						id_templates, id_barcodes, price,
						description, batch);
				distribute.save(db);
				break;
			}
		}
	}

	private String getString(String[] params, Map<String, String> map, String name) {
		return params[Integer.parseInt(map.get(name)) - 1];
	}

	private int getInt(String[] params, Map<String, String> map, String name) {
		return Integer.parseInt(params[Integer.parseInt(map.get(name)) - 1]);
	}

	private float getFloat(String[] params, Map<String, String> map, String name) {
		return Float.parseFloat(params[Integer.parseInt(map.get(name)) - 1]);
	}

	private Blob getBlob(String[] params, Map<String, String> map, String name, String path)
			throws NullPointerException, IOException, SQLException {
		String pathToImages = path + "/images";
		File[] files = new File(pathToImages).listFiles();
		String fileName = getString(params, map, name);

		if (files == null) throw new NullPointerException("Was not able to get images from path " + pathToImages);
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

//	private void logError(TableType type, String path, String message) throws IOException {
//		File errorFolder = new File(path + "/errors");
//		if (!errorFolder.mkdir()) throw new IOException("Was not able to create folder " + errorFolder.getAbsolutePath());
//
//		File errorFile = new File(path + "/errors/" + type.name().toLowerCase(Locale.ROOT) + ".txt");
//		if (!errorFile.exists() && !errorFile.createNewFile())
//			throw new IOException("Was not able to create file " + errorFile.getAbsolutePath());
//		BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile), );
//	}

	private enum LoadFrom {
		FTP,
		LOCAL;
	}
}
