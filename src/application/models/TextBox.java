package application.models;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class TextBox {
	public static class MSBOX {
		public static final String INFORMATION = "info";
		public static final String ERROR = "error";
		public static final String CONFIRMATION = "confirmation";
		public static final String WARNING = "warning";
	};

	public static String[] parametersObj = { "Ширина пробілу", "Висота штрих коду", "Розмір шрифту", "Вільна зона",
			"Жирність шрифту", "Текст", "Ширина", "Висота", "Вирівнювання", "З наступного рядку", "Шрифт" };
	public static String[] typeGoods = { "ШТ.", "КГ." };
	/*
	 * public static String[] nameObjTemplate = {"Назва PLU", "Штрихкод",
	 * "Ціна за одиницю", "Вага", "Вартість", "Термін придатності",
	 * "Дата пакування", "Вільний текст", "Код PLU", "Текст заголовку",
	 * "Колонтітул", "Інгредієнти", "Тара", "Вага бутто", "Вага нетто",
	 * "Номер етикетки", "Час друку етикетки" , "Час пакування",
	 * "Термін зберігання (в днях)", "Термін зберігання (в годинах)"}; public static
	 * Map<Integer, String[]> ObjTemplate = new HashMap<Integer, String[]>();
	 */
	public static String[] objBarcode = { "Префікс", "Код товару", "Вага товару" };
	/*
	 * public static String[][] goodsInfo =
	 * {{"Integer","№","num"},{"String","Назва","name"},{"Float","Ціна","price"},{
	 * "String","Тип","type"},
	 * {"Integer","Код","code"},/*{"Integer","Код","pre_code"},/{
	 * "Integer","Терм. придат.","before_validity"},{"String","Інгредієнти",
	 * "ingredients"},
	 * {"String","Назва секції","sectionsName"}/*,{"String","Зображення","imageName"
	 * }/,{"String","Етикетки","templatesName"},{"String","Штрих код","barcodesName"
	 * }};
	 */
	public static String[][] info2col = { { "String", "Параметри", "name" }, { "String", "Значення", "value" } };
	public static String[][] options = { { "Пароль адміна", "pass", "0000" }, { "Пароль фасування", "pass", "0000" },
			{ "Номер етикетки", "id_templates", "1" }, { "Номер штрих коду", "id_barcodes", "1" },
			{ "Сітка секції (3*2 до 3*4)", "section_grid", "3*2" }, { "Сітка plu (3*2 до 4*5)", "plu_grid", "3*2" },
			{ "Папка завантаження", "direct_load", "load/" },
			{ "Текст заголовку етикетки", "top_message", "Раді Вас Вітати!!!" },
			{ "Колонтітул етикетки", "bottom_message", "Дякуємо за покупку!!!" },
			{ "Порт зважування", "name_com_port", "COM2" },
			{ "Сортування секцій (1-по коду або 0-по алфавіту)", "section_sort", "0" },
			{ "Сортування plu (1-по коду або 0-по алфавіту)", "plu_sort", "0" },
			{ "Максимальна кількість PLU в секції", "max_plu", "1000" },
			{ "Друк етикетки не знімаючи товар(0-знімати, 1-не знімати)", "print_not_up", "0" },
			{ "Логотип", "logo", "C:/img.png" } };
	public static String[] DBServer = { "Налаштування", "Категорії", "PLU", "Штрих код", "Етикетки",
			"Сортування PLU по категоріям" };
	public static String[][] Scales = { { "CheckBox", "", "checkBox" }, { "Integer", "№", "id" },
			{ "String", "Назва", "name" }, { "LocalDateTime", "Дата обновлення", "date_update" },
			{ "AnchorPane", "Стан", "status" } };
	// public static String[][] Scales =
	// {{"CheckBox","","checkBox"},{"Integer","№","id"},{"String","Назва","name"},{"LocalDateTime","Дата
	// обновлення","date_update"},{"AnchorPane","Стан","status"}};
	public Map<String, String[]> msg = new HashMap<String, String[]>();
	public Map<String, String[]> settings = new HashMap<String, String[]>();
	public static Map<String, String> word = new HashMap<String, String>();
	public Map<String, String[][]> info = new HashMap<String, String[][]>();
	private static TextBox inst;

	public static TextBox init() {
		if (inst == null)
			inst = new TextBox();
		return inst;
	}

	public TextBox() {
		super();
		String[][] str = {
				{ "Редагування", "Відкрити редагування не вдалося",
						"Відкрити редагування неможливо, через те, що не вибрано елемент!" },
				{ "Видалення", "", "Ви впевнені, що хочете видалити з бази даних товар?" },
				{ "Видалення", "Результат видалення: Успішно", "З бази даних успішно видалено товар!" },
				{ "Видалення", "Результат видалення: Негативно",
						"З бази даних не вдалося видалити товар.\n Можливо в базі його вже немає. Обновіть дані!" },
				{ "Вибір зображення", "Результат вибору: Негативно",
						"Можливо файлу не існує.\n Перевірте введені дані!" },
				{ "Добавлення", "Результат добавлення: Успішно", "До бази даних успішно добавлено товар!" },
				{ "Добавлення", "Результат добавлення: Негативно",
						"До бази даних не вдалося добавити товар.\n Перевірте введені дані!" },
				{ "Редагування", "Результат редагування: Успішно", "В базі даних успішно відредаговано товар!" },
				{ "Редагування", "Результат редагування: Негативно",
						"В базі даних не вдалося відредагувати товар.\n Перевірте введені дані!" },
				{ "Підключення", "Підключитись до бази даних не вдалося",
						"Підключитись до бази даних не вдалося, можливо не встановленна база даних!\n\r" },
				{ "Підключення", "Підключитись до бази даних не вдалося",
						"Підключитись до бази даних не вдалося, можливо не встановленні відповідні драйвера!\n\r" },
				{ "Видалення", "", "Ви впевнені, що хочете видалити шаблон(и) етикетки(ок)?" },
				{ "Видалення", "Результат видалення: Успішно", "Шаблон(и) успішно видалено!" },
				{ "Видалення", "Результат видалення: Негативно",
						"Шаблон(и) не вдалося видалити.\n Можливо в базі його(їх) вже немає. Обновіть дані!" },
				{ "Редагування", "Відкрити редагування не вдалося",
						"Відкрити редагування неможливо, через те, що не вибрано шаблон етикетки!" },
				{ "Зважування", "Помилка зважування", "Вибраний товар, важить менше дозволеного!" },
				{ "Ласкаво просимо!", "Self-service 1.0.1", "Ласкаво просимо до каси самообслуговування!" },
				{ "Вихід", "", "Ви впевнені, що хочете вийти програми?" }, { "Увага!", "", "Покладіть товар на ваги!" },
				{ "Видалення", "", "Ви впевнені, що хочете видалити з бази даних категорію?" },
				{ "Видалення", "Результат видалення: Успішно", "З бази даних успішно видалено категорію!" },
				{ "Видалення", "Результат видалення: Негативно",
						"З бази даних не вдалося видалити категорію.\n Можливо в базі цієї категорії вже немає. Обновіть дані!" },
				{ "Добавлення", "Результат добавлення: Успішно", "До бази даних успішно добавлена категорія!" },
				{ "Добавлення", "Результат добавлення: Негативно",
						"До бази даних не вдалося добавити категорію.\n Перевірте введені дані!" },
				{ "Редагування", "Результат редагування: Успішно", "В базі даних успішно відредаговано категорію!" },
				{ "Редагування", "Результат редагування: Негативно",
						"В базі даних не вдалося відредагувати категорію.\n Перевірте введені дані!" },
				{ "Видалення", "Ви впевнені, що хочете видалити з бази даних шаблон штрих коду?" },
				{ "Видалення", "Результат видалення: Успішно", "З бази даних успішно видалено шаблон штрих коду!" },
				{ "Видалення", "Результат видалення: Негативно",
						"З бази даних не вдалося видалити шаблон штрих коду.\n Можливо в базі цього шаблону штрих коду вже немає. Обновіть дані!" },
				{ "Добавлення", "Результат добавлення: Успішно", "До бази даних успішно добавлено шаблон штрих коду!" },
				{ "Добавлення", "Результат добавлення: Негативно",
						"До бази даних не вдалося добавити шаблон штрих коду.\n Перевірте введені дані!" },
				{ "Редагування", "Результат редагування: Успішно",
						"В базі даних успішно відредаговано шаблон штрих коду!" },
				{ "Редагування", "Результат редагування: Негативно",
						"В базі даних не вдалося відредагувати шаблон штрих коду.\n Перевірте введені дані!" },
				{ "Не вистачає даних", "", "Не всі дані було введено" },
				{ "Помилка імпорту", "", "Файл імпорту не вибрано." },
				{ "Помилка імпорту", "", "Папка з зображеннями не знайдена " },
				{ "Помилка імпорту", "", "Помилка під час добавлення в Базу даних " },
				{ "Помилка імпорту", "", "Зображення не має звязку з товаром " },
				{ "Помилка імпорту", "", "Exception: " },
				{ "Помилка імпорту", "", "Помилка під час добавлення зображення " },
				{ "Очищення", "", "Ви впевнені, що хочете видалити всі товари?" },
				{ "Увага!", "", "Зніміть товар з вагів!" }, { "Увага!", "", "Назва така вже існує! Змініть назву." },
				{ "Увага!", "", "Такого товару не існує." },
				{ "Увага!", "", "Зачекайте будь-ласка, поки відбувається оновлення товару ..." } };
		String[][][] infoTmp = {
				{ { "Integer", "Id", "number" }, { "Integer", "Номер вагів", "id_scales" },
						{ "String", "Назва", "name" } },
				{ { "Integer", "Id", "id" }, { "Integer", "Номер вагів", "id_scales" }, { "String", "Назва",
						"name" }/*
								 * ,{"Integer","Id етикетки","id_templates"},{"Integer","Id штрих коду"
								 * ,"id_barcodes"},
								 * {"String","Макс. сітка секції","section_grid"},{"String","Макс. сітка товару"
								 * ,"goods_grid"},/*{"String","Папка завантаження","direct_load"},
								 * {"String","Повідомлення в шапці етикетки","top_message"},{
								 * "String","Повідомлення в підвалі етикетки","bottom_message"},{
								 * "String","Порт зчитування вагів","name_com_port"}
								 */
				},
				{ { "Integer", "Id", "id" }, { "Integer", "Номер вагів", "id_scales" }, { "String", "Назва", "name" },
						{ "Float", "Ціна", "price" }, { "String", "Тип", "type" }, { "Integer", "Код", "code" },
						{ "Integer", "Id секції", "id_sections" }, { "Integer", "Id етикетки", "id_templates" },
						{ "Integer", "Id штрих коду", "id_barcodes" },
						{ "Integer", "Терм. придат.", "before_validity" }, { "String", "Інгредієнти", "ingredients" } },
				{ { "Integer", "Id", "number" }, { "Integer", "Номер вагів", "id_scales" },
						{ "String", "Назва", "name" }, { "String", "Розширення", "extension" } },
				{ { "Integer", "Номер вагів", "id" }, { "String", "Назва", "name" },
						{ "String", "ip адрес", "ip_address" }, { "String", "Ip адрес серверу", "Ip_address_server" },
						{ "String", "Налаштування", "config" } },
				{ { "Integer", "Id", "number" }, { "Integer", "Номер вагів", "id_scales" },
						{ "String", "Назва", "name" }, { "String", "Опис", "description" } } };
		this.msg.put("editNotGoods", str[0]);
		this.msg.put("deleteGoods?", str[1]);
		this.msg.put("deleteGoodsYes", str[2]);
		this.msg.put("deleteGoodsNo", str[3]);
		this.msg.put("chooseImageNo", str[4]);
		this.msg.put("addGoodsYes", str[5]);
		this.msg.put("addGoodsNot", str[6]);
		this.msg.put("editGoodsYes", str[7]);
		this.msg.put("editGoodsNo", str[8]);
		this.msg.put("connectNoBD", str[9]);
		this.msg.put("connectNoDriverBD", str[10]);
		this.msg.put("deleteTemplate?", str[11]);
		this.msg.put("deleteTemplateYes", str[12]);
		this.msg.put("deleteTemplateNo", str[13]);
		this.msg.put("editNotTemplate", str[14]);
		this.msg.put("chooseMinNo", str[15]);
		this.msg.put("about", str[16]);
		this.msg.put("Exit?", str[17]);
		this.msg.put("warningDownPLU", str[18]);
		this.msg.put("deleteSection?", str[19]);
		this.msg.put("deleteSectionYes", str[20]);
		this.msg.put("deleteSectionNo", str[21]);
		this.msg.put("addSectionYes", str[22]);
		this.msg.put("addSectionNot", str[23]);
		this.msg.put("editSectionYes", str[24]);
		this.msg.put("editSectionNo", str[25]);
		this.msg.put("deleteBarcode?", str[26]);
		this.msg.put("deleteBarcodeYes", str[27]);
		this.msg.put("deleteBarcodeNo", str[28]);
		this.msg.put("addBarcodeYes", str[29]);
		this.msg.put("addBarcodeNot", str[30]);
		this.msg.put("editBarcodeYes", str[31]);
		this.msg.put("editBarcodeNo", str[32]);
		this.msg.put("saveGoodsNo", str[33]);
		this.msg.put("chooseCSVNo", str[34]);
		this.msg.put("imgDirNo", str[35]);
		this.msg.put("addDBError", str[36]);
		this.msg.put("connectImgGoodsNo", str[37]);
		this.msg.put("importError", str[38]);
		this.msg.put("addImgNo", str[39]);
		this.msg.put("clearGoods?", str[40]);
		this.msg.put("warningUpPLU", str[41]);
		this.msg.put("warningName", str[42]);
		this.msg.put("warningNotPLU", str[43]);
		this.msg.put("warningUpdatePLU", str[44]);

		/*
		 * this.ObjTemplate.put(0, new String[]{"namePLU","Name PLU"});
		 * this.ObjTemplate.put(1, new String[]{"barcode",""}); this.ObjTemplate.put(2,
		 * new String[]{"price","000.00"}); this.ObjTemplate.put(3, new
		 * String[]{"weight","00.000"}); this.ObjTemplate.put(4, new
		 * String[]{"cost","0000.00"}); this.ObjTemplate.put(5, new
		 * String[]{"endDate","00.00.0000"}); this.ObjTemplate.put(6, new
		 * String[]{"date","00.00.0000"}); this.ObjTemplate.put(7, new
		 * String[]{"freeText","free Text"}); this.ObjTemplate.put(8, new
		 * String[]{"codePLU","1350"}); this.ObjTemplate.put(9, new
		 * String[]{"top","top"}); this.ObjTemplate.put(10, new
		 * String[]{"bottom","bottom"}); this.ObjTemplate.put(11, new
		 * String[]{"ingredients","ingredients"}); this.ObjTemplate.put(12, new
		 * String[]{"tara","0.000"}); this.ObjTemplate.put(13, new
		 * String[]{"weightUp","00.000"}); this.ObjTemplate.put(14, new
		 * String[]{"weightDown","00.000"}); this.ObjTemplate.put(15, new
		 * String[]{"numberTemplate","15"}); this.ObjTemplate.put(16, new
		 * String[]{"timePrint","00:00:00"}); this.ObjTemplate.put(17, new
		 * String[]{"timePacking","00:00:00"}); this.ObjTemplate.put(18, new
		 * String[]{"daySave","000"}); this.ObjTemplate.put(19, new
		 * String[]{"timeSave","00000"});
		 */
		this.word.put("name", "Назва");

		this.settings.put("admin", options[0]);
		this.settings.put("packing", options[1]);
		this.settings.put("template", options[2]);
		this.settings.put("barcode", options[3]);
		this.settings.put("section_grid", options[4]);
		this.settings.put("plu_grid", options[5]);
		this.settings.put("load", options[6]);
		this.settings.put("top_temp", options[7]);
		this.settings.put("bottom_temp", options[8]);
		this.settings.put("com_port", options[9]);
		this.settings.put("sort_section", options[10]);
		this.settings.put("sort_plu", options[11]);
		this.settings.put("max_plu", options[12]);
		this.settings.put("print_not_up", options[13]);

		this.info.put("barcode", infoTmp[0]);
		this.info.put("settings", infoTmp[4]);
		this.info.put("plu", infoTmp[2]);
		this.info.put("image", infoTmp[3]);
		this.info.put("section", infoTmp[5]);
		this.info.put("template", infoTmp[5]);
	}

	public static String getUkrDayOfWeek(DayOfWeek dayOfWeek) {
		switch (dayOfWeek.getValue()) {
		case 1:
			return "Понеділок";
		case 2:
			return "Вівторок";
		case 3:
			return "Середа";
		case 4:
			return "Четверг";
		case 5:
			return "П'ятниця";
		case 6:
			return "Субота";
		case 7:
			return "Неділя";
		}
		return "";
	}

	public static String getStatus(int i) {
		switch (i) {
		case -2:
			return "Не вірно введений адрес вагів";
		case -1:
			return "Не має зв'язку. Перевірте: адрес вагів, лінію зв'язку або доступ до даних!";
		case 0:
			return "Дані вагів актуальні";
		case 1:
			return "Ваги були оновлені сторонньою програмою, оновіть дані на сервері";
		case 2:
			return "PLU вагів порожнє";
		}
		return "";
	}

	private static Boolean[] visibleButton(String type) {
		Boolean[] visible = new Boolean[3];
		switch (type) {
		case "info": {
			visible = null;
		}
			break;
		case "error": {
			visible[0] = true;
			visible[1] = false;
			visible[2] = false;
		}
			break;
		case "warning": {
			visible[0] = true;
			visible[1] = false;
			visible[2] = false;
		}
			break;
		case "choice": {
			visible[0] = true;
			visible[1] = true;
			visible[2] = false;
		}
			break;
		case "confirmation": {
			visible[0] = true;
			visible[1] = true;
			visible[2] = true;
		}
			break;
		}
		return visible;
	}

	public static void alertOpenDialogException(AlertType type, String key, String exc) {
		TextBox box = new TextBox();
		Alert alert = new Alert(type);
		alert.setTitle(box.msg.get(key)[0]);
		alert.setHeaderText(box.msg.get(key)[1]);
		alert.setContentText(box.msg.get(key)[2] + exc);
		alert.showAndWait();
	}

	public static void alertOpenDialog(AlertType type, String key) {
		TextBox box = new TextBox();
		Alert alert = new Alert(type);
		alert.setTitle(box.msg.get(key)[0]);
		alert.setHeaderText(box.msg.get(key)[1]);
		alert.setContentText(box.msg.get(key)[2]);
		alert.showAndWait();
	}

	public static Optional<ButtonType> alertOpenDialog(AlertType type, String key, String str) {
		TextBox box = new TextBox();
		Alert alert = new Alert(type);
		alert.setTitle(box.msg.get(key)[0]);
		alert.setHeaderText(box.msg.get(key)[1]);
		alert.setContentText(str);
		return alert.showAndWait();
	}

	public static String select(String str) {
		init();
		final String regex = "([\\w_]+)";
		String res = str.toLowerCase();

		final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex,
				java.util.regex.Pattern.MULTILINE);
		final java.util.regex.Matcher matcher = pattern.matcher(res);
		System.out.println(" match : " + matcher.groupCount());
		while (matcher.find()) {
			System.out.println("Full match 0: " + matcher.group(0));
			System.out.println("Full match 1: " + matcher.group(1));
			res = res.replaceAll(matcher.group(1), word.get(matcher.group(1)));
		}
		System.out.println("Full text: " + res);
		return res;
	}
}