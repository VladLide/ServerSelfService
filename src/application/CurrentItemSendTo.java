package application;

import application.models.net.mysql.interface_tables.ScaleItemMenu;

public enum CurrentItemSendTo {
    SCALE,
    SERVER;

    private ScaleItemMenu scaleItemMenu;

    public ScaleItemMenu getScaleItemMenu() {
        return scaleItemMenu;
    }

    public void setScaleItemMenu(ScaleItemMenu scaleItemMenu) {
        this.scaleItemMenu = scaleItemMenu;
    }
}
