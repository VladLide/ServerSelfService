package application.models.template;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.HumanReadablePlacement;

import javafx.geometry.Pos;

public class OptionsItem implements Externalizable {
	private FontItem font = new FontItem();
	private Double width = 0.0;
	private Double height = 0.0;
	private Double widthModule = 2.0;
	private String humanReadablePlacement = HumanReadablePlacement.HRP_BOTTOM.getName();
	private Double quietZone = 16.0;
	private String alignment = Pos.CENTER.name();
	private String wrapText = "false";
	private Double lineSpacing = 0.0;
	private Double[] indent = {0.0,0.0,0.0,0.0};
	private Double borderWidth = 0.0;
	private Double rotate = 0.0;
	private static final long serialVersionUID = 1L;
	
   public OptionsItem() {
		super();
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
			   /*switch(Utils.getType(f)) {
				case "Integer":*/
					f.set(this,in.readObject());
				/*break;
				case "int":
					f.set(this,(int)in.readObject());
				break;
				case "FontItem":
					f.set(this,(FontItem)in.readObject());
				break;
				case "Double": 
					f.set(this,(Double)in.readObject());
				break;
				case "String":
					f.set(this,(String)in.readObject());
				break;
				default:
					System.out.println(table+": type was not found " + f.getName()+":"+f.getType());
			}*/
			}catch (Exception e) {
				System.out.println(getClass().getName()+" - value: type was not read '" + f.getName()+"' : '"+f.getType()+"'");
			}
	   }
   }

	public FontItem getFont() {
		return font;
	}
	public void setFont(FontItem font) {
		this.font = font;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public Double getWidthModule() {
		return widthModule;
	}
	public void setWidthModule(Double widthModule) {
		this.widthModule = widthModule;
	}
	public Double getQuietZone() {
		return quietZone;
	}
	public void setQuietZone(Double quietZone) {
		this.quietZone = quietZone;
	}
	public String getHumanReadablePlacement() {
		return humanReadablePlacement;
	}
	public void setHumanReadablePlacement(String humanReadablePlacement) {
		this.humanReadablePlacement = humanReadablePlacement;
	}
	public String getAlignment() {
		return alignment;
	}
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
	public String getWrapText() {
		return wrapText;
	}
	public void setWrapText(String wrapText) {
		this.wrapText = wrapText;
	}
	public Double getLineSpacing() {
		return lineSpacing;
	}
	public String getLineSpacingString() {
		return lineSpacing+"";
	}
	public void setLineSpacing(Double lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	public Double[] getIndent() {
		return indent;
	}
	public void setIndent(Double[] indent) {
		this.indent = indent;
	}
	public String getIndentString() {
		return StringUtils.join(indent,";");
	}
	public void setIndent(String indent) {
		String[] arr = indent.split(";");
		Double n = 0.0;
		for(int i=0;i<4;i++) {
			try {
				n=Double.parseDouble(arr[i].replace(",", "."));
			}catch (Exception e) {
				n=null;
			}
			if(n!=null)
				this.indent[i] = n;
		}
	}
	public Double getBorderWidth() {
		return borderWidth;
	}
	public void setBorderWidth(Double borderWidth) {
		this.borderWidth = borderWidth;
	}
	public Double getRotate() {
		return rotate;
	}
	public void setRotate(Double rotate) {
		this.rotate = rotate;
	}
}
