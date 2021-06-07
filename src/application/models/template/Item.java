package application.models.template;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;

public class Item implements Externalizable {
	private Integer id;
	private String type;
	private Object item;
	private Point lastposition;
	private Point position;
	private OptionsItem options = new OptionsItem();
	private static final long serialVersionUID = 1L;

	public Item() {
		super();
	}

	public Item(Integer id, String type, Object item, Point position) {
		super();
		this.id = id;
		this.type = type;
		this.item = item;
		this.position = position;
		this.lastposition = position;
	}

	public Item(Integer id, String type, Object item, Point lastposition, Point position) {
		super();
		this.id = id;
		this.type = type;
		this.item = item;
		this.lastposition = lastposition;
		this.position = position;
	}

	@Override
	public void writeExternal(ObjectOutput out) {
		for (Field f : getClass().getDeclaredFields()) {
			if (f.getName() == "serialVersionUID")
				continue;
			try {
				out.writeObject(f.get(this));
			} catch (Exception e) {
				System.out.println(getClass().getName() + " - value: type was not write '" + f.getName() + "' : '"
						+ f.getType() + "'");
			}
		}
	}

	@Override
	public void readExternal(ObjectInput in) {
		for (Field f : getClass().getDeclaredFields()) {
			if (f.getName() == "serialVersionUID")
				continue;
			try {
				f.set(this, in.readObject());
			} catch (Exception e) {
				System.out.println(getClass().getName() + " - value: type was not read '" + f.getName() + "' : '"
						+ f.getType() + "'");
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getItem() {
		return item;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public Point getPosition() {
		return this.position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Point getLastposition() {
		return this.lastposition;
	}

	public void setLastposition(Point lastposition) {
		this.lastposition = lastposition;
	}

	public OptionsItem getOptions() {
		return options;
	}

	public void setOptions(OptionsItem options) {
		this.options = options;
	}
}
