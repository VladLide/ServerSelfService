package application.enums;

import application.models.net.database.mysql.interface_tables.ScaleItemMenu;

/**
 * Contains places to which items can be send
 */
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
