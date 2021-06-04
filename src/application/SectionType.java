package application;

/**
 * Contains sections at which operations can happen
 */
public enum SectionType {
    PRODUCT("продукт"),
    SECTION("категорія"),
    TEMPLATE("шаблон етикетки"),
    CODE("шаблон коду");

    private final String name;

    SectionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
