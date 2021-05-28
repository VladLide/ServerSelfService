package application.models.template;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import com.google.zxing.WriterException;

import application.controllers.windows.MainWindowCtrl;
import application.models.Utils;
import application.models.net.mysql.interface_tables.ProductItem;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Scales;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;

public class PrintTemplate {
	private PaneObj label;
	private ProductItem goods;
	private TemplatePanelCtrl template;
	private String weight;
	public boolean useCount = true;
	
	public PrintTemplate(PaneObj lable, ProductItem goodsInfo) {
		super();
		this.label = lable;
		this.goods = goodsInfo;
		this.template = new TemplatePanelCtrl();
	}
	public boolean PrintTemplate(String code, Template template, MainWindowCtrl mainWindow) {
		ZonedDateTime timetmp = ZonedDateTime.now();
    	String date = timetmp.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.YYYY"));
    	String time = timetmp.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			this.label = template.readObjBlob(this.template,null);
			this.label.getItems().forEach(v -> {
				System.out.println("Value: " + v);
				switch(v.getType()) {
				case "namePLU":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "price":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "barcode":{
					Barcode controlCode = new Barcode(new Codes("", 0, "", "free_text="+code, "0"),"");
					Image image = optionsBarcode(v.getOptions(),controlCode);
			        ((Pane)v.getItem()).setPrefSize(image.getWidth(), image.getHeight());
			        ((Pane)v.getItem()).setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
					 break;
				}
				case "date":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "endDate":{
					((Label)v.getItem()).setText("");
					break;
				}
				case "weight":{
					((Label) v.getItem()).setText("");
					 break;
				}
				case "cost":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "top":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "bottom":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "codePLU":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "NB":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "ingredients":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "tara":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "weightUp":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "weightDown":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "numberTemplate":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "timePrint":{
					((Label)v.getItem()).setText(time);
					 break;
				}
				case "timePacking":{
					((Label)v.getItem()).setText(time);
					 break;
				}
				case "daySave":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "timeSave":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "qrcode":{
					QRCode controlCode = new QRCode();
					controlCode.setQrCodeText(code);
					BufferedImage img = null;
					try {
						if(v.getOptions()!=null) {
							controlCode.setSize(v.getOptions().getWidth().intValue());
							img = controlCode.createQRImage();
						}else {
							img = controlCode.createQRImage();
						}
					} catch (WriterException e) {
						e.printStackTrace();
					}
			        Image image = SwingFXUtils.toFXImage(img, null);
			        ((Pane)v.getItem()).setPrefSize(image.getWidth(), image.getHeight());
			        ((Pane)v.getItem()).setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
					 break;
				}
				case "nameStock":{
					((Label)v.getItem()).setText("");
					break;
				}
				case "stock":{
					((Label)v.getItem()).setText("");
					break;
				}
				case "dateStock":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "timeStock":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "endDateStock":{
					((Label)v.getItem()).setText("");
					 break;
				}
				case "endTimeStock":{
					((Label)v.getItem()).setText("");
					 break;
				}
				};
			});
			this.printer(mainWindow.getPrinter());
		return false;
	}
	public boolean Print(Scales scaleC, ProductItem goodsInfo, MainWindowCtrl mainWindow, String cost) {
		if(goodsInfo!=null) {
			weight = ""; String tara = mainWindow.serialPort.tara;
			switch(goodsInfo.getType()) {
				case 0:{
					weight = mainWindow.serialPort.weight; 
				}
				break;
				case 1:{
					weight = mainWindow.getEnterQuantity().getCount().getText();
				}
				break;
				default:{}
			}
			ZonedDateTime timetmp = ZonedDateTime.now();
	    	String date = timetmp.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.YYYY"));
	    	String time = timetmp.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			this.label = goodsInfo.getTemplate().readObjBlob(this.template,null);
			this.label.getItems().forEach(v -> {
				System.out.println("Value: " + v);
				switch(v.getType()) {
				case "namePLU":{
					((Label)v.getItem()).setText(this.goods.getName());
					 break;
				}
				case "price":{
					String str = String.format("%.2f",goods.getPrice()).replace(",", ".");//(goodsInfo.getStock()!=null)?String.format("%.2f",goodsInfo.getPriceStockFloat()).replace(",", "."):String.format("%.2f",goods.getPrice()).replace(",", ".");
					((Label)v.getItem()).setText(str);
					 break;
				}
				case "barcode":{
					Barcode controlCode = new Barcode(goodsInfo, weight);
					Image image = optionsBarcode(v.getOptions(),controlCode);
			        ((Pane)v.getItem()).setPrefSize(image.getWidth(), image.getHeight());
			        ((Pane)v.getItem()).setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
					 break;
				}
				case "date":{
					((Label)v.getItem()).setText(date);
					 break;
				}
				case "endDate":{
					if(goodsInfo.getBefore_validity()>0) {
						SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
						Date myDate = Date.from(timetmp.toInstant());
						Calendar c = Calendar.getInstance();
						c.setTime(myDate);
						c.add(Calendar.DATE, ((goodsInfo.getBefore_validity()%24)<=12)?(goodsInfo.getBefore_validity() - (goodsInfo.getBefore_validity()%24))/24:(goodsInfo.getBefore_validity() - 
								(goodsInfo.getBefore_validity()%24))/24+1);
						((Label)v.getItem()).setText(format.format(c.getTime()));
					}else {
						((Label)v.getItem()).setText("");
					}
					break;
				}
				case "weight":{
					((Label) v.getItem()).setText(weight);
					 break;
				}
				case "cost":{
					((Label)v.getItem()).setText(cost);
					 break;
				}
				case "top":{
					((Label)v.getItem()).setText(scaleC.getConfigItem("top_message"));
					 break;
				}
				case "bottom":{
					((Label)v.getItem()).setText(scaleC.getConfigItem("bottom_message"));
					 break;
				}
				case "codePLU":{
					((Label)v.getItem()).setText(goodsInfo.getNumber_button()+"");
					 break;
				}
				case "NB":{
					((Label)v.getItem()).setText(goodsInfo.getPre_code()+"");
					 break;
				}
				case "ingredients":{
					((Label)v.getItem()).setText(goodsInfo.getIngredients());
					 break;
				}
				case "tara":{
					String taraStr = "";
					try{
						taraStr = (Double.parseDouble(tara)>0.0)?tara:"";
					}catch (Exception e) {
						taraStr = "";
					}
					((Label)v.getItem()).setText(taraStr);
					 break;
				}
				case "weightUp":{
					((Label)v.getItem()).setText(String.format("%.3f",(Double.parseDouble(weight)+Double.parseDouble(tara))).replace(",", "."));
					 break;
				}
				case "weightDown":{
					((Label)v.getItem()).setText(weight);
					 break;
				}
				case "numberTemplate":{
					((Label)v.getItem()).setText("0");
					 break;
				}
				case "timePrint":{
					((Label)v.getItem()).setText(time);
					 break;
				}
				case "timePacking":{
					((Label)v.getItem()).setText(time);
					 break;
				}
				case "daySave":{
					((Label)v.getItem()).setText((((goodsInfo.getBefore_validity()%24)<=12)?(goodsInfo.getBefore_validity() - (goodsInfo.getBefore_validity()%24))/24:(goodsInfo.getBefore_validity() - 
							(goodsInfo.getBefore_validity()%24))/24+1)+"");
					 break;
				}
				case "timeSave":{
					((Label)v.getItem()).setText(goodsInfo.getBefore_validity()+"");
					 break;
				}
				case "qrcode":{
					QRCode controlCode = new QRCode(scaleC,goodsInfo,mainWindow,cost);
					controlCode.setQRCodeText();
					BufferedImage img = null;
					try {
						if(v.getOptions()!=null) {
							controlCode.setSize(v.getOptions().getWidth().intValue());
							img = controlCode.createQRImage();
						}else {
							img = controlCode.createQRImage();
						}
					} catch (WriterException e) {
						e.printStackTrace();
					}
			        Image image = SwingFXUtils.toFXImage(img, null);
			        ((Pane)v.getItem()).setPrefSize(image.getWidth(), image.getHeight());
			        ((Pane)v.getItem()).setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
					 break;
				}
				case "nameStock":{
					if(goodsInfo.getStock()!=null) {
						((Label)v.getItem()).setText(goodsInfo.getStock().getName()+"");
					}else {
						((Label)v.getItem()).setText("");
					}
					break;
				}
				case "stock":{
					if(goodsInfo.getStock()!=null) {
						Stocks stock = goodsInfo.getStock();
						String s = ((stock.getSum_stocks()>0)?stock.getSum_stocks()+" грн":"")+(((stock.getSum_stocks()>0)&&(stock.getPercent()>0))?" - ":"")+((stock.getPercent()>0)?stock.getPercent()+"%":"");
						((Label)v.getItem()).setText(s);
					}else {
						((Label)v.getItem()).setText("");
					}
					break;
				}
				case "dateStock":{
					if(goodsInfo.getStock()!=null) {
						((Label)v.getItem()).setText(goodsInfo.getStock().getDate_on("dd.MM.YYYY"));
					}else {
						((Label)v.getItem()).setText("");
					}
					 break;
				}
				case "timeStock":{
					if(goodsInfo.getStock()!=null) {
						((Label)v.getItem()).setText(goodsInfo.getStock().getDate_on("HH:mm"));
					}else {
						((Label)v.getItem()).setText("");
					}
					 break;
				}
				case "endDateStock":{
					if(goodsInfo.getStock()!=null) {
						((Label)v.getItem()).setText(goodsInfo.getStock().getDate_end("dd.MM.YYYY"));
					}else {
						((Label)v.getItem()).setText("");
					}
					 break;
				}
				case "endTimeStock":{
					if(goodsInfo.getStock()!=null) {
						((Label)v.getItem()).setText(goodsInfo.getStock().getDate_end("HH:mm"));
					}else {
						((Label)v.getItem()).setText("");
					}
					 break;
				}
				};
			});
			if(useCount)goodsInfo.setUseCount(scaleC.getDb());
			this.printer(mainWindow.getPrinter());
		}
		return false;
	}
	public void printer(Printer printer){
		File file = new File(Utils.getPath("tmp", "img.gif"));
		WritableImage writableImage = new WritableImage(Math.round(this.label.getWidthContent()),Math.round(this.label.getHeightContent()));
        WritableImage img = this.label.getPane().snapshot(new SnapshotParameters(), writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
        BufferedImage image = PlanarImage.wrapRenderedImage(renderedImage).getAsBufferedImage();
        try {
			ImageIO.write(image, "gif", file);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
        printer.setSize((double)this.label.getWidth(),(double)this.label.getHeight());
        printer.setFoto(image);
        printer.printFoto();
	}
	public BufferedImage rotateImage(BufferedImage buffImage, double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));
        int width = buffImage.getWidth();
        int height = buffImage.getHeight();
        int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
        int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);
        BufferedImage rotatedImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = rotatedImage.createGraphics();
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, nWidth, nHeight);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
        graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
        graphics.drawImage(buffImage, 0, 0,null);
        graphics.dispose();
        return rotatedImage;
    }
	public static Label createViewLabel(FontItem font, String str, AnchorPane pane) {
		Label label = new Label(str);
		if(font!=null) {
			label.setFont(Font.font(font.name,FontWeight.findByName(font.fontWeight),font.size));
		}else {
			label.setFont(new Font("System", 26));
		}
		label.setLayoutX(0);
		label.setLayoutY(0);
		label.setTranslateX(10);
		label.setTranslateY(50);
		return label;
	}
	public static Pane createViewBarcode(OptionsItem Oitem, PaneObj pane) {
		Barcode controlCode = new Barcode(new Codes(0,false), "15.506");
		controlCode.setBarHeight(Oitem.getHeight());
		controlCode.setDoQuietZone((Oitem.getQuietZone()>0.0)?true:false);
		controlCode.setFontSize(Oitem.getFont().size);
		controlCode.setModuleWidth(Oitem.getWidthModule());
		controlCode.setMsgPosition(Oitem.getHumanReadablePlacement());
		controlCode.setQuietZone(Oitem.getQuietZone());
		BufferedImage img = controlCode.generate(false);
        Image image = SwingFXUtils.toFXImage(img, null);
		Pane label = new Pane();
		label.setPrefSize(image.getWidth(), image.getHeight());
		label.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
				BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
		/*label.setImage(image);*/
		label.setLayoutX(0);
		label.setLayoutY(0);
		return label;
	}
	public static Image optionsBarcode(OptionsItem Oitem, Barcode controlCode) {
		controlCode.setBarHeight(Oitem.getHeight());
		controlCode.setDoQuietZone((Oitem.getQuietZone()>0.0)?true:false);
		controlCode.setFontSize(Oitem.getFont().size);
		controlCode.setModuleWidth(Oitem.getWidthModule());
		controlCode.setMsgPosition(Oitem.getHumanReadablePlacement());
		controlCode.setQuietZone(Oitem.getQuietZone());
		BufferedImage img = controlCode.generate(false);
        return SwingFXUtils.toFXImage(img, null);
	}
	public static Rectangle createViewRectangle(OptionsItem Oitem, PaneObj pane){
		Rectangle rectangle = new Rectangle();
		rectangle.setStroke(Color.BLACK);
		rectangle.setFill(Color.rgb(255, 255, 255, 0.0));
		if(Oitem!=null) {
			rectangle.setHeight(Oitem.getHeight());
			rectangle.setWidth(Oitem.getWidth());
			rectangle.setStrokeWidth(Oitem.getBorderWidth());
		}else {
			rectangle.setHeight(50);
			rectangle.setWidth(50);
			rectangle.setStrokeWidth(1);
		}
		rectangle.setLayoutX(0);
		rectangle.setLayoutY(0);
		return rectangle;
	}
	public static Line createViewLine(OptionsItem Oitem, PaneObj pane) {
		Line line = new Line(0.0, 0.0, 0.0, 0.0);
		if(Oitem!=null) {
			line.setRotate(Oitem.getRotate());
			line.setStartX(Oitem.getWidth());
			line.setStrokeWidth(Oitem.getBorderWidth());
		}else {
			line.setRotate(0);
			line.setStrokeWidth(1);
		}
		line.setLayoutX(0);
		line.setLayoutY(0);
		return line;
	}
	public static Pane createViewQRCode(OptionsItem Oitem, PaneObj pane) throws WriterException {
		QRCode controlCode = new QRCode();
		BufferedImage img = null;
		if(Oitem!=null) {
			controlCode.setSize(Oitem.getWidth().intValue());
			img = controlCode.createQRImage();
		}else {
			img = controlCode.createQRImage();
		}
        Image image = SwingFXUtils.toFXImage(img, null);
		Pane label = new Pane();
		label.setPrefSize(image.getWidth(), image.getHeight());
		label.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
				BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
		label.setLayoutX(0);
		label.setLayoutY(0);
		return label;
	}
}
