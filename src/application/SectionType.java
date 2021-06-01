package application;

public enum SectionType {
    PRODUCT("продукт"),
    SECTION("категорія"),
    TEMPLATE("шаблон етикетки"),
    CODE("шаблон коду");

    private String name;

    SectionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
