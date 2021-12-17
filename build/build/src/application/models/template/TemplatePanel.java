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

import application.controllers.parts.TemplatePanelCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.models.Utils;
import application.models.net.database.mysql.tables.Codes;
import application.models.net.database.mysql.tables.Scales;
import application.models.net.database.mysql.tables.Stocks;
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

public class TemplatePanel {
	private PaneObj label;
	// private PLUInfo goods;
	private TemplatePanelCtrl template;
	private String weight;
	public boolean useCount = true;

	public TemplatePanel(PaneObj lable/* , PLUInfo goodsInfo */) {
		super();
		this.label = lable;
		// this.goods = goodsInfo;
		this.template = new TemplatePanelCtrl();
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
		graphics.drawImage(buffImage, 0, 0, null);
		graphics.dispose();
		return rotatedImage;
	}

	public static Label createViewLabel(FontItem font, String str, AnchorPane pane) {
		Label label = new Label(str);
		if (font != null) {
			label.setFont(Font.font(font.name, FontWeight.findByName(font.fontWeight), font.size));
		} else {
			label.setFont(new Font("System", 26));
		}
		label.setLayoutX(0);
		label.setLayoutY(0);
		label.setTranslateX(10);
		label.setTranslateY(50);
		return label;
	}

	public static Pane createViewBarcode(OptionsItem Oitem, PaneObj pane) {
		Code controlCode = new Code(new Codes("2/5/5", "22", "PPCCCCCUUUUU"), 12345, "15.506");//
		controlCode.setBarHeight(Oitem.getHeight());
		controlCode.setDoQuietZone((Oitem.getQuietZone() > 0.0) ? true : false);
		controlCode.setFontSize(Oitem.getFont().size);
		controlCode.setModuleWidth(Oitem.getWidthModule());
		controlCode.setMsgPosition(Oitem.getHumanReadablePlacement());
		controlCode.setQuietZone(Oitem.getQuietZone());
		BufferedImage img = controlCode.generate(false);
		Image image = SwingFXUtils.toFXImage(img, null);
		Pane label = new Pane();
		label.setPrefSize(image.getWidth(), image.getHeight());
		label.setBackground(
				new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
						BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(),
								BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
		/* label.setImage(image); */
		label.setLayoutX(0);
		label.setLayoutY(0);
		return label;
	}

	public static Image optionsBarcode(OptionsItem Oitem, Code controlCode) {
		controlCode.setBarHeight(Oitem.getHeight());
		controlCode.setDoQuietZone((Oitem.getQuietZone() > 0.0) ? true : false);
		controlCode.setFontSize(Oitem.getFont().size);
		controlCode.setModuleWidth(Oitem.getWidthModule());
		controlCode.setMsgPosition(Oitem.getHumanReadablePlacement());
		controlCode.setQuietZone(Oitem.getQuietZone());
		BufferedImage img = controlCode.generate(false);
		return SwingFXUtils.toFXImage(img, null);
	}

	public static Rectangle createViewRectangle(OptionsItem Oitem, PaneObj pane) {
		Rectangle rectangle = new Rectangle();
		rectangle.setStroke(Color.BLACK);
		rectangle.setFill(Color.rgb(255, 255, 255, 0.0));
		if (Oitem != null) {
			rectangle.setHeight(Oitem.getHeight());
			rectangle.setWidth(Oitem.getWidth());
			rectangle.setStrokeWidth(Oitem.getBorderWidth());
		} else {
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
		if (Oitem != null) {
			line.setRotate(Oitem.getRotate());
			line.setStartX(Oitem.getWidth());
			line.setStrokeWidth(Oitem.getBorderWidth());
		} else {
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
		if (Oitem != null) {
			controlCode.setSize(Oitem.getWidth().intValue());
			img = controlCode.createQRImage();
		} else {
			img = controlCode.createQRImage();
		}
		Image image = SwingFXUtils.toFXImage(img, null);
		Pane label = new Pane();
		label.setPrefSize(image.getWidth(), image.getHeight());
		label.setBackground(
				new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
						BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(),
								BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
		label.setLayoutX(0);
		label.setLayoutY(0);
		return label;
	}
}
