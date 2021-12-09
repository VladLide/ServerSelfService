package application.enums;

import java.util.HashMap;
import java.util.Map;

public enum TableType {
	SCALES,
	SECTIONS,
	GOODS,
	TEMPLATES,
	CODES,
	USERS,
	ACCESS,
	STOCKS,
	OBJECT_TARA,
	BOTS_TELEGRAM,
	USERS_TELEGRAM,
	DISTRIBUTE;

	private static final Map<String, TableType> map;

	static {
		map = new HashMap<>();
		map.put("scales", SCALES);
		map.put("sections", SECTIONS);
		map.put("goods", GOODS);
		map.put("templates", TEMPLATES);
		map.put("codes", CODES);
		map.put("users", USERS);
		map.put("access", ACCESS);
		map.put("stocks", STOCKS);
		map.put("object_tara", OBJECT_TARA);
		map.put("bots_telegram", BOTS_TELEGRAM);
		map.put("users_telegram", USERS_TELEGRAM);
		map.put("distribute", DISTRIBUTE);
	}

	public static TableType get(String name) {
		return map.get(name);
	}
}
