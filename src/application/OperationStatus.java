package application;

/**
 * Contains statuses of operations
 */
public enum OperationStatus {
    SUCCESS("успішно"),
    FAILURE("негативно");

	private final String name;

    OperationStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
