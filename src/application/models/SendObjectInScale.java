package application.models;

import application.controllers.windows.MainWindowCtrl;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Templates;
import application.views.languages.uk.parts.LogInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class SendObjectInScale extends Task<Void> {
    private PackageSend pack = null;
    private int MAX_WORK = 10;

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

        for (int i = 0; i < scaleItemMenus.size(); i++) {
            ScaleItemMenu scaleItemMenu = scaleItemMenus.get(i);

            String startText = pack.getType() + " > "
                    + scaleItemMenu.getName()
                    + " " + scaleItemMenu.getId()
                    + ": ";

            for (int j = 0; j < packItems.size(); j++) {
                Object packItem = packItems.get(j);

                if (isCancelled()) {
                    MainWindowCtrl.setLog(canceledMessage);
                    updateMessage(canceledMessage);
                    return;
                }

                String s = send(packItem, scaleItemMenu.getDB());

                if (s.length() > 0)
                    countSend++;

                String message = startText + s + " all:" + (i * scaleItemMenusSize + j + 1) + "/" + MAX_WORK;
                MainWindowCtrl.setLog(message);
                updateMessage(message);
                updateProgress((long) i * scaleItemMenusSize + j, MAX_WORK);

                try {
                    Thread.sleep(300);
                } catch (InterruptedException interrupted) {
                    if (isCancelled()) {
                        MainWindowCtrl.setLog(canceledMessage);
                        updateMessage(canceledMessage);
                        return;
                    }
                }
            }
        }
        updateMessage(LogInfo.sendObj[2] + " " + countSend + "/" + MAX_WORK);
    }

    private String send(Object obj, MySQL db) {
        switch (obj.getClass().getSimpleName()) {
            case "Templates": {
                Templates tmp = (Templates) obj;
                tmp.save(db);
                return tmp.getId() + " - " + tmp.getName();
            }
            case "templateCodes": {

                return "";
            }
            case "settings": {

                return "";
            }
            default:
                return "";
        }
    }
}
