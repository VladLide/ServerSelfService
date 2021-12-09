package application;


import application.enums.TableType;

import java.util.Map;

public class LoadConfigFile {
	private TableType type;
	private Map<String, String> map;

	public LoadConfigFile(TableType type, Map<String, String> map) {
		this.type = type;
		this.map = map;
	}

	public TableType getType() {
		return type;
	}

	public void setType(TableType type) {
		this.type = type;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "LoadConfigFile{" +
				"type=" + type +
				", map=" + map +
				'}';
	}
}