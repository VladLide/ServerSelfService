package application.models.net.database.mysql;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.models.TextBox;
import application.models.net.ConfigNet;
import application.models.net.PackingDBValue;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

public class MySQL {
	public Connection dbConnection = null;
	private ConfigNet config = null;
	private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String SQL_UPDATE = "UPDATE ${table} SET ${values}";
	private static final String SQL_SELECT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String SQL_DELETE = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String TABLE_REGEX = "\\$\\{table\\}";
	private static final String KEYS_REGEX = "\\$\\{keys\\}";
	private static final String VALUES_REGEX = "\\$\\{values\\}";
	private final Logger logger = LogManager.getLogger(MySQL.class);

	public MySQL(Integer id) {
		try {
			config = new ConfigNet(id);
		} catch (Exception e) {
			logger.error(String.format("Error while creating ConfigNet with id %d", id), e);
		}
	}

	public Connection getDBConnection() {
		if (dbConnection == null) {
			try {
				String connectionString = "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/"
						+ config.getName() + "?useUnicode=yes&characterEncoding=UTF-8&serverTimezone="
						+ TimeZone.getDefault().getID();
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.out.println(connectionString);
				dbConnection = DriverManager.getConnection(connectionString, config.getLogin(), config.getPass());
			} catch (SQLException e) {
				logger.error("Error obtaining database", e);
				if(config.isServer())
				Platform.runLater(()->{
					TextBox.alertOpenDialogException(AlertType.INFORMATION, "connectNoBD", e.getMessage());
				});
			} catch (ClassNotFoundException e) {
				logger.error("Error obtaining database driver", e);
				if(config.isServer())
				Platform.runLater(()->{
					TextBox.alertOpenDialogException(AlertType.INFORMATION, "connectNoDriverBD", e.getMessage());
				});
			}
		}
		return dbConnection;
	}

