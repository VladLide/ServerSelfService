package application.models.template;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import application.controllers.windows.MainWindowCtrl;
import application.models.net.mysql.interface_tables.ProductItem;
import application.models.net.mysql.tables.Scales;

public class QRCode {
	private String qrCodeText = "https://www.journaldev.com|chfhfhgjhgkj";
	private int size = 80;
	private Scales scale = null;
	private ProductItem plu = null;
	private MainWindowCtrl mainWindow = null;
	private String cost = "";

	public QRCode() {}

	public QRCode(Scales scale, ProductItem plu, MainWindowCtrl mainWindow, String cost) {
		super();
		this.scale = scale;
		this.mainWindow = mainWindow;
		this.plu = plu;
		this.cost = cost;
	}

	public void setQRCodeText() {
		qrCodeText = "";
    	/*this.plu.getCodeObj().getItemCode().forEach((k,v)->{
    		String key = new TextBox().itemCode.get(this.plu.getCodeObj().getTypeInt())[this.plu.getCodeObj().getPlaceItem().get(k)][1];
    		switch(key) {
	    		case "free_text":{
	    			qrCodeText += v;
		    		break;
	    		}
	    		case "separator":{
	    			qrCodeText += v;
		    		break;
	    		}
		    	case "prefix":{
		    		qrCodeText += v;
		    		break;
		    	}
		    	case "price_stocks":{
		    		qrCodeText += String.format("%.2f",plu.getPriceStockFloat()).replace(",", ".")+v;
		    		break;
		    	}
		    	case "cost":{
		    		qrCodeText += cost+v;
		    		break;
		    	}
		    	case "unit":{
					String weight = "0.000";
		    		switch(plu.getType()) {
						case 0:{
							weight = mainWindow.serialPort.weight.replace(".", ","); 
						}
						break;
						case 1:{
							weight = mainWindow.getEnterQuantity().getCount().getText();
						}
						break;
						default:{}
					}
		    		qrCodeText += weight+v;
		    		break;
		    	}
		    	case "code":{
		    		qrCodeText += plu.getNumber_button()+v;
		    		break;
		    	}
		    	case "button_code":{
		    		qrCodeText += plu.getPre_code()+v;
		    		break;
		    	}
		    	case "section":{
		    		qrCodeText += mainWindow.getHome().getClient().getCurrentIdSection()+v;
		    		break;
		    	}
		    	case "cur_time":{
		    		ZonedDateTime timetmp = ZonedDateTime.now();
		    		if(v.length()>2) {
		    			qrCodeText += timetmp.format(DateTimeFormatter.ofPattern(v));//"dd.MM.YYYY HH:mm:ss"
		    		}else {
		    			qrCodeText += timetmp.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss"));//"dd.MM.YYYY HH:mm:ss"
		    		}
		    		break;
		    	}
		    	case "full_user_name":{
		    		
		    		qrCodeText += plu.getPre_code()+v;
		    		break;
		    	}
		    	case "price":{
		    		qrCodeText += String.format("%.2f",plu.getPrice()).replace(",", ".")+v;
		    		break;
		    	}
		    	case "data_on_stock":{
	    			qrCodeText += (v.length()>2)?plu.getStock().getDate_on(v):(plu.getStock().getDate_on(v)+v);
		    		/*if(plu.getStock().getDate_on()!=null) {
			    		if(v.length()>2) {
			    			qrCodeText += plu.getStock().getDate_on().format(DateTimeFormatter.ofPattern(v));
			    		}else {
			    			qrCodeText += plu.getStock().getDate_on().format(DateTimeFormatter.ofPattern("dd.MM.YYYY"))+v;
			    		}
		    		}else {
		    			ZonedDateTime timetmp = ZonedDateTime.now();
		    			if(v.length()>2) {
			    			qrCodeText += timetmp.format(DateTimeFormatter.ofPattern(v));
			    		}else {
			    			qrCodeText += timetmp.format(DateTimeFormatter.ofPattern("dd.MM.YYYY"))+v;
			    		}
		    		}*/
		    		/*break;
		    	}
		    	case "val_stock":{
		    		qrCodeText += String.format("%.0f",plu.getStock().getPercent()).replace(",", ".")+v;
		    		break;
		    	}
		    	case "data_end_stock":{
		    		qrCodeText += (v.length()>2)?plu.getStock().getDate_end(v):(plu.getStock().getDate_end(v)+v);
		    		/*if(plu.getStock().getDate_on()!=null) {
			    		if(v.length()>2) {
			    			qrCodeText += plu.getStock().getDate_on().plusHours(plu.getStock().getCount_time()).format(DateTimeFormatter.ofPattern(v));
			    		}else {
			    			qrCodeText += plu.getStock().getDate_on().plusHours(plu.getStock().getCount_time()).format(DateTimeFormatter.ofPattern("dd.MM.YYYY"))+v;
			    		}
		    		}else {
		    			ZonedDateTime timetmp = ZonedDateTime.now();
		    			if(v.length()>2) {
			    			qrCodeText += timetmp.plusHours(plu.getStock().getCount_time()).format(DateTimeFormatter.ofPattern(v));
			    		}else {
			    			qrCodeText += timetmp.plusHours(plu.getStock().getCount_time()).format(DateTimeFormatter.ofPattern("dd.MM.YYYY"))+v;
			    		}
		    		}*/
		    		/*break;
		    	}
    		}
    	});*/
	}

	public String generateStandart(int n, String str, String copyStr) {
		String result = new String(str);
		while (result.length() < n) {
			result = copyStr + result;
		}
		return result;
	}

	public BufferedImage createQRImage() throws WriterException {
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hintMap.put(EncodeHintType.MARGIN, 1);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		return image;
	}

	public String getQrCodeText() {
		return qrCodeText;
	}

	public void setQrCodeText(String qrCodeText) {
		this.qrCodeText = qrCodeText;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}