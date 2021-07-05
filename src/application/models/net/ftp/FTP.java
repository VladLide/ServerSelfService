package application.models.net.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import application.models.Configs;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import application.models.Utils;

public class FTP extends Configs {
	private String server;
	private int port;
	private String user;
	private String password;
	private FTPClient ftp;
	private OutputStream logFile = null;
	private String log = "";

	public FTP(String host, String port, String user, String pass) throws IOException {
		super();
		this.server = host;
		this.port = Integer.parseInt(port);
		this.user = user;
		this.password = pass;

		startLogForFtp();
	}

	private void startLogForFtp() throws IOException {
		File folder = new File(Utils.getPath("log"));
		if (!folder.exists() && !folder.mkdir()) {
			throw new IOException("Was not able to create folder: " + folder.getAbsolutePath());
		}
		try {
			logFile = new FileOutputStream(Utils.getPath("log", "ftpLoad.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void open() throws IOException {
		ftp = new FTPClient();
		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(logFile)));
		ftp.connect(server, port);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new IOException("Exception in connecting to FTP Server");
		}
		ftp.login(user, password);
		ftp.enterLocalPassiveMode();
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
	}

	public void close() {
		try {
			ftp.disconnect();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	public Collection<String> listFiles(String path) throws IOException {
		FTPFile[] files = ftp.listFiles(path);
		return Arrays.stream(files)
				.map(FTPFile::getName)
				.collect(Collectors.toList());
	}

	public void downloadFile(String source, String destination) throws IOException {
		FileOutputStream out = new FileOutputStream(destination);
		ftp.retrieveFile(source, out);
		out.close();
	}

	public boolean deleteFile(String source) throws IOException {
		boolean delete = ftp.deleteFile(source);
		return delete;
	}

	public InputStream downloadFile(String source) throws IOException {
		return ftp.retrieveFileStream(source);
	}

	public void putFileToPath(File file, String path) throws IOException {
		ftp.storeFile(path, new FileInputStream(file));
	}

	public void writeLog() {
		try {
			logFile.write(log.getBytes());
		} catch (IOException e1) {
		}
	}

	public void addLog(String message) {
		log += message + System.lineSeparator();
	}

	public void closeLog() {
		try {
			logFile.flush();
			logFile.close();
		} catch (IOException e) {
		}
	}

	public FTPClient getFtp() {
		return ftp;
	}

	public void setFtp(FTPClient ftp) {
		this.ftp = ftp;
	}

	public OutputStream getLogFile() {
		return logFile;
	}

	public void setLogFile(OutputStream logFile) {
		this.logFile = logFile;
	}
}