	public void setConfig(Integer id) {
		try {
			config = new ConfigNet(id);
			Connection connection = getDBConnection();
			if (connection != null)
				connection.close();
			this.dbConnection = null;
			getDBConnection();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public ConfigNet getConfig() {
		return config;
	}

	public Statement getStatement() throws SQLException {
		return getDBConnection().createStatement();
	}

	public static String Query(String tableName, String[] columns, String type) {
		String query = "";
		switch (type) {
		case "insert": {
			String questionmarks = StringUtils.repeat("?,", columns.length);
			questionmarks = (String) questionmarks.subSequence(0, questionmarks.length() - 1);
			query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
			query = query.replaceFirst(KEYS_REGEX, StringUtils.join(columns, ","));
			query = query.replaceFirst(VALUES_REGEX, questionmarks);
			query = query + " ON DUPLICATE KEY UPDATE " + StringUtils.join(columns, " = ? , ") + " = ?";
			break;
		}
		case "update": {
			String questionmarks = StringUtils.join(columns, " = ?, ") + " = ? ";
			questionmarks = (String) questionmarks.subSequence(0, questionmarks.length());
			query = SQL_UPDATE.replaceFirst(TABLE_REGEX, tableName);
			query = query.replaceFirst(VALUES_REGEX, questionmarks);
			break;
		}
		case "select": {
			break;
		}
		case "delete": {
			break;
		}
		default: {
			System.out.println("Query: type was not found: " + type);
		}
		}
		// System.out.println("Query: " + query);
		return query;
	}

	public int execute(String sql, PackingDBValue[] value) {
		int result = 0;
		PreparedStatement pstm;
		try {
			if (dbConnection != null) {
				pstm = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				int index = 1;
				int count = (sql.matches("^INSERT.*")) ? value.length * 2 : value.length;
				do {
					index = fillPstm(value, pstm, index);
				} while (index < count);
				result = pstm.executeUpdate();
			}
			/*
			 * int key = 0; try (ResultSet generatedKeys = pstm.getGeneratedKeys()) { if
			 * (generatedKeys.next()) { result = generatedKeys.getInt(1); } else {
			 * System.out.println("Creating user failed, no ID obtained."); } }finally {
			 * result = (key==0)?result:key; }
			 */
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*
			 * finally { try { if (this.dbConnection != null) { this.dbConnection.close(); }
			 * } catch (Exception e) { e.printStackTrace(); } }
			 */
		return result;
	}

	private int fillPstm(PackingDBValue[] value, PreparedStatement pstm, int index) throws SQLException {
		for (PackingDBValue string : value) {
			switch (string.type) {
			case "I": {
				if (string.data == null)
					pstm.setInt(index++, 0);
				else
					pstm.setInt(index++, (int) string.data);
				break;
			}
			case "S": {
				if (string.data == null)
					pstm.setString(index++, "NULL");
				else
					pstm.setString(index++, (String) string.data);
				break;
			}
			case "F": {
				if (string.data == null)
					pstm.setFloat(index++, 0);
				else
					pstm.setFloat(index++, (Float) string.data);
				break;
			}
			case "D": {
				if (string.data == null)
					pstm.setDouble(index++, 0);
				else
					pstm.setDouble(index++, (Double) string.data);
				break;
			}
			case "B": {
				if (string.data == null) {
					pstm.setBlob(index++, (Blob) null);
				} else
					pstm.setBlob(index++, (Blob) string.data);
				break;
			}
			case "BL": {
				if (string.data == null) {
					pstm.setBoolean(index++, false);
				} else
					pstm.setBoolean(index++, (Boolean) string.data);
				break;
			}
			case "DT": {
				if (string.data == null) {
					pstm.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.parse("2000-01-01T00:00:00")));
				} else
					pstm.setTimestamp(index++, Timestamp.valueOf((LocalDateTime) string.data));
				break;
			}
			}
		}
		return index;
	}

	public int[] execute(String sql, List<PackingDBValue[]> list) {
		int[] result = {};
		PreparedStatement pstm;
		try {
			if (dbConnection != null) {
				Connection con = dbConnection;
				con.setAutoCommit(false);
				pstm = con.prepareStatement(sql);
				final int batchSize = 1000;
				int count = 0;
				for (PackingDBValue[] value : list) {
					int index = 1;
					int count1 = (sql.matches("^INSERT.*")) ? value.length * 2 : value.length;
					do {
						index = fillPstm(value, pstm, index);
					} while (index < count1);
					pstm.addBatch();
					if (++count % batchSize == 0) {
						try {
							result = pstm.executeBatch();
						} catch (Exception e) {
							System.out.println("DBHandler-All: error sql " + e.getMessage());
						}
					}
				}
				result = ArrayUtils.addAll(result, pstm.executeBatch());
				con.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} /*
			 * finally { try { if (this.dbConnection != null) { this.dbConnection.close(); }
			 * } catch (Exception e) {
			 * System.out.println("DBHandler-All: error close "+e.getMessage()); } }
			 */
		return result;
	}

	public int insert(String table, String[] columns, PackingDBValue[] value) {
		int result = 0;
		if (columns.length != value.length) {
			return result;
		}
		String sql = MySQL.Query(table, columns, "insert");
		this.execute(sql, value);
		// System.out.println(result);
		return result;
	}

	public int[] insertAll(String table, String[] columns, List<PackingDBValue[]> value) {
		int[] result = {};
		// todo fix if comparison
//		if (columns.length != value.get(0).length) {
//			return result;
//		}
		String sql = MySQL.Query(table, columns, "insert");
		result = this.execute(sql, value);
		// System.out.println(result);
		return result;
	}

	public int update(String table, String[] columns, PackingDBValue[] value, String[] where) {
		int result = 0;
		if (columns.length != value.length) {
			return result;
		}
		String sql = MySQL.Query(table, columns, "update");
		if (where.length != 0) {
			sql += "WHERE " + StringUtils.join(where, ",");
			// sql=(String) sql.subSequence(0, sql.length()-1);
		}
		result = this.execute(sql, value);
		// System.out.println(result);
		return result;
	}

	public int[] updateAll(String table, String[] columns, List<PackingDBValue[]> values, String[] where) {
		String sql = MySQL.Query(table, columns, "update");
		if (where.length != 0)
			sql += "WHERE " + StringUtils.join(where, ",");

		return execute(sql, values);
	}

	public int delete(String table, String id) {
		int result = 0;
		String sql = "DELETE FROM " + table + " WHERE id = " + id;
		try {
			if (dbConnection != null)
				result = getStatement().executeUpdate(sql);
		} catch (SQLException e) {
			result = 0;
			System.out.println(sql + System.getProperty("line.separator") + e.getMessage());
		}
		return result;
	}

	public int delete(String table, int id) {

		return delete(table, id + "");
	}

	public int deleteAll(String table) {
		int result = 0;
		String sql = "DELETE FROM " + table;
		try {
			if (dbConnection != null)
				result = getStatement().executeUpdate(sql);
		} catch (SQLException e) {
			result = 0;
			System.out.println(sql + System.getProperty("line.separator") + e.getMessage());
		}
		return result;
	}

	public int delete(String table, String[] where) {
		int result = 0;
		String sql = "DELETE FROM " + table + " WHERE ";
		if (where.length != 0) {
			sql += StringUtils.join(where, ",");
		} else {
			return result;
		}
		try {
			if (dbConnection != null)
				result = getStatement().executeUpdate(sql);
		} catch (SQLException e) {
			result = 0;
			System.out.println(sql + System.getProperty("line.separator") + e.getMessage());
		}
		return result;
	}

	public List<List<String>> getSelect(List<String> select, String table, List<String> where) {
		List<List<String>> result = new ArrayList<List<String>>();
		String sql = "SELECT " + StringUtils.join(select, ",");
		if (select.size() == 0)
			sql += "* ";
		else {
			int len = sql.length() - 2;
			sql.subSequence(0, len);
		}
		sql += " FROM " + table;
		if (where.size() != 0) {
			sql += "Where " + StringUtils.join(where, ",");
		}
		try {
			if (dbConnection != null) {
				ResultSet answer = getStatement().executeQuery(sql);
				while (answer.next()) {
					List<String> row = new ArrayList<String>();
					for (int i = 1; i < select.size() + 1; i++)
						row.add(answer.getString(i));
					result.add(row);
				}
			}
		} catch (SQLException e) {
			System.out.println(sql + System.getProperty("line.separator") + e.getMessage());
		}
		return result;
	}

	public ResultSet getSelect(String sql) {
		ResultSet result = null;
		try {
			// System.out.println(sql);
			if (dbConnection != null)
				result = getStatement().executeQuery(sql);
		} catch (SQLException e) {
			System.out.println(sql + System.getProperty("line.separator") + e.getMessage());
		}
		return result;
	}

	public boolean isId(String table, int id) {
		String sql = "SELECT " + table + ".id FROM " + table + " WHERE " + table + ".id=" + id;
		try {
			ResultSet result = getStatement().executeQuery(sql);
			return (result.next()) ? true : false;
		} catch (SQLException e) {
			System.out.println(sql + System.getProperty("line.separator") + e.getMessage());
			return false;
		}
	}

	public int getSection() {
		String sql = "SELECT COUNT(DISTINCT " + "id_scale" + ") FROM" + " sections";
		int count = 0;
		try {
			if (dbConnection != null) {
				ResultSet result = getStatement().executeQuery(sql);
				count = (result.next()) ? result.getInt(1) : 0;
			}
		} catch (SQLException e) {
			System.out.println(sql + System.getProperty("line.separator") + e.getMessage());
		}

		return count;
	}

	public static void delete(String tableName, MySQL db) {
		try {
			db.dbConnection.createStatement().execute("DELETE FROM " + tableName);
		} catch (SQLException e) {
			System.out.println("DELETE: " + e.getMessage());
		}
	}

	public boolean isDBConnection() {
		boolean result = false;
		try {
			Statement statement = dbConnection.createStatement();
			statement.executeQuery("select 1");

			return true;
		} catch (SQLException e) {
			System.out.println("isDBConnection: " + e.getMessage());

			TextBox.alertOpenDialog(AlertType.WARNING, "wait");
			try {
				if (!getDBConnection().isValid(5)) {
					dbConnection.close();
					dbConnection = null;
				}
				result = getDBConnection().isValid(5);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
				return false;
			}
		}
		return result;
	}
}
