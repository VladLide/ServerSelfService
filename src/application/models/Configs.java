package application.models;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Configs {
	private String file = "";
	private Map<String, String> config = new HashMap<String, String>();
	private static Configs instance = null;

	public Configs() {
		super();
	}

	public static Configs getInstance() {
		if (instance == null) {
			instance = new Configs();
			instance.readConfigFile("config");
		}
		return instance;
	}

	public void readConfigFile(String name) {
		Configs me = getInstance();
		me.config.clear();
		try {
			me.file = new String(Files.readAllBytes(Paths.get(Utils.getPath("sys", name))));
			String[] nextLine = {};
			if (me.file != null) {
				if (me.file.length() > 0)
					nextLine = me.file.split(System.getProperty("line.separator"));
			}
			for (String v : nextLine) {
				String[] key = v.split("=");
				if (key.length == 2)
					me.config.put(key[0], key[1]);
				else
					me.config.put(key[0], "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeConfigFile(String name) {
		Configs me = getInstance();
		me.file = "";
		me.config.forEach((key, value) -> {
			me.file += key + "=" + value + System.getProperty("line.separator");
		});
		try {
			FileWriter myWriter = new FileWriter(Utils.getPath("sys", name));
			myWriter.write(me.file);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void setItem(String key, String value) {
		getInstance().config.put(key, value);
	}

	public static void setItem(String key, Integer value) {
		getInstance().config.put(key, value + "");
	}

	public static void setItem(String key, Double value, Integer singShare) {
		singShare = (singShare == null) ? 2 : singShare;
		int dil = (10 * singShare);
		getInstance().config.put(key,
				String.format("%." + singShare + "f", (double) Math.round(value * dil) / dil).replace(",", "."));
	}

	public static void setItem(String key, Float value, Integer singShare) {
		singShare = (singShare == null) ? 2 : singShare;
		int dil = (10 * singShare);
		getInstance().config.put(key,
				String.format("%." + singShare + "f", (double) Math.round(value * dil) / dil).replace(",", "."));
	}

	public static void setItem(String key, Boolean value) {
		getInstance().config.put(key, value.toString());
	}

	public static String getItemStr(String key) {
		String result = getInstance().config.get(key);
		return (result != null) ? result : "";
	}

	public static Boolean getItemBoolean(String key) {
		Boolean result = null;
		try {
			result = Integer.parseInt(getInstance().config.get(key)) > 0;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public static Object getItemObj(String key) {
		Object result = null;
		try {
			result = getInstance().config.get(key);
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}

	public static Integer getItemInt(String key) {
		Integer result = null;
		try {
			result = Integer.parseInt(getInstance().config.get(key));
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}

	public static Double getItemDoub(String key) {
		Double result = null;
		try {
			result = Double.parseDouble(getInstance().config.get(key));
		} catch (Exception e) {
			result = 0.0;
		}
		return result;
	}

	public static Float getItemFloat(String key) {
		Float result = null;
		try {
			result = Float.parseFloat(getInstance().config.get(key));
		} catch (Exception e) {
			result = 0.0F;
		}
		return result;
	}
}
