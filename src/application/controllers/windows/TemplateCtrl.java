package application.controllers.windows;

import application.*;
import application.controllers.MainCtrl;
import application.enums.Operation;
import application.enums.OperationStatus;
import application.enums.PlaceType;
import application.enums.SectionType;
import application.models.*;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Templates;
import application.models.objectinfo.ItemTemplate;
import application.models.template.*;
import application.views.languages.uk.windows.TemplateInfo;
import com.google.zxing.WriterException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class TemplateCtrl {
	private final Stage stage = new Stage();
	private SizeTemplateCtrl size;
	private PaneObj paneSave;
	private Item currentItem = null;
	private Templates objTemplate = null;
	private MySQL db = null;
	private final Map<String, String> messages = new HashMap<>();
	private String pointMove = "(0.0;0.0)";
	private final DropShadow dropShadow = new DropShadow();
	private String ipAddress;
	private PlaceType placeType;
	private final Logger logger = LogManager.getLogger(TemplateCtrl.class);

	@FXML
	private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "window", "Template");
	@FXML
	private URL location = getClass().getResource(Utils.getView("window", "Template"));
	@FXML
	private AnchorPane allPanel;
	@FXML
	private AnchorPane editPanel;
	@FXML
	private AnchorPane template;
	@FXML
	private SplitPane allWork;
	@FXML
	private Button deleteObj;
	@FXML
	private TableView<Info2Col> infoParameterTable;
	@FXML
	private TableColumn<Info2Col, String> colNameTable;
	@FXML
	private TableColumn<Info2Col, String> colValueTable;
	@FXML
	private TableView<Info2Col> infoTable;
	@FXML
	private TableColumn<Info2Col, String> colNameInfoTable;
	@FXML
	private TableColumn<Info2Col, String> colValueInfoTable;
	@FXML
	private Button prev;
	@FXML
	private Button next;
	@FXML
	private Label nameTemplate;
	@FXML
	private Label nameObjTemplate;
	@FXML
	private ListView<ItemTemplate> listElement;
	@FXML
	private AnchorPane keyboard;
	@FXML
	private Button addBackground;
	@FXML
	private Button delBackground;
	@FXML
	private Button testPrint;
	@FXML
	private MenuBar topMenu;
	@FXML
	private MenuItem save;
	@FXML
	private MenuItem help;

	public TemplateCtrl(Templates item, MySQL db) {
		this.db = db;
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(MainWindowCtrl.getMainStage());
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(0.0);
		dropShadow.setOffsetY(0.0);
		dropShadow.setColor(Color.GREEN);
		dropShadow.setSpread(0.5);
		try {
			FXMLLoader loader = new FXMLLoader(location, resources);
			loader.setController(this);
			this.stage.setScene(new Scene(loader.load()));
			if (item != null) {
				this.paneSave = item.readObjBlob(null, this);
				this.objTemplate = item;
				try {
					this.template.setBackground(new Background(new BackgroundImage(item.getImage(this.template, 1),
							BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
							new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(),
									true, false, true, false))));
				} catch (Exception e) {
					this.template.setBackground(
							new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
				}
			} else {
				paneSave = new PaneObj();
				this.size = new SizeTemplateCtrl(this);
				size.setTemplates(Templates.getLName(db));
				this.size.showStage();
				this.objTemplate = new Templates(0, paneSave.getName());
				this.template.setBackground(
						new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.size != null && !size.isOpenEdit()) {
			this.close();
		}
		load();
	}

	public void show() {
		stage.getScene().setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.DELETE)
				this.deleteItem();
		});
		stage.showAndWait();
	}

	public void close() {
		stage.close();
	}

	public static void captureAndSaveDisplay(AnchorPane pane) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
		File file = fileChooser.showSaveDialog(null);
		if (file != null) {
			try {
				WritableImage img = pane.snapshot(new SnapshotParameters(), null);
				RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
				ImageIO.write(renderedImage, "png", file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void addImage() {
		File file = null;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Image");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpeg", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));
		file = fileChooser.showOpenDialog(stage);
		if (file == null) {
			TextBox.alertOpenDialog(AlertType.WARNING, "chooseImageNo");
		} else {
			this.objTemplate.setData(file, 1);
			try {
				template.setBackground(new Background(new BackgroundImage(this.objTemplate.getImage(template, 1),
						BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
						new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true,
								false, true, false))));
			} catch (Exception e) {
				template.setBackground(null);
				logger.error("ButtonWithImage: no image - {}", e.getMessage());
			}
		}
	}

	public void deleteItem() {
		if ((this.currentItem != null)) {
			int index = this.paneSave.getPane().getChildren().indexOf(getClassOfObject(currentItem.getType()).cast(currentItem.getItem()));
			if (index != -1) {
				this.paneSave.getPane().getChildren().remove(index);
				this.paneSave.remove(index);
			} else {
				logger.error("Item: {} error", this.currentItem.getType());
			}
		}
	}

	public void removeBorder(Item item) {
		if (item != null) {
			getClassOfObject(item.getType()).cast(item.getItem()).setStyle("");
			getClassOfObject(item.getType()).cast(item.getItem()).setEffect(null);
		}
	}

	public void loadInfo(Node on) {
		ObservableList<Info2Col> row = FXCollections.observableArrayList();
		String style = "-fx-border-color: black;-fx-border-style: dashed;";
		int i = paneSave.getPane().getChildren().indexOf(on);

		removeBorder(currentItem);

		Item obj = currentItem = paneSave.getItem(i);

		nameObjTemplate.setText(TemplateInfo.ItemsTemplate.get(obj.getId())[1]);
		infoParameterTable.getItems().clear();
		colNameTable.setCellValueFactory(new PropertyValueFactory<>(TextBox.info2col[0][2]));
		colValueTable.setCellValueFactory(new PropertyValueFactory<>(TextBox.info2col[1][2]));

		getClassOfObject(obj.getType()).cast(obj.getItem()).setStyle(style);
		getClassOfObject(obj.getType()).cast(obj.getItem()).setEffect(dropShadow);

		switch (obj.getType()) {
			case "barcode": {
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(0),
						Double.toString(obj.getOptions().getWidthModule()),
						0));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(1),
						Double.toString(obj.getOptions().getHeight()),
						1));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(2),
						Double.toString(obj.getOptions().getFont().size),
						2));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(3),
						Double.toString(obj.getOptions().getQuietZone()),
						3));
				break;
			}
			case "qrcode": {
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(6),
						Double.toString(obj.getOptions().getWidth()),
						6));
				break;
			}
			case "rectangle": {
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(6),
						Double.toString(obj.getOptions().getWidth()),
						6));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(7),
						Double.toString(obj.getOptions().getHeight()),
						7));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(12),
						Double.toString(obj.getOptions().getBorderWidth()), 12));
				break;
			}
			case "line": {
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(14),
						Double.toString(obj.getOptions().getWidth()),
						6));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(13),
						Double.toString(obj.getOptions().getRotate()),
						13));
				row.add(new Info2Col(TemplateInfo.parametersItem.get(12),
						Double.toString(obj.getOptions().getBorderWidth()),
						12));
				break;
			}
			case "freeText":
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(5),
						((Label) obj.getItem()).getText(),
						5));
			default:
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(6),
						Double.toString(obj.getOptions().getWidth()),
						6));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(7),
						Double.toString(obj.getOptions().getHeight()),
						7));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(4),
						obj.getOptions().getFont().fontWeight,
						4));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(2),
						Double.toString(obj.getOptions().getFont().size),
						2));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(8),
						obj.getOptions().getAlignment(),
						8));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(9),
						obj.getOptions().getWrapText(),
						9));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(10),
						obj.getOptions().getLineSpacingString(),
						10));
				row.add(new Info2Col(
						TemplateInfo.parametersItem.get(11),
						obj.getOptions().getIndentString(),
						11));
		}

		row.add(new Info2Col(
				TemplateInfo.parametersItem.get(15),
				obj.getPosition().x.toString(),
				14
		));
		row.add(new Info2Col(
				TemplateInfo.parametersItem.get(16),
				obj.getPosition().y.toString(),
				15
		));

		infoParameterTable.getItems().setAll(row);
	}

	public Label createViewLabel(FontItem font, String str, AnchorPane pane) {
		Label label = new Label(str);
		label.setFont(font != null ? Font.font(font.name, FontWeight.findByName(font.fontWeight), font.size) : new Font("System", 26));

		label.setOnMouseClicked(event -> {
			this.loadInfo(label);
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		pane.setOnDragExited(event -> {
			if (this.currentItem != null && this.currentItem.getItem() == label) {
				this.currentItem = null;
			}
		});
		label.setOnMousePressed(event -> {
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(label));
			obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		label.setOnMouseDragged(event -> {
			event.setDragDetect(false);
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(label));
			if ((this.currentItem != null)) {
				double maxX = this.paneSave.getPane().getWidth();
				double maxY = this.paneSave.getPane().getHeight();
				if (obj.getLastposition() == null) {
					obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				}
				Point mouse = new Point(event.getSceneX(), event.getSceneY());
				double dx = event.getSceneX() - obj.getLastposition().x;
				double dy = event.getSceneY() - obj.getLastposition().y;

				if (maxX > (label.getTranslateX() + obj.getOptions().getWidth() + dx)) {
					label.setTranslateX(label.getTranslateX() + dx);
				} else {
					label.setTranslateX(maxX - obj.getOptions().getWidth());
				}
				if (maxY > (label.getTranslateY() + obj.getOptions().getHeight() + dy)) {
					label.setTranslateY(label.getTranslateY() + dy);
				} else {
					label.setTranslateY(maxY - obj.getOptions().getHeight());
				}
				if (label.getTranslateX() < 0) {
					label.setTranslateX(0);
				}
				if (label.getTranslateY() < 0) {
					label.setTranslateY(0);
				}
				obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				if (obj.getOptions().getWidth() != label.getWidth()
						|| obj.getOptions().getHeight() != label.getHeight()) {
					obj.getOptions().setWidth(label.getWidth());
					obj.getOptions().setHeight(label.getHeight());
				}
				obj.setPosition(new Point(label.getTranslateX(), label.getTranslateY()));
				event.setDragDetect(true);
			} else {
				logger.info("Exit");
			}
			event.consume();
		});
		label.setLayoutX(0);
		label.setLayoutY(0);
		label.setTranslateX(10);
		label.setTranslateY(50);
		return label;
	}

	public Pane createViewBarcode(OptionsItem optionsItem, AnchorPane pane) {
		Code controlCode = new Code(new Codes(true), 12345, "15.506");
		BufferedImage img = null;
		if (optionsItem != null) {
			controlCode.setBarHeight(optionsItem.getHeight());
			controlCode.setDoQuietZone(optionsItem.getQuietZone() > 0.0);
			controlCode.setFontSize(optionsItem.getFont().size);
			controlCode.setModuleWidth(optionsItem.getWidthModule());
			controlCode.setMsgPosition(optionsItem.getHumanReadablePlacement());
			controlCode.setQuietZone(optionsItem.getQuietZone());
			img = controlCode.generate(false);
		} else {
			img = controlCode.generate(true);
		}
		Image image = SwingFXUtils.toFXImage(img, null);
		Pane label = new Pane();
		label.setPrefSize(image.getWidth(), image.getHeight());
		label.setBackground(
				new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
						BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(),
						BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
		label.setOnMouseClicked(event -> {
			this.loadInfo(label);
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		pane.setOnDragExited(event -> {
			if (this.currentItem != null && this.currentItem.getItem() == label) {
				this.currentItem = null;
			}
		});
		label.setOnMousePressed(event -> {
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(label));
			obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		label.setOnMouseDragged(event -> {
			event.setDragDetect(false);
			// Node on = (Node)event.getTarget();
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(label));
			if ((this.currentItem != null)) {
				double maxX = this.paneSave.getPane().getWidth();
				double maxY = this.paneSave.getPane().getHeight();
				if (obj.getLastposition() == null) {
					obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				}
				Point mouse = new Point(event.getSceneX(), event.getSceneY());
				double dx = event.getSceneX() - obj.getLastposition().x;
				double dy = event.getSceneY() - obj.getLastposition().y;

				if (maxX > label.getTranslateX() + obj.getOptions().getWidth() + dx) {
					label.setTranslateX(label.getTranslateX() + dx);
				} else {
					label.setTranslateX(maxX - obj.getOptions().getWidth());
				}
				if (maxY > label.getTranslateY() + obj.getOptions().getHeight() + dy) {
					label.setTranslateY(label.getTranslateY() + dy);
				} else {
					label.setTranslateY(maxY - obj.getOptions().getHeight());
				}
				if (label.getTranslateX() < 0) {
					label.setTranslateX(0);
				}
				if (label.getTranslateY() < 0) {
					label.setTranslateY(0);
				}
				obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				obj.setPosition(new Point(label.getTranslateX(), label.getTranslateY()));
				event.setDragDetect(true);
			} else {
				logger.info("Exit");
			}
			event.consume();
		});
		label.setLayoutX(0);
		label.setLayoutY(0);
		label.setTranslateX(10);
		label.setTranslateY(50);
		return label;
	}

	public Rectangle createViewRectangle(OptionsItem optionsItem, AnchorPane pane) {
		Rectangle rectangle = new Rectangle(50.0, 50.0);
		rectangle.setStroke(Color.BLACK);
		rectangle.setFill(Color.rgb(255, 255, 255, 0.0));
		if (optionsItem != null) {
			rectangle.setWidth(optionsItem.getWidth());
			rectangle.setHeight(optionsItem.getHeight());
			rectangle.setStrokeWidth(optionsItem.getBorderWidth());
		} else {
			rectangle.setStrokeWidth(1.0);
		}
		rectangle.setOnMouseClicked(event -> {
			this.loadInfo(rectangle);
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		pane.setOnDragExited(event -> {
			if (this.currentItem != null && this.currentItem.getItem() == rectangle) {
				this.currentItem = null;
			}
		});
		rectangle.setOnMousePressed(event -> {
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(rectangle));
			obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
		});
		rectangle.setOnMouseDragged(event -> {
			event.setDragDetect(false);
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(rectangle));
			if ((this.currentItem != null)) {

				double maxX = this.paneSave.getPane().getWidth();
				double maxY = this.paneSave.getPane().getHeight();
				if (obj.getLastposition() == null) {
					obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				}
				Point mouse = new Point(event.getSceneX(), event.getSceneY());
				double dx = event.getSceneX() - obj.getLastposition().x;
				double dy = event.getSceneY() - obj.getLastposition().y;

				if (maxX > rectangle.getTranslateX() + obj.getOptions().getWidth() + dx) {
					rectangle.setTranslateX(rectangle.getTranslateX() + dx);
				} else {
					rectangle.setTranslateX(maxX - obj.getOptions().getWidth());
				}
				if (maxY > rectangle.getTranslateY() + obj.getOptions().getHeight() + dy) {
					rectangle.setTranslateY(rectangle.getTranslateY() + dy);
				} else {
					rectangle.setTranslateY(maxY - obj.getOptions().getHeight());
				}
				if (rectangle.getTranslateX() < 0) {
					rectangle.setTranslateX(0);
				}
				if (rectangle.getTranslateY() < 0) {
					rectangle.setTranslateY(0);
				}
				obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				obj.setPosition(new Point(rectangle.getTranslateX(), rectangle.getTranslateY()));
				event.setDragDetect(true);
			} else {
				logger.info("Exit");
			}
			event.consume();
		});
		rectangle.setLayoutX(0);
		rectangle.setLayoutY(0);
		rectangle.setTranslateX(10);
		rectangle.setTranslateY(50);
		return rectangle;
	}

	public Line createViewLine(OptionsItem optionsItem, AnchorPane pane) {
		Line line = new Line(0.0, 0.0, 0.0, 0.0);
		if (optionsItem != null) {
			line.setStartX(optionsItem.getWidth());
			line.setRotate(optionsItem.getRotate());
			line.setStrokeWidth(optionsItem.getBorderWidth());
		} else {
			line.setStartX(20);
		}
		line.setOnMouseClicked(event -> {
			this.loadInfo(line);
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		pane.setOnDragExited(event -> {
			System.out.println("OnDragExited");
			if (this.currentItem != null && this.currentItem.getItem() == line) {
				this.currentItem = null;
			}
		});
		line.setOnMousePressed(event -> {
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(line));
			obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		line.setOnMouseDragged(event -> {
			event.setDragDetect(false);
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(line));
			if (this.currentItem != null) {
				double maxX = this.paneSave.getPane().getWidth();
				double maxY = this.paneSave.getPane().getHeight();

				if (obj.getLastposition() == null) {
					obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				}

				Point mouse = new Point(event.getSceneX(), event.getSceneY());
				double dx = event.getSceneX() - obj.getLastposition().x;
				double dy = event.getSceneY() - obj.getLastposition().y;

				if (maxX > line.getTranslateX() + obj.getOptions().getWidth() + dx) {
					line.setTranslateX(line.getTranslateX() + dx);
				} else {
					line.setTranslateX(maxX - obj.getOptions().getWidth());
				}
				if (maxY > line.getTranslateY() + obj.getOptions().getHeight() + dy) {
					line.setTranslateY(line.getTranslateY() + dy);
				} else {
					line.setTranslateY(maxY - obj.getOptions().getHeight());
				}
				if (line.getTranslateX() < 0) {
					line.setTranslateX(0);
				}
				if (line.getTranslateY() < 0) {
					line.setTranslateY(0);
				}
				obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				obj.setPosition(new Point(line.getTranslateX(), line.getTranslateY()));
				event.setDragDetect(true);
			} else {
				logger.info("Exit");
			}
			event.consume();
		});
		line.setLayoutX(0);
		line.setLayoutY(0);
		line.setTranslateX(10);
		line.setTranslateY(50);
		return line;
	}

	public Pane createViewQRCode(OptionsItem oitem, AnchorPane pane) throws WriterException {
		QRCode controlCode = new QRCode();
		BufferedImage img = null;
		if (oitem != null) {
			controlCode.setSize(oitem.getWidth().intValue());
		}
		img = controlCode.createQRImage();
		Image image = SwingFXUtils.toFXImage(img, null);
		Pane label = new Pane();
		label.setPrefSize(image.getWidth(), image.getHeight());
		label.setBackground(
				new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
						BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(),
						BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
		label.setOnMouseClicked(event -> {
			this.loadInfo(label);
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		pane.setOnDragExited(event -> {
			if (this.currentItem != null && this.currentItem.getItem() == label) {
				this.currentItem = null;
			}
		});
		label.setOnMousePressed(event -> {
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(label));
			obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
			pointMove = "(" + event.getSceneX() + ";" + event.getSceneY() + ")";
		});
		label.setOnMouseDragged(event -> {
			event.setDragDetect(false);
			Item obj = this.paneSave.getItem(this.paneSave.getPane().getChildren().indexOf(label));
			if ((this.currentItem != null)) {

				double maxX = this.paneSave.getPane().getWidth();
				double maxY = this.paneSave.getPane().getHeight();
				if (obj.getLastposition() == null) {
					obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				}
				Point mouse = new Point(event.getSceneX(), event.getSceneY());
				double dx = event.getSceneX() - obj.getLastposition().x;
				double dy = event.getSceneY() - obj.getLastposition().y;

				if (maxX > label.getTranslateX() + obj.getOptions().getWidth() + dx) {
					label.setTranslateX(label.getTranslateX() + dx);
				} else {
					label.setTranslateX(maxX - obj.getOptions().getWidth());
				}
				if (maxY > label.getTranslateY() + obj.getOptions().getHeight() + dy) {
					label.setTranslateY(label.getTranslateY() + dy);
				} else {
					label.setTranslateY(maxY - obj.getOptions().getHeight());
				}
				if (label.getTranslateX() < 0) {
					label.setTranslateX(0);
				}
				if (label.getTranslateY() < 0) {
					label.setTranslateY(0);
				}
				obj.setLastposition(new Point(event.getSceneX(), event.getSceneY()));
				obj.setPosition(new Point(label.getTranslateX(), label.getTranslateY()));
				event.setDragDetect(true);
			} else {
				logger.info("Exit");
			}
			event.consume();
		});
		label.setLayoutX(0);
		label.setLayoutY(0);
		label.setTranslateX(10);
		label.setTranslateY(50);
		return label;
	}

	public void load() {
		ObservableList<Info2Col> arr = FXCollections.observableArrayList();
		colNameInfoTable.setCellValueFactory(new PropertyValueFactory<>(TextBox.info2col[0][2]));
		colValueInfoTable.setCellValueFactory(new PropertyValueFactory<>(TextBox.info2col[1][2]));
		arr.add(new Info2Col(TemplateInfo.templateInfo.get(0), paneSave.getId() + "", 0));
		arr.add(new Info2Col(TemplateInfo.templateInfo.get(1), paneSave.getName(), 1));
		arr.add(new Info2Col(TemplateInfo.templateInfo.get(2), paneSave.getWidth() + "", 2));
		arr.add(new Info2Col(TemplateInfo.templateInfo.get(3), paneSave.getHeight() + "", 3));
		infoTable.setItems(arr);
	}

	@FXML
	void initialize() {
		stage.setOnCloseRequest(event -> messages.clear());
		save.setOnAction(event -> {
			boolean newItem = false;
			objTemplate.setId(0);
			objTemplate.setName(paneSave.getName());
			objTemplate.writeObjBlob(paneSave, db);
			if (paneSave.getId() != objTemplate.getId()) {
				if (Templates.get(paneSave.getId(), "", false, db) == null) {
					objTemplate.updateId(paneSave.getId(), db);
				}
				newItem = true;
			}
			paneSave.setId(0);
			paneSave.setName("");
			close();

			MainWindowCtrl.setLog(Helper.formatOutput(newItem ? Operation.CREATE : Operation.UPDATE, placeType,
					ipAddress, SectionType.TEMPLATE, objTemplate.getName(),
					LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), OperationStatus.SUCCESS));
		});
		testPrint.setOnAction(event -> {
			/*
			 * try { ObservableList<Goods> arr = PLUInfo.get(false, 0, "", 0, 0, 0,
			 * mainWindow.getDb()); PLUInfo plu = (arr.size()>0)?arr.get(0):new PLUInfo();
			 * TemplateCtrl tmp = new TemplateCtrl(0, paneSave.getName());
			 * tmp.setTemplate(paneSave); plu.setTemplate(tmp); PrintTemplate printT = new
			 * PrintTemplate(null,plu); printT.useCount = false;
			 * printT.Print(mainWindow.getScale(), plu, mainWindow, (3*plu.getPrice())+"");
			 * }catch (Exception e) {
			 * System.out.println("Editor->TestPrint: "+e.getMessage()); }
			 */
		});
		deleteObj.setOnAction(event -> deleteItem());
		addBackground.setOnAction(event -> addImage());
		delBackground.setOnAction(event -> {
			this.objTemplate.setBackground_data(null);
			template.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		});
		Callback<TableColumn<Info2Col, String>, TableCell<Info2Col, String>> cellFactory = p -> new EditingCell();
		colValueTable.setCellFactory(cellFactory);
		colValueTable.setOnEditCommit(value -> {
			Info2Col item = value
					.getTableView()
					.getItems()
					.get(value.getTablePosition().getRow());
			item.setValue(value.getNewValue());

			switch (item.getType()) {
				case 0: {
					Code controlCode = new Code("15.506");
					controlCode.setBarHeight(currentItem.getOptions().getHeight());
					controlCode.setDoQuietZone(currentItem.getOptions().getQuietZone() > 0.0);
					controlCode.setFontSize(currentItem.getOptions().getFont().size);
					controlCode.setModuleWidth(Double.parseDouble(item.getValue()));
					currentItem.getOptions().setWidthModule(Double.parseDouble(item.getValue()));
					controlCode.setMsgPosition(currentItem.getOptions().getHumanReadablePlacement());
					controlCode.setQuietZone(currentItem.getOptions().getQuietZone());
					BufferedImage img = controlCode.generate(false);
					createImage(img);
					break;
				}
				case 1: {
					Code controlCode = new Code("15.506");
					controlCode.setBarHeight(Double.parseDouble(item.getValue()));
					currentItem.getOptions().setHeight(Double.parseDouble(item.getValue()));
					controlCode.setDoQuietZone(currentItem.getOptions().getQuietZone() > 0.0);
					controlCode.setFontSize(currentItem.getOptions().getFont().size);
					controlCode.setModuleWidth(currentItem.getOptions().getWidthModule());
					controlCode.setMsgPosition(currentItem.getOptions().getHumanReadablePlacement());
					controlCode.setQuietZone(currentItem.getOptions().getQuietZone());
					BufferedImage img = controlCode.generate(false);
					createImage(img);
					break;
				}
				case 2: {
					if ("barcode".equals(currentItem.getType())) {
						Code controlCode = new Code("15.506");
						controlCode.setBarHeight(currentItem.getOptions().getHeight());
						controlCode.setDoQuietZone(currentItem.getOptions().getQuietZone() > 0.0);
						controlCode.setFontSize(Double.parseDouble(item.getValue()));
						currentItem.getOptions().getFont().size = Double.parseDouble(item.getValue());
						controlCode.setModuleWidth(currentItem.getOptions().getWidthModule());
						controlCode.setMsgPosition(currentItem.getOptions().getHumanReadablePlacement());
						controlCode.setQuietZone(currentItem.getOptions().getQuietZone());
						BufferedImage img = controlCode.generate(false);
						createImage(img);
					} else {
						currentItem.getOptions().getFont().size = Double.parseDouble(item.getValue());
						((Label) currentItem.getItem()).setFont(new Font(currentItem.getOptions().getFont().name,
								currentItem.getOptions().getFont().size));// if(currentItem.getType().compareToIgnoreCase(TextBox.nameObjTemplate[2])!=0)
					}
					break;
				}
				case 3: {
					Code controlCode = new Code("15.506");
					controlCode.setBarHeight(currentItem.getOptions().getHeight());
					controlCode.setDoQuietZone(currentItem.getOptions().getQuietZone() > 0.0);
					controlCode.setFontSize(currentItem.getOptions().getFont().size);
					controlCode.setModuleWidth(currentItem.getOptions().getWidthModule());
					controlCode.setMsgPosition(currentItem.getOptions().getHumanReadablePlacement());
					controlCode.setQuietZone(Double.parseDouble(item.getValue()));
					currentItem.getOptions().setQuietZone(Double.parseDouble(item.getValue()));
					BufferedImage img = controlCode.generate(false);
					createImage(img);
					break;
				}
				case 4: {
					currentItem.getOptions().getFont().fontWeight = item.getValue();
					((Label) currentItem.getItem()).setFont(Font.font(currentItem.getOptions().getFont().name,
							FontWeight.findByName(item.getValue()), currentItem.getOptions().getFont().size));
					break;
				}
				case 5: {
					((Label) currentItem.getItem()).setText(item.getValue());
					break;
				}
				case 6: {
					try {
						double w = Double.parseDouble(item.getValue());
						currentItem.getOptions().setWidth(w);
						switch (currentItem.getType()) {
							case "qrcode": {
								QRCode controlCode = new QRCode();
								controlCode.setSize((int) w);
								BufferedImage img = controlCode.createQRImage();
								createImage(img);
								break;
							}
							case "line": {
								((Line) currentItem.getItem()).setStartX(w);
								break;
							}
							case "rectangle": {
								((Rectangle) currentItem.getItem()).setWidth(w);
								break;
							}
							default: {
								((Label) currentItem.getItem()).setPrefWidth(w);
								break;
							}
						}
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getWidth() + "");
					}
					break;
				}
				case 7: {
					try {
						double h = Double.parseDouble(item.getValue());
						currentItem.getOptions().setHeight(h);
						if ("rectangle".equals(currentItem.getType())) {
							((Rectangle) currentItem.getItem()).setHeight(h);
						}
						((Label) currentItem.getItem()).setPrefHeight(h);
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getHeight() + "");
					}
					break;
				}
				case 8: {
					try {
						Pos p = Pos.valueOf(item.getValue());
						currentItem.getOptions().setAlignment(p.name());
						((Label) currentItem.getItem()).setAlignment(p);
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getAlignment());
					}
					break;
				}
				case 9: {
					try {
						String s = item.getValue();
						boolean b = s.compareToIgnoreCase("true") == 0;// Boolean.getBoolean(s);
						if (b) {
							currentItem.getOptions().setWrapText(item.getValue());
							((Label) currentItem.getItem()).setWrapText(b);
						} else {
							item.setValue(currentItem.getOptions().getWrapText());
						}
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getWrapText());
					}
					break;
				}
				case 10: {
					try {
						double val = Double.parseDouble(item.getValue());
						currentItem.getOptions().setLineSpacing(val);
						((Label) currentItem.getItem()).setLineSpacing(val);
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getLineSpacingString());
					}
					break;
				}
				case 11: {
					try {
						currentItem.getOptions().setIndent(item.getValue());
						Double[] val = currentItem.getOptions().getIndent();
						((Label) currentItem.getItem()).setPadding(new Insets(val[0], val[1], val[2], val[3]));
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getIndentString());
					}
					break;
				}
				case 12: {
					try {
						double val = Double.parseDouble(item.getValue());
						currentItem.getOptions().setBorderWidth(val);
						switch (currentItem.getType()) {
							case "line": {
								((Line) currentItem.getItem()).setStrokeWidth(val);
								break;
							}
							case "rectangle": {
								((Rectangle) currentItem.getItem()).setStrokeWidth(val);
								break;
							}
							default:
								break;
						}
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getBorderWidth() + "");
					}
					break;
				}
				case 13: {
					try {
						double val = Double.parseDouble(item.getValue());
						currentItem.getOptions().setRotate(val);
						if ("line".equals(currentItem.getType())) {
							((Line) currentItem.getItem()).setRotate(val);
						} else {
							((Label) currentItem.getItem()).setRotate(val);
						}
					} catch (Exception e) {
						item.setValue(currentItem.getOptions().getWidth() + "");
					}
					break;
				}
				case 14: {
					double x = Double.parseDouble(item.getValue());
					if (x > paneSave.getPane().getWidth()) {
						x = paneSave.getPane().getWidth() - currentItem.getOptions().getWidth();
					} else if (x < 0.0) {
						x = 0.0;
					}

					getClassOfObject(currentItem.getType())
							.cast(currentItem.getItem())
							.setTranslateX(x);
					break;
				}
				case 15: {
					double y = Double.parseDouble(item.getValue());

					if (y > paneSave.getPane().getHeight()) {
						y = paneSave.getPane().getHeight() - currentItem.getOptions().getHeight();
					} else if (y < 0.0) {
						y = 0.0;
					}

					getClassOfObject(currentItem.getType())
							.cast(currentItem.getItem())
							.setTranslateY(y);
					break;
				}
			}
		});
		listElement.setItems(ItemTemplate.getList(TemplateInfo.ItemsTemplate));

		//set cursor to open hand as user hovers over list of template elements
		listElement.setOnMouseMoved(event -> {
			stage.getScene().setCursor(Cursor.OPEN_HAND);
			event.consume();
		});
		//set cursor back to normal as user stops hovering over list of template elements
		listElement.setOnMouseExited(event -> {
			stage.getScene().setCursor(Cursor.DEFAULT);
			event.consume();
		});

		listElement.setOnMouseClicked(value -> {
			ItemTemplate selectedItem = listElement.getSelectionModel().getSelectedItem();
			stage.getScene().setCursor(Cursor.CLOSED_HAND);
			switch (selectedItem.getType()) {
				case "barcode": {
					Pane label = this.createViewBarcode(null, this.paneSave.getPane());
					this.paneSave.getPane().getChildren().add(label);
					Item it = new Item(selectedItem.getId(), selectedItem.getType(), label,
							new Point(label.getTranslateX(), label.getTranslateY()));
					it.getOptions().setHeight(label.getPrefHeight());
					it.getOptions().setWidth(label.getPrefWidth());
					it.getOptions().setFont(new FontItem("System", "NORMAL", 14.0));
					this.paneSave.setItem(it);
					logger.info("add: {}", selectedItem);
					loadInfo(label);
					break;
				}
				case "qrcode": {
					try {
						Pane label = this.createViewQRCode(null, this.paneSave.getPane());
						this.paneSave.getPane().getChildren().add(label);
						Item it = new Item(selectedItem.getId(), selectedItem.getType(), label,
								new Point(label.getTranslateX(), label.getTranslateY()));
						it.getOptions().setHeight(label.getPrefHeight());
						it.getOptions().setWidth(label.getPrefWidth());
						it.getOptions().setFont(new FontItem("System", "NORMAL", 14.0));
						this.paneSave.setItem(it);
						logger.info("add: {}", selectedItem);
						loadInfo(label);
					} catch (WriterException e) {
						e.printStackTrace();
					}
					break;
				}
				case "line": {
					Line line = this.createViewLine(null, this.paneSave.getPane());
					this.paneSave.getPane().getChildren().add(line);
					Item it = new Item(selectedItem.getId(), selectedItem.getType(), line,
							new Point(line.getTranslateX(), line.getTranslateY()));
					it.getOptions().setRotate(line.getRotate());
					it.getOptions().setWidth(line.getStartX());
					it.getOptions().setBorderWidth(line.getStrokeWidth());
					this.paneSave.setItem(it);
					logger.info("add: {}", selectedItem);
					loadInfo(line);
					break;
				}
				case "rectangle": {
					Rectangle rectangle = createViewRectangle(null, this.paneSave.getPane());
					this.paneSave.getPane().getChildren().add(rectangle);
					Item it = new Item(selectedItem.getId(), selectedItem.getType(), rectangle,
							new Point(rectangle.getTranslateX(), rectangle.getTranslateY()));
					it.getOptions().setHeight(rectangle.getHeight());
					it.getOptions().setWidth(rectangle.getWidth());
					it.getOptions().setBorderWidth(rectangle.getStrokeWidth());
					this.paneSave.setItem(it);
					logger.info("add: {}", selectedItem);
					loadInfo(rectangle);
					break;
				}
				default: {
					Label label = this.createViewLabel(null, selectedItem.getValue(), this.paneSave.getPane());
					this.paneSave.getPane().getChildren().add(label);
					label.setAlignment(Pos.valueOf("CENTER"));
					Item it = new Item(selectedItem.getId(), selectedItem.getType(), label,
							new Point(label.getTranslateX(), label.getTranslateY()));
					it.getOptions().setFont(new FontItem(label.getFont().getFamily(), label.getFont().getSize()));
					it.getOptions().setHeight(label.getHeight());
					if (Objects.equals(selectedItem.getType(), "namePLU")
							|| Objects.equals(selectedItem.getType(), "top")
							|| Objects.equals(selectedItem.getType(), "bottom")) {
						label.setPrefWidth(this.paneSave.getPane().getWidth());
						it.getOptions().setWidth(this.paneSave.getPane().getWidth());
					} else
						it.getOptions().setWidth(label.getWidth());
					this.paneSave.setItem(it);
					logger.info("add: {}", selectedItem);
					loadInfo(label);
				}
			}
			value.consume();
		});
		colValueInfoTable.setCellFactory(cellFactory);
		colValueInfoTable.setOnEditCommit(v -> {
			Info2Col item = v.getTableView().getItems().get(v.getTablePosition().getRow());
			switch (item.getType()) {
				case 0: {
					int n = 0;
					try {
						n = Integer.parseInt(v.getNewValue());
						if(Templates.get(n, "", false, MainCtrl.getDB())!=null)
							n = 0;
					} catch (Exception e) {
						n = -1;
					}
					if (n > 0) {
						paneSave.setId(n);
						item.setValue(v.getNewValue());
					}
					break;
				}
				case 1: {
					if (v.getNewValue().length() > 0) {
						Boolean f = true;
						String n = v.getNewValue();
						for(String value : Templates.getLName(MainCtrl.getDB())) {
							if(value.compareToIgnoreCase(n)==0) {
								f=false; break;
							}
						}
						if(f) {
							if(objTemplate.getName().length()>0)
								objTemplate.updateName(n, MainCtrl.getDB());
							paneSave.setName(n);
							item.setValue(n);
						}
					}
					break;
				}
				case 2: {
					double n = 0;
					try {
						n = Double.parseDouble(v.getNewValue()) * 8;
					} catch (Exception e) {
						n = -1;
					}
					if (n > 0) {
						logger.info(String.format("%f:%f", template.getMinWidth(), template.getMinHeight()));
						template.setMinWidth(n);
						template.setPrefWidth(n);
						paneSave.setWidthContent((float) n);
						item.setValue(v.getNewValue());
						logger.info(String.format("%f:%f", template.getPrefWidth(), template.getPrefHeight()));
					}
					break;
				}
				case 3: {
					double n = 0;
					try {
						n = Double.parseDouble(v.getNewValue()) * 8;
					} catch (Exception e) {
						n = -1;
					}
					if (n > 0) {
						logger.info(String.format("%f:%f", template.getMinWidth(), template.getMinHeight()));
						template.setMinHeight(n);
						template.setPrefHeight(n);
						paneSave.setHeightContent((float) n);
						item.setValue(v.getNewValue());
						logger.info(String.format("%f:%f", template.getPrefWidth(), template.getPrefHeight()));
					}
					break;
				}
			}
		});
	}

	private Class<? extends Node> getClassOfObject(String type) {
		switch (type) {
			case "barcode":
			case "qrcode":
				return Pane.class;
			case "rectangle":
				return Rectangle.class;
			case "line":
				return Line.class;
			default:
				return Label.class;
		}
	}

	private void createImage(BufferedImage img) {
		Image image = SwingFXUtils.toFXImage(img, null);
		((Pane) currentItem.getItem()).setPrefSize(image.getWidth(), image.getHeight());
		((Pane) currentItem.getItem()).setBackground(new Background(new BackgroundImage(image,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(BackgroundSize.DEFAULT.getWidth(),
						BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
	}

	public PaneObj getPaneSave() {
		return this.paneSave;
	}

	public void setCanvasSave(PaneObj paneSave) {
		this.paneSave = paneSave;
	}

	public Pane getEditPanel() {
		return editPanel;
	}

	public void setEditPanel(AnchorPane editPanel) {
		this.editPanel = editPanel;
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(Item currentItem) {
		this.currentItem = currentItem;
	}

	public Stage getStage() {
		return stage;
	}

	public AnchorPane getTemplate() {
		return template;
	}

	public void setTemplate(AnchorPane template) {
		this.template = template;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setPlaceType(PlaceType placeType) {
		this.placeType = placeType;
	}
}