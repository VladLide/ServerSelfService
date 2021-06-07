package application;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Marquee extends Text {
	private AnchorPane pnlMain = null;
	private HBox pnlHBMain = null;
	private TranslateTransition marqueeTT;
	private int speedSec = 18;
	private boolean hasPausedME = false;
	private boolean hasPlayedME = false;
	private final List<Thread> threads = new ArrayList<>();

	public Marquee() {
		this("");
	}

	public Marquee(String text) {
		this.setStyle("-fx-font: bold 20 arial;");
		this.setVisible(false);
		this.setTranslateY(this.maxHeight(0));
		this.setText(text);
	}

	public void setScrollDuration(int seconds) {
		this.speedSec = seconds;
	}

	public void play() {
		marqueeTT.play();
	}

	public void pause() {
		marqueeTT.pause();
	}

	public void stop() {
		marqueeTT.stop();
		this.setText("");
		threads.forEach(thread -> {
					if (!thread.isInterrupted()) {
						thread.interrupt();
					}
				}
		);
	}

	public void setBoundsFrom(AnchorPane pnl) {
		this.pnlMain = pnl;
	}

	public void setBoundsFrom(HBox pnl) {
		this.pnlHBMain = pnl;
	}

	public void moveDownBy(int amount) {
		this.setTranslateY(this.maxHeight(0) + amount);
	}

	public void setColor(String color) {
		this.setFill(Paint.valueOf(color));
	}

	public void run() {
		marqueeTT = new TranslateTransition(Duration.seconds(this.speedSec), this);
		marqueeTT.setOnFinished(actionEvent -> reRunMarquee());

		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(700);
			} catch (InterruptedException ex) {
			}
			runMarquee();
		});
		thread.start();

		threads.add(thread);
	}

	private boolean hasPausedME() {
		return hasPausedME;
	}

	private boolean hasPlayedME() {
		return hasPlayedME;
	}

	private void setHasPausedME(boolean state) {
		hasPausedME = state;
	}

	private void setHasPlayedME(boolean state) {
		hasPlayedME = state;
	}


	private void runMarquee() {
		this.setOnMouseEntered(t -> {
			if (hasPausedME()) {
				marqueeTT.pause();
			} else {
				setHasPausedME(true);
				Thread thread = new Thread(() -> {
					try {
						Thread.sleep(180);
					} catch (InterruptedException ex) {
					}
					if (!hasPlayedME()) marqueeTT.pause();
				});
				thread.start();
			}
		});

		this.setOnMouseExited(t -> {
			marqueeTT.play();
			setHasPlayedME(true);
		});
		reRunMarquee();
	}

	private void reRunMarquee() {
		marqueeTT.setDuration(Duration.seconds(this.speedSec));
		marqueeTT.setInterpolator(Interpolator.LINEAR);
		marqueeTT.stop();
		double toX = -(this.maxWidth(0) + 50);
		marqueeTT.setToX(toX);

		double fromX = this.pnlMain != null ?
				this.pnlMain.getWidth() : this.pnlHBMain.getWidth();

		marqueeTT.setFromX(fromX);

		marqueeTT.playFromStart();
		this.setVisible(true);
	}
}
