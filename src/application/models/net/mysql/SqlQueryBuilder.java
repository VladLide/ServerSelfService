package application.models.net.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

public class SqlQueryBuilder {
	private MySQL db;
	private StringBuilder builder;
	private Logger logger = LogManager.getLogger(SqlQueryBuilder.class);

	public SqlQueryBuilder(MySQL db) {
		this.db = db;
		this.builder = new StringBuilder();
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
		ResultSet resultSet = db.getSelect(builder.toString());

		logger.info("Sql query: {}", builder);

		return resultSet;
	}
}
