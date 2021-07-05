package application;

import application.enums.*;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.SqlQueryBuilder;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Templates;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//todo add javadoc comments to methods

/**
 * Class contains static helper methods
 */
public final class Helper {
	//this variables needed to check if operation or place or ip changed from last call of
	// formatOutput method
	private static Operation lastExecutedOperation;
	private static PlaceType lastPlaceType;
	private static String lastIp;
	private static Logger logger = LogManager.getLogger(Helper.class);

	/**
	 * Helper class contains only static methods, so there is no need to create an
	 * instance of this class
	 *
	 * @throws IllegalAccessException will be thrown if instance of class will be created
	 */
	private Helper() throws IllegalAccessException {
		throw new IllegalAccessException("Private method is called");
	}

	/**
	 * Method will create formatted string for outputting to "Журнал подій",
	 * there are two types of string can be created:
	 * 1. If operation or place or ip is null or changed string will look like
	 * "Operation на (вагах/сервері): ip \n\tSectionType: name [date] status"
	 * 2. If operation and place and ip are the same as the last time string will look like
	 * "   SectionType: name [date] status"
	 *
	 * @param operation       enum which contains operations which can be done with objects
	 * @param place           enum which contains places where operation can happen
	 * @param ip              string which contains ip address of place which called operation
	 * @param sectionType     enum which contains section at which operations can happen
	 * @param name            string which contains name of object on which operation is produced
	 * @param dateTime        string which contains current date and time
	 * @param operationStatus enum which contains result of operation
	 * @return string with formatted ouptut
	 * @throws NoSuchElementException will be thrown if place is null or has value that is
	 *                                not in switch
	 * @throws NullPointerException   will be thrown if operation is null
	 */
	public static String formatOutput(Operation operation,
	                                  PlaceType place,
	                                  String ip,
	                                  SectionType sectionType,
	                                  String name,
	                                  String dateTime,
	                                  OperationStatus operationStatus)
			throws NoSuchElementException, NullPointerException {
		if (operation == null)
			throw new NullPointerException("Operation can not be null");

		boolean operationOrPlaceChanged =
				lastExecutedOperation == null
						|| !lastExecutedOperation.equals(operation)
						|| lastPlaceType == null
						|| !lastPlaceType.equals(place)
						|| lastIp == null
						|| !lastIp.equals(ip);

		lastExecutedOperation = operation;
		lastPlaceType = place;
		lastIp = ip;

		String baseString = String.format(
				"\t%s: %s [%s]-%s",
				capitalizeFirstLetter(sectionType.getName()),
				name,
				dateTime,
				operationStatus.getName()
		);

		if (operationOrPlaceChanged) {
			return String.format(
					"%s на %s:%s" + System.lineSeparator(),
					capitalizeFirstLetter(operation.getName()), //with first letter capitalized
					PlaceType.SCALE.equals(place) ? "вагах" : "сервері",
					ip) + baseString;
		} else {
			return baseString;
		}
	}

	private static String capitalizeFirstLetter(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static Optional<List<Object>> getData(MySQL db,
	                                             int limit,
	                                             int offset,
	                                             ObjectType type) {
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder(db, type.getTableName());
		List<Object> list = null;
		try {
			ResultSet resultSet = queryBuilder.select("*")
					.from(type.getTableName())
					.orderBy(type.getTableName(), type.getOrderByColumn())
					.limit(limit)
					.offset(offset)
					.execute();

			switch (type) {
				case PRODUCTS:
					List<Goods> goods = Converter.fromResultSetToGoodsList(resultSet);
					list = new ArrayList<>(goods);
					break;
				case SECTIONS:
					List<Sections> sections = Converter.fromResultSetToSectionsList(resultSet);
					list = new ArrayList<>(sections);
					break;
				case TEMPLATES:
					List<Templates> templates = Converter.fromResultSetToTemplatesList(resultSet);
					list = new ArrayList<>(templates);
					break;
				case TEMPLATES_CODES:
					List<Codes> codes = Converter.fromResultSetToCodesList(resultSet);
					list = new ArrayList<>(codes);
					break;
				default:
					throw new IllegalArgumentException("No such type");
			}

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

		return Optional.ofNullable(list);
	}

	public static Optional<List<Goods>> getGoods(MySQL db,
	                                   int limit,
	                                   int offset,
	                                   ObjectType type) {
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder(db, type.getTableName());
		ResultSet resultSet = queryBuilder.select("*")
				.from(type.getTableName())
				.orderBy(type.getTableName(), type.getOrderByColumn())
				.limit(limit)
				.offset(offset)
				.execute();
		try {
			return Optional.of(Converter.fromResultSetToGoodsList(resultSet));
		} catch (SQLException e) {
			return Optional.empty();
		}
	}

	public static Optional<ScrollBar> getDataTableScrollBar(TableView<?> dataTable) {
		ScrollBar scrollbar = null;
		for (Node n : dataTable.lookupAll(".scroll-bar")) {
			if (n instanceof ScrollBar) {
				ScrollBar bar = (ScrollBar) n;
				if (bar.getOrientation().equals(Orientation.VERTICAL)) {
					scrollbar = bar;
				}
			}
		}

		return Optional.ofNullable(scrollbar);
	}

	public static Optional<Class<?>> getClassByFullName(String name) throws ClassNotFoundException {
		return Optional.of(Class.forName(name));
	}

	public static boolean isNumeric(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static Blob fileToBlob(File file) throws IOException, SQLException {
		byte[] bytes = Files.readAllBytes(file.toPath());
		return new SerialBlob(bytes);
	}
}

