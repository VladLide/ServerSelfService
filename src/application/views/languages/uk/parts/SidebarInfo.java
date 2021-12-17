package application.views.languages.uk.parts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SidebarInfo {
	public static String root = "Головна";

	public static ObservableList<String[]> menu = FXCollections.observableArrayList(
			new String[] { "scales", "Ваги" },
			new String[] { "editors", "Редактори даних" }
	);

	public static ObservableList<String[]> menuScale = FXCollections.observableArrayList(
			new String[] { "products", "Товари" },
			new String[] { "stocks", "Знижки" },
			new String[] { "sections", "Категорії" },
			new String[] { "templates", "Шаблони етикетки" },
			new String[] { "templateCodes", "Шаблони коду" },
			new String[] { "telegrams", "Повідомлення" },
			new String[] { "users", "Користувачі" },
			new String[] { "settings", "Налаштування" }
	);

	public static ObservableList<String[]> menuEditors = FXCollections.observableArrayList(
			new String[] { "products", "Товари" },
			new String[] { "stocks", "Знижки" },
			new String[] { "sections", "Категорії" },
			new String[] { "templates", "Шаблони етикетки" },
			new String[] { "templateCodes", "Шаблони кодів" },
			new String[] { "settings", "Налаштування" }
	);

	public static ObservableList<String[]> filter = FXCollections.observableArrayList(
			new String[] { "String", "Пошук", "search" },
			new String[] { "", "", "" }
	);

	public SidebarInfo() {
		// TODO Auto-generated constructor stub
	}

}
