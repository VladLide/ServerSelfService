package application.models.template;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SaveObj  implements Externalizable  {
	private Integer id;
	private String name;
	private Float widthContent;
	private Float heightContent;
	private String color;
	private Map<Integer,Item> Content;
	private static final long serialVersionUID = 1L;
	
	public SaveObj() {
		super();
		this.Content = new HashMap<Integer,Item>();
	}
	public SaveObj(int id,String name, float widthContent, float heightContent,  Map<Integer,Item> content) {
		super();
		this.id = id;
		this.name = name;
		this.widthContent = widthContent;
		this.heightContent = heightContent;
		this.Content = content;
	}
	public SaveObj(int id,String name, float widthContent, float heightContent) {
		super();
		this.id = id;
		this.name = name;
		this.widthContent = widthContent;
		this.heightContent = heightContent;
		this.Content = new HashMap<Integer,Item>();
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
   
	public float getWidthContent() {
		return this.widthContent;
	}
	public void setWidthContent(float widthContent) {
		this.widthContent = widthContent;
	}
	public float getHeightContent() {
		return this.heightContent;
	}
	public void setHeightContent(float heightContent) {
		this.heightContent = heightContent;
	}
	public Map<Integer,Item> getItems() {
		return this.Content;
	}
	public Item getItem(int index) {
		return this.Content.get(index);
	}
	public void setItems( Map<Integer,Item> content) {
		this.Content = content;
	}
	public void setItem(int id, Item content) {
		this.Content.put(id, content);
	}
	public void setItem(Item content) {
		this.Content.put(this.Content.size(), content);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
