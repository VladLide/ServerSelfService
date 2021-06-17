package application.models.template;

import application.models.net.mysql.tables.Codes;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Code {
	private String code = "12345";
	private Codes barcode = new Codes(true);
	private String unit;
	private AbstractBarcodeBean bean = new EAN13Bean();
	private final int dpi = 203;

	public Code(String unit) {
		this.unit = unit.replace(",", "").replace(".", "");
	}

	public Code(Codes barcode, int cod, String unit) {
		super();
		this.barcode = barcode;
		this.code = Integer.toString(cod);
		this.unit = unit.replace(",", "").replace(".", "");
	}

	public String generateCode() {
		String result = "";
		String prefix = this.barcode.getPrefix_val();
		String maska = this.barcode.getMask();
		for (int i = maska.length() - 1; i >= 0; i--) {
			switch (maska.charAt(i)) {
			case 'P': {
				if (prefix.length() > 0) {
					result = prefix.substring(prefix.length() - 1) + result;
					prefix = prefix.substring(0, prefix.length() - 1);
				} else
					result = "2" + result;
				break;
			}
			case 'C': {
				if (code.length() > 0) {
					result = code.substring(code.length() - 1) + result;
					code = code.substring(0, code.length() - 1);
				} else
					result = "0" + result;
				break;
			}
			case 'U': {
				if (unit.length() > 0) {
					result = unit.substring(unit.length() - 1) + result;
					unit = unit.substring(0, unit.length() - 1);
				} else
					result = "0" + result;
				break;
			}
			}
		}
		System.out.println(result + Code.checkSum(result));
		return result;
	}

	public String generateStandart(int n, String str, String copyStr) {
		String result = new String(str);
		while (result.length() < n) {
			result = copyStr + result;
		}
		return result;
	}

	public void setParameters(double ModuleWidth, double BarHeight, double FontSize, double QuietZone,
			boolean doQuietZone) {
		this.bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
		this.bean.setModuleWidth(ModuleWidth);
		this.bean.setBarHeight(BarHeight);
		this.bean.setFontSize(FontSize);
		this.bean.setQuietZone(QuietZone);
		this.bean.doQuietZone(doQuietZone);
	}

	public BufferedImage generate(Boolean def) {
		String value = this.generateCode();
		if (def) {
			this.bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
			this.bean.setModuleWidth(2);
			this.bean.setBarHeight(30.0);
			this.bean.setFontSize(12.0);
			this.bean.setQuietZone(16.0);
			this.bean.doQuietZone(true);
		}
		BarcodeDimension dim = this.bean.calcDimensions(value);
		int width = (int) dim.getWidth(0) + 20;
		int height = (int) dim.getHeight(0);

		BufferedImage imgtext = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = imgtext.createGraphics();

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		g2d.setColor(Color.BLACK);

		try {
			this.bean.generateBarcode(new Java2DCanvasProvider(g2d, 0), value);
		} catch (IllegalArgumentException e) {
			g2d.drawRect(0, 0, width - 1, height - 1);
			g2d.drawString(value, 2, height - 3);
		}

		g2d.dispose();
		return imgtext;
	}

	public static int checkSum(String code) {
		int val = 0;
		for (int i = 0; i < code.length() - 1; i++) {
			val += ((int) Integer.parseInt(code.charAt(i) + "")) * ((i % 2 == 0) ? 1 : 3);
		}
		int checksum_digit = (10 - (val % 10)) % 10;
		System.out.println(checksum_digit);
		return checksum_digit;
	}

	public void setMsgPosition(String humanReadablePlacement) {
		this.bean.setMsgPosition(HumanReadablePlacement.byName(humanReadablePlacement));
	}

	public void setModuleWidth(Double ModuleWidth) {
		this.bean.setModuleWidth(ModuleWidth);
	}

	public void setBarHeight(Double BarHeight) {
		this.bean.setBarHeight(BarHeight);
	}

	public void setFontSize(Double FontSize) {
		this.bean.setFontSize(FontSize);
	}

	public void setQuietZone(Double QuietZone) {
		this.bean.setQuietZone(QuietZone);
	}

	public void setDoQuietZone(Boolean doQuietZone) {
		this.bean.doQuietZone(doQuietZone);
	}
}
