package application.models.template;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Externalizable;
import java.lang.reflect.Field;

public class Point implements Externalizable {
	public Double x;
	public Double y;
	private static final long serialVersionUID = 1L;

	public Point() {
		super();
	}

	public Point(Double x, Double y) {
		super();
		this.x = x;
		this.y = y;
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
}
