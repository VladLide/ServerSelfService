package application.models.net.mysql.tables;

import application.Converter;
import application.controllers.MainCtrl;
import application.models.net.PackingDBValue;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.SqlQueryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Distribute {
	private int id;
	private int id_command = 1;
	private int id_type_table = 1;
	private Integer unique_item = 0;
	private int id_scales = 0;
	private int id_condition = 0;
	private int id_templates = 0;
	private int id_barcodes = 0;
	private float price = 0.0F;
	private String description = "";
	private boolean batch = false;
	private Timer refreshTimer;
	private static final String table = "distribute";
	private static final String orderByColumn = "id_type_table";
	private static final Logger logger = LogManager.getLogger(Distribute.class);
	private static final Map<Integer, Command> intCommandMap = new HashMap<>();
	private static final Map<Integer, TypeTable> intTypeTableMap = new HashMap<>();
	private final IllegalArgumentException wrongTypeTable = new IllegalArgumentException("Wrong TypeTable");
	private final NullPointerException noColumns = new NullPointerException("Was not able to obtain columns");
	private final NullPointerException noValues = new NullPointerException("Was not able to obtain packingDbValues");

	public Distribute(int id, int id_command, int id_type_table, Integer unique_item, int id_scales) {
		this.id = id;
		this.id_command = id_command;
		this.id_type_table = id_type_table;
		this.unique_item = unique_item;
		this.id_scales = id_scales;

		//fill intCommand hashMap
		intCommandMap.put(1, Command.CREATE);
		intCommandMap.put(2, Command.UPDATE);
		intCommandMap.put(3, Command.DELETE);

		//fill intTypeTable hasMap
		intTypeTableMap.put(1, TypeTable.SCALES);
		intTypeTableMap.put(2, TypeTable.SECTIONS);
		intTypeTableMap.put(3, TypeTable.GOODS);
		intTypeTableMap.put(4, TypeTable.TEMPLATES);
		intTypeTableMap.put(5, TypeTable.CODES);
		intTypeTableMap.put(6, TypeTable.USERS);
		intTypeTableMap.put(7, TypeTable.ACCESS);
		intTypeTableMap.put(8, TypeTable.STOCKS);
		intTypeTableMap.put(9, TypeTable.OBJECTS_TARA);
		intTypeTableMap.put(10, TypeTable.BOTS_TELEGRAM);
		intTypeTableMap.put(11, TypeTable.USERS_TELEGRAM);
	}

	public Distribute(ResultSet res) {
		try {
			this.id = res.getInt(1);
			this.id_command = res.getInt(2);
			this.id_type_table = res.getInt(3);
			this.unique_item = res.getInt(4);
			this.id_scales = res.getInt(5);
			this.id_condition = res.getInt(6);
			this.id_templates = res.getInt(7);
			this.id_barcodes = res.getInt(8);
			this.price = res.getFloat(9);
			this.description = res.getString(10);
			//in database column is tinyInt, so 0 -> false, non-zero -> true
			this.batch = res.getInt(11) != 0;
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void start() {
		if (refreshTimer == null) {
			refreshTimer = new Timer();
		}

		refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Starting a timer");
				update();
			}
		}, 0, 1000);
	}

	public void update() {
		logger.info("Updating...");
		//get data from distribute table in server db
		MySQL getFromDb = MainCtrl.getDB();
		List<Distribute> everything = getEverything(getFromDb);
		logger.info("Data from distribute table {}", everything);

		//if we got anything
		if (!everything.isEmpty()) {
			for (Distribute distribute : everything) {
				logger.info("Getting db for scale_id {}", distribute.getId_scales());
				MySQL sendToDb = new MySQL(distribute.getId_scales());
				sendToDb.getDBConnection();
				Integer uniqueItem = distribute.getUnique_item();
				TypeTable type = intTypeTableMap.get(distribute.getId_type_table());
				logger.info("Unique item {}", uniqueItem);

				if (uniqueItem != null && uniqueItem != 0) {
					logger.info("Getting one item by id {} from table {}", uniqueItem, type.table);
					//get one item and save it on scale
					Object item = getItem(uniqueItem, getFromDb, type)
							.orElseThrow(() -> new NullPointerException("No item was got from table " + type.table));
					String[] columns = getColumns(item, type)
							.orElseThrow(() -> new NullPointerException("Was not able to obtain columns"));
					PackingDBValue[] values = PackingDBValue.get(item)
							.orElseThrow(() -> new NullPointerException("Was not able to obtain values"));
					switch (intCommandMap.get(distribute.getId_command())) {
						case CREATE:
							logger.info("Creating item...");
							//create item on scale;
							sendToDb.insert(type.table, columns, values);
							break;
						case UPDATE:
							logger.info("Updating item");
							//update item on scale;
							String[] where = getWhere(item, type)
									.orElseThrow(() -> new NullPointerException("Was not able to obtain WHERE clause"));
							sendToDb.update(type.table, columns, values, where);
							break;
						case DELETE:
							logger.info("Deleting item");
							//delete item on scale;
							sendToDb.delete(type.table, uniqueItem);
							break;
						default:
							logger.error("Wrong command id");
					}
				} else {
					logger.info("Getting many items from table {}", type.table);
					//get all items of given TableType
					List<Object> list = getItems(type, getFromDb);

					switch (intCommandMap.get(distribute.getId_command())) {
						case CREATE:
							logger.info("Creating items...");
							//insert items on scale
							insertItems(list, sendToDb, type);
							break;
						case UPDATE:
							logger.info("Updating items...");
							sendToDb.deleteAll(type.table);
							insertItems(list, sendToDb, type);
							break;
						case DELETE:
							logger.info("Deleting items...");
							//delete items from scale
							sendToDb.deleteAll(type.table);
							break;
						default:
							logger.error("Wrong command id");
					}
				}

				MainCtrl.getDB().delete("distribute", distribute.getId());
			}
		}
	}

	private Optional<Object> getItem(Integer uniqueItem, MySQL db, TypeTable type) {
		switch (type) {
			case SCALES:
				return Optional.of(Scales.get(uniqueItem, "", db));
			case SECTIONS:
				return Optional.of(Sections.get(uniqueItem, -1, 0, "", true, db));
			case GOODS:
				return Optional.of(Goods.get(0, uniqueItem, "", 0, 0, db));
			case TEMPLATES:
				return Optional.of(Templates.get(uniqueItem, "", true, db));
			case CODES:
				return Optional.of(Codes.get(uniqueItem, "", db));
			case USERS:
				return Optional.of(Users.get(uniqueItem, "", "", "", "", db));
			case ACCESS:
				return Optional.empty();
			case STOCKS:
				return Optional.of(Stocks.get(uniqueItem, db));
			case OBJECTS_TARA:
				return Optional.of(ObjectsTara.get(0, uniqueItem, "", db));
			case BOTS_TELEGRAM:
				return Optional.of(BotsTelegram.get(uniqueItem, "", "", "", -1, db));
			case USERS_TELEGRAM:
				return Optional.of(UsersTelegram.get(uniqueItem, "", "", "", db));
			default:
				throw wrongTypeTable;
		}
	}

	private void insertItems(List<Object> list, MySQL db, TypeTable type) {
		try {
			String[] columns = getColumns(list.get(0), type).orElseThrow(() -> noColumns);
			List<PackingDBValue[]> packingDBValues = new ArrayList<>();

			for (Object object : list) {
				PackingDBValue[] values = PackingDBValue.get(object).orElseThrow(() -> noValues);
				packingDBValues.add(values);
			}

			db.insertAll(type.table, columns, packingDBValues);
		} catch (IllegalArgumentException | NullPointerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static Distribute get(int idCommand,
	                             int idTypeTable,
	                             Integer uniqueItem,
	                             int idScales,
	                             MySQL db) {
		List<Distribute> result = Distribute.getList(idCommand, idTypeTable, uniqueItem, idScales, db);
		return result.isEmpty() ? null : result.get(0);
	}

	public static List<Distribute> getEverything(MySQL db) {
		List<Distribute> list = new ArrayList<>();

		try {
			SqlQueryBuilder builder = new SqlQueryBuilder(db, "distribute");
			ResultSet resultSet = builder.select("*").from("distribute").execute();
			while (resultSet.next()) {
				list.add(new Distribute(resultSet));
			}

		} catch (SQLException | NullPointerException e) {
			logger.error(e.getMessage(), e);
		}

		return list;
	}

	public static List<Distribute> getList(int idCommand,
	                                       int idTypeTable,
	                                       Integer uniqueItem,
	                                       int idScales,
	                                       MySQL db) {
		List<Distribute> rows = new ArrayList<>();

		try {
			ResultSet result = getResultSet(idCommand, idTypeTable, uniqueItem, idScales, db)
					.orElseThrow(() -> new NullPointerException("Was not able to execute query"));

			while (result.next()) {
				rows.add(new Distribute(result));
			}
		} catch (NullPointerException | SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return rows;
	}

	private static Map<String, String> getColumnValueMap(int id_command,
	                                                     int id_type_table,
	                                                     Integer unique_item,
	                                                     int id_scales) {
		Map<String, String> map = new HashMap<>();
		if (id_command != 0)
			map.put("id_command", String.valueOf(id_command));
		if (id_type_table != 0)
			map.put("id_type_table", String.valueOf(id_type_table));
		if (unique_item != null)
			map.put("unique_item", String.valueOf(unique_item));
		if (id_scales != 0)
			map.put("id_scales", String.valueOf(id_scales));
		return map;
	}

	public static Optional<ResultSet> getResultSet(int idCommand, int idTypeTable, Integer uniqueItem,
	                                               int idScales, MySQL db) {
		Map<String, String> map = getColumnValueMap(idCommand, idTypeTable, uniqueItem, idScales);
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder(db, table);
		queryBuilder.select("*").from(table);
		if (!map.isEmpty()) queryBuilder.where(map);
		queryBuilder.orderBy(table, orderByColumn);

		return Optional.of(queryBuilder.execute());
	}

	public int getId_command() {
		return id_command;
	}

	public void setId_command(int id_command) {
		this.id_command = id_command;
	}

	public int getId_type_table() {
		return id_type_table;
	}

	public void setId_type_table(int id_type_table) {
		this.id_type_table = id_type_table;
	}

	public Integer getUnique_item() {
		return unique_item;
	}

	public void setUnique_item(Integer unique_item) {
		this.unique_item = unique_item;
	}

	public int getId_scales() {
		return id_scales;
	}

	public void setId_scales(int id_scales) {
		this.id_scales = id_scales;
	}

	public int getId_condition() {
		return id_condition;
	}

	public void setId_condition(int id_condition) {
		this.id_condition = id_condition;
	}

	public int getId_templates() {
		return id_templates;
	}

	public void setId_templates(int id_templates) {
		this.id_templates = id_templates;
	}

	public int getId_barcodes() {
		return id_barcodes;
	}

	public void setId_barcodes(int id_barcodes) {
		this.id_barcodes = id_barcodes;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isBatch() {
		return batch;
	}

	public void setBatch(boolean batch) {
		this.batch = batch;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private Optional<String[]> getColumns(Object object, TypeTable type) throws IllegalArgumentException {
		switch (type) {
			case SCALES:
				return Optional.of(((Scales) object).getFields().toArray(new String[0]));
			case SECTIONS:
				return Optional.of(((Sections) object).getFields().toArray(new String[0]));
			case GOODS:
				return Optional.of(((Goods) object).getFields().toArray(new String[0]));
			case TEMPLATES:
				return Optional.of(((Templates) object).getFields(true).toArray(new String[0]));
			case CODES:
				return Optional.of(((Codes) object).getFields().toArray(new String[0]));
			case USERS:
				return Optional.of(((Users) object).getFields().toArray(new String[0]));
			case ACCESS:
				return Optional.empty();
			case STOCKS:
				return Optional.of(((Stocks) object).getFields().toArray(new String[0]));
			case OBJECTS_TARA:
				return Optional.of(((ObjectsTara) object).getFields().toArray(new String[0]));
			case BOTS_TELEGRAM:
				return Optional.of(((BotsTelegram) object).getFields().toArray(new String[0]));
			case USERS_TELEGRAM:
				return Optional.of(((UsersTelegram) object).getFields().toArray(new String[0]));
			default:
				throw wrongTypeTable;
		}
	}

	private List<Object> getItems(TypeTable type, MySQL getFromDb) {
		List<Object> list = null;
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder(getFromDb, type.table);
		ResultSet resultSet = queryBuilder.select("*").from(type.table).execute();

		try {
			switch (type) {
				case SCALES:
					list = new ArrayList<>(Converter.fromResultSetToScalesList(resultSet));
					break;
				case SECTIONS:
					list = new ArrayList<>(Converter.fromResultSetToSectionsList(resultSet));
					break;
				case GOODS:
					list = new ArrayList<>(Converter.fromResultSetToGoodsList(resultSet));
					break;
				case TEMPLATES:
					list = new ArrayList<>(Converter.fromResultSetToTemplatesList(resultSet));
					break;
				case CODES:
					list = new ArrayList<>(Converter.fromResultSetToCodesList(resultSet));
					break;
				case USERS:
					//todo empty case
					break;
				case ACCESS:
					//todo empty case
					break;
				case STOCKS:
					//todo empty case
					break;
				case OBJECTS_TARA:
					//todo empty case
					break;
				case BOTS_TELEGRAM:
					//todo empty case
					break;
				case USERS_TELEGRAM:
					//todo empty case
					break;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

		return list;
	}

	private Optional<String[]> getWhere(Object object, TypeTable type) throws IllegalArgumentException {
		switch (type) {
			case SCALES:
				return Optional.of(new String[]{type.table + ".id=" + ((Scales) object).getId()});
			case SECTIONS:
				return Optional.of(new String[]{type.table + ".id=" + ((Sections) object).getId()});
			case GOODS:
				return Optional.of(new String[]{type.table + ".pre_code=" + ((Goods) object).getPre_code()});
			case TEMPLATES:
				return Optional.of(new String[]{type.table + ".id=" + ((Templates) object).getId()});
			case CODES:
				return Optional.of(new String[]{type.table + ".id=" + ((Codes) object).getId()});
			case USERS:
				return Optional.of(new String[]{type.table + ".code=" + ((Users) object).getCode()});
			case ACCESS:
				return Optional.empty();
			case STOCKS:
				return Optional.of(new String[]{type.table + ".code_goods=" + ((Stocks) object).getCode_goods()});
			case OBJECTS_TARA:
				return Optional.of(new String[]{type.table + ".id=" + ((ObjectsTara) object).getId()});
			case BOTS_TELEGRAM:
				return Optional.of(new String[]{type.table + ".name=" + ((BotsTelegram) object).getName()});
			case USERS_TELEGRAM:
				return Optional.of(new String[]{type.table + ".chat_id=" + ((UsersTelegram) object).getChat_id()});
			default:
				throw wrongTypeTable;
		}
	}

	private enum Command {
		CREATE,
		UPDATE,
		DELETE;
	}

	private enum TypeTable {
		SCALES("scales"),
		SECTIONS("sections"),
		GOODS("goods"),
		TEMPLATES("templates"),
		CODES("barcodes"),
		USERS(""),
		ACCESS(""),
		STOCKS(""),
		OBJECTS_TARA(""),
		BOTS_TELEGRAM(""),
		USERS_TELEGRAM("");

		private final String table;

		TypeTable(String table) {
			this.table = table;
		}
	}

	@Override
	public String toString() {
		return "Distribute{" +
				"id_command=" + id_command +
				", id_type_table=" + id_type_table +
				", unique_item=" + unique_item +
				", id_scales=" + id_scales +
				", id_condition=" + id_condition +
				", id_templates=" + id_templates +
				", id_barcodes=" + id_barcodes +
				", price=" + price +
				", description='" + description + '\'' +
				", batch=" + batch +
				", refreshTimer=" + refreshTimer +
				'}';
	}
}