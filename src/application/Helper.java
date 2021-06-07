package application;

import java.util.NoSuchElementException;


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
	 * @param operation   enum which contains operations which can be done with objects
	 * @param place       enum which contains places where operation can happen
	 * @param ip          string which contains ip address of place which called operation
	 * @param sectionType enum which contains section at which operations can happen
	 * @param name        string which contains name of object on which operation is produced
	 * @param dateTime    string which contains current date and time
	 * @param operationStatus      enum which contains result of operation
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
}

