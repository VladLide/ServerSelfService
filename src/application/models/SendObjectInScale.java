package application.models;

import java.util.Locale;

import application.controllers.windows.MainWindowCtrl;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Templates;
import application.views.languages.uk.parts.LogInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

public class SendObjectInScale extends Task<Void> {
	private final Logger logger = LogManager.getLogger(SendObjectInScale.class);
	private final PackageSend pack;
	private final int MAX_WORK;

	public SendObjectInScale(PackageSend pack) {
		this.pack = pack;
		MAX_WORK = pack.getItems().size() * pack.getConnectSend().size();
	}

	@Override
	public Void call() {
		process();
		return null;
	}

	private void process() {
		String startTransmissionMessage = LogInfo.sendObj[0];
		String canceledMessage = LogInfo.sendObj[1];
		MainWindowCtrl.setLog(startTransmissionMessage);
		updateMessage(startTransmissionMessage);

		ObservableList<ScaleItemMenu> scaleItemMenus = pack.getConnectSend();
		ObservableList<Object> packItems = pack.getItems();
		int countSend = 0;
		int scaleItemMenusSize = scaleItemMenus.size();

		try {
		for (int i = 0; i < scaleItemMenus.size(); i++) {
			ScaleItemMenu scaleItemMenu = scaleItemMenus.get(i);

				String startText = String.format("%s > %s %s: ",
						pack.getType(),
						scaleItemMenu.getName(),
						scaleItemMenu.getId());

			for (int j = 0; j < packItems.size(); j++) {
				Object packItem = packItems.get(j);

				if (isCancelled()) {
					MainWindowCtrl.setLog(canceledMessage);
					updateMessage(canceledMessage);
					return;
				}

				String s = send(pack.getCommand(), packItem, scaleItemMenu.getDB());

				if (s.length() > 0)
					countSend++;

					String message = String.format("%s %s all %d/%d",
							startText,
							s,
							(i * scaleItemMenusSize + j + 1),
							MAX_WORK);
				MainWindowCtrl.setLog(message);
				updateMessage(message);
					updateProgress((long) i * scaleItemMenusSize + j + 1, MAX_WORK);

					Thread.sleep(300);
				}
			}
		} catch (InterruptedException e) {
					if (isCancelled()) {
						MainWindowCtrl.setLog(canceledMessage);
						updateMessage(canceledMessage);
						return;
					}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			}

		String successMessage = LogInfo.sendObj[2] + " " + countSend + "/" + MAX_WORK;
		MainWindowCtrl.setLog(successMessage);
		updateMessage(successMessage);
	}

	private String send(int command, Object obj, MySQL db) {
		switch (obj.getClass().getSimpleName().toLowerCase(Locale.ROOT)) {
			case "goods": {
				Goods goods = (Goods) obj;
				if(command < 3) {
					goods.save(db);
				} else {
					goods.delete(db);
				}
				return goods.getId() + " - " + goods.getName();
			}
			case "templates": {
				Templates tmp = (Templates) obj;
				if(command < 3) {
					tmp.save(db);
				} else {
					tmp.delete(db);
				}
				return tmp.getId() + " - " + tmp.getName();
			}
			case "codes": {
				Codes tmp = (Codes) obj;
				if(command < 3) {
					tmp.save(db);
				} else {
					tmp.delete(db);
				}
				return tmp.getId() + " - " + tmp.getName();
			}
			case "sections": {
				Sections tmp = (Sections) obj;
				tmp.save(db);

				return tmp.getId() + " - " + tmp.getName();
			}
			case "settings": {
	
				return "";
			}
			default:
				return "";
		}
	}
}
