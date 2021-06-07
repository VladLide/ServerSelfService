package application.models.objectinfo;

import application.models.net.mysql.tables.Sections;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class NodeTree {
	private AnchorPane status = null;
	private int id = 0;
	private String name;
	private String type;
	private int level;
	private Object object;
	private NodeTree upObject;

	@Override
	public String toString() {
		return name;
	}

	public NodeTree(String name, String type, int level, Object object) {
		super();
		this.name = name;
		this.level = level;
		this.object = object;
		this.upObject = null;
		this.setStatus(type);
	}

	public NodeTree(String name, String type, int level, Object object, NodeTree upObject) {
		super();
		this.name = name;
		this.level = level;
		this.object = object;
		this.upObject = upObject;
		this.setStatus(type);
	}

	public NodeTree(int id, String name, String type, int level, Object object, NodeTree upObject) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
		this.object = object;
		this.upObject = upObject;
		this.setStatus(type);
	}

	public AnchorPane getStatus() {
		return status;
	}

	public void setStatus(String type) {
		this.type = type;
		try {
			status = new AnchorPane();
			switch (type) {
			case "Scales":
				status.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			case "Sections":
				status.setBackground(new Background(new BackgroundImage(((Sections) object).getImage(status),
						BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
						new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true,
								false, true, false))));
				break;
			default:
				status = null;
				break;
			}
		} catch (Exception e) {
			status = null;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public String getTypeObj() {
		String[] type = object.getClass().getTypeName().replace(".", " ").split(" ");
		return type[type.length - 1];
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public NodeTree getUpObject() {
		return upObject;
	}

	public void setUpObject(NodeTree upObject) {
		this.upObject = upObject;
	}
}
