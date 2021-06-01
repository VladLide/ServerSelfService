package application;

import java.util.NoSuchElementException;


//todo add javadoc comments to methods

/**
 * Class contains static helper methods
 */
public final class Helper {
    private static Operation lastExecutedOperation;
    private static PlaceType lastPlaceType;
    private static String lastIp;

    private Helper() throws IllegalAccessException {
        throw new IllegalAccessException("Private method is called");
    }

    public static String formatOutput(Operation operation,
                                      PlaceType place,
                                      String ip,
                                      SectionType sectionType,
                                      String name,
                                      String date,
                                      Status status)
            throws NoSuchElementException, NullPointerException {
        if (operation == null)
            throw new NullPointerException("Operation can not be null");

        String baseString;
        boolean operationOrPlaceChanged = lastExecutedOperation == null
                || !lastExecutedOperation.equals(operation)
                || lastPlaceType == null
                || !lastPlaceType.equals(place)
                || lastIp == null
                || !lastIp.equals(ip);

        lastExecutedOperation = operation;
        lastPlaceType = place;
        lastIp = ip;

        switch (place) {
            case SCALE:
                baseString = String.format(
                        "\t%s: %s [%s]-%s",
                        capitalizeFirstLetter(sectionType.getName()),
                        name,
                        date,
                        status.getName()
                );
                return operationOrPlaceChanged ?
                        String.format(
                                "%s на %s:%s" + System.lineSeparator(),
                                capitalizeFirstLetter(operation.getName()), //with first letter capitalized
                                "вагах",
                                ip) + baseString
                        :
                        baseString;
            case SERVER:
                baseString = String.format(
                        "\t%s: %s [%s]-%s",
                        capitalizeFirstLetter(sectionType.getName()),
                        name,
                        date,
                        status.getName()
                );
                return operationOrPlaceChanged ?
                        String.format(
                                "%s на %s:%s" + System.lineSeparator(),
                                capitalizeFirstLetter(operation.getName()), //with first letter capitalized
                                "сервері",
                                ip) + baseString
                        :
                        baseString;
            default:
                throw new NoSuchElementException("No place type found");
        }
    }

    private static String capitalizeFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}

