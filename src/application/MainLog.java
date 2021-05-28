package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainLog {
	private final File file;
	private static MainLog log;

	public MainLog(String pathToFile) {
		file = new File(pathToFile);

		try {
			if (file.exists()) {
				if (!file.delete()) throw new RuntimeException("Was not able to delete file at path: " + pathToFile);
			}
			if (!file.createNewFile())
				throw new RuntimeException("Was not able to create file at path: " + pathToFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
		}
	}

	public static MainLog getInstance() {
		if (log == null) log = new MainLog(System.getProperty("user.dir") + "\\log\\log.log");
		return log;
	}

	private static void log(String s) {
		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(MainLog.getInstance().file, true))) {
			writer.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logInfo(String s, String title) {
		log("INFO: class-"+ title + ", message-" + s);
	}

	public static void logDebug(String s, String title) {
		log("DEBUG: class-"+ title + ", message-" + s);
	}

	public static void logError(String s, String title) {
		log("ERROR: class-"+ title + ", message-" + s);
	}
}
