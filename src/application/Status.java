package application;

public enum Status {
    SUCCESS("успішно"),
    FAILURE("негативно");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
