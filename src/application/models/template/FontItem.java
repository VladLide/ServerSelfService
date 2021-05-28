package application.models.template;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FontItem implements Externalizable{
	public String name = "System";
	public String fontWeight = "NORMAL";
	public String fontPosture = "REGULAR";
	public Double size = 24.0;
	private static final long serialVersionUID = 1L;
	
	public FontItem() {
		super();
	}
	public FontItem(String name, Double size) {
		super();
		this.name = name;
		this.fontWeight = "NORMAL";
		this.fontPosture = "REGULAR";
		this.size = size;
	}
	public FontItem(String name, String fontWeight, Double size) {
		super();
		this.name = name;
		this.fontWeight = fontWeight;
		this.fontPosture = "REGULAR";
		this.size = size;
	}
	public FontItem(String name, String fontWeight, String fontPosture, Double size) {
		super();
		this.name = name;
		this.fontWeight = fontWeight;
		this.fontPosture = fontPosture;
		this.size = size;
	}
	
   @Override
   public void writeExternal(ObjectOutput out) {
	   for (Field f : getClass().getDeclaredFields()) {
		   if(f.getName()=="serialVersionUID") continue;
			try {
				out.writeObject(f.get(this));
			}catch (Exception e) {
				System.out.println(getClass().getName()+" - value: type was not write '" + f.getName()+"' : '"+f.getType()+"'");
			}
	   }
   }
   @Override
   public void readExternal(ObjectInput in) {
	   for (Field f : getClass().getDeclaredFields()) {
		   if(f.getName()=="serialVersionUID") continue;
			try {
				f.set(this,in.readObject());
			}catch (Exception e) {
				System.out.println(getClass().getName()+" - value: type was not read '" + f.getName()+"' : '"+f.getType()+"'");
			}
	   }
   }
	   
	public FontWeight getFontWeight() {
		return FontWeight.findByName(fontWeight);
	}
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}
	public FontPosture getFontPosture() {
		return FontPosture.findByName(fontPosture);
	}
	public void setFontPosture(String fontPosture) {
		this.fontPosture = fontPosture;
	}
	
}
