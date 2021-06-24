package application.models.net.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

public class SqlQueryBuilder {
	private final MySQL db;
	private final StringBuilder builder;
	private final Logger logger;
	private String table;

	public SqlQueryBuilder(MySQL db, String table) {
		this.db = db;
		this.builder = new StringBuilder();
		this.table = table;
		logger = LogManager.getLogger(SqlQueryBuilder.class);
	}

	public SqlQueryBuilder select(String... columns) {
		builder.append("select ");

		if (columns.length > 1) {
			builder.append("(");

			for (int i = 0; i < columns.length; i++) {
				builder.append(columns[i]);
				if (i != columns.length - 1) {
					builder.append(", ");
				} else {
					builder.append(")");
				}
			}
		} else if (columns.length == 1 && "".equals(columns[0])) {
			return this;
		} else {
			builder.append(columns[0]);
		}

		builder.append(" ");

		return this;
	}

	public SqlQueryBuilder where(Map<String, String> map) {
		builder.append("where").append(" ");
		Set<Map.Entry<String, String>> entries = map.entrySet();
		int counter = 0;

		for (Map.Entry<String, String> entry : entries) {
			builder.append(table)
					.append(".")
					.append(entry.getKey())
					.append("=")
					.append(entry.getValue());
			if (counter != entries.size() - 1) {
				builder.append(" ").append("and").append(" ");
			}
			counter++;
		}

		builder.append(" ");
		return this;
	}

	public SqlQueryBuilder count(String... columns) {
		builder.append("count(");
		if (columns.length == 1 && "*".equals(columns[0])) {
			builder.append(columns[0]).append(")");
		} else {
			for (int i = 0; i < columns.length; i++) {
				builder.append(columns[i]);

				if (i != columns.length - 1) {
					builder.append(", ");
				} else {
					builder.append(")");
				}
			}
		}

		builder.append(" ");

		return this;
	}

	public SqlQueryBuilder from(String tableName) {
		builder.append("from").append(" ").append(tableName).append(" ");
		return this;
	}

	public SqlQueryBuilder limit(int number) {
		builder.append("limit").append(" ").append(number).append(" ");
		return this;
	}

	public SqlQueryBuilder offset(int number) {
		builder.append("offset").append(" ").append(number).append(" ");
		return this;
	}

	public SqlQueryBuilder orderBy(String tableName, String columnName) {
		builder.append("order by").append(" ").append(tableName).append(".").append(columnName).append(" ");
		return this;
	}

	public ResultSet execute() {
		builder.append(";");
		logger.info("Sql query: {}", builder);

		return db.getSelect(builder.toString());
	}

}
