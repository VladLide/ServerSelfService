package application.models;

import java.lang.reflect.Field;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.CodeSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import application.Main;

import java.util.Date;

import javafx.scene.image.Image;

public class Utils {

	public Utils() {}

	public static String DIR_VIEW = "/application/views/";
	public static String DIR_RES = ((getPathCur().length()!=0)?getPathCur():"")+"/resources/";
	private static List<SimpleDateFormat> 
    dateFormats = new ArrayList<SimpleDateFormat>() {
		private static final long serialVersionUID = 1L; 
		{
		    add(new SimpleDateFormat("M/dd/yyyy"));
		    add(new SimpleDateFormat("dd.M.yyyy"));
		    add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
		    add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
		    add(new SimpleDateFormat("dd.MMM.yyyy"));
		    add(new SimpleDateFormat("dd-MMM-yyyy"));
		}
	};
	private static List<String> errors = new ArrayList<String>();
	public static Utils instance;
	static String pathLogs = ((getPathCur().length()!=0)?getPathCur():"");//Paths.get("/Temp/SoapUI/Logs/").toAbsolutePath().toString() + "/";

	public static Utils getInstance() {
		if (instance == null) {
			instance = new Utils();
		}
		return instance;
	}
	protected static Utils setErr( String s ) {
		errors.add(s);
		return getInstance();
	}
	public static String getErr() {
		int len = errors.size();
		return (len>0?errors.get(len-1):null);
	}
	protected static Utils putLog(String type,Object msg) {
		String className = msg.getClass().getName();
		String msgType = "info";
		String text = className+":";
		switch (type) {
		case"w":
			msgType = "warn";
		break;
		case"e":
			msgType = "error";
		break;
		}
		switch (className) {
			case "java.lang.String":
				text = (String) msg;
				break;
			default:
				try {
					Exception e = (Exception) msg;
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					text = sw.toString();
					msgType = "error";
				} catch (Exception e) {
					text += e.getMessage()+msg.toString();
				}
			break;
		}

		if(msgType == "error")
			setErr( text );
		
		msgType = "["+msgType+"]:";
		System.out.println( msgType+"{"+text+"}" );
		putFile(getPath("logs") + "Utils.log", msgType+"{"+text+"}", true);
		
		return getInstance();
	}
	protected static Utils putLog(Object msg) {
		return putLog("i", msg);
	}

	public String getLog() {
		return getFile(pathLogs + "Utils.log","windows-1251");
	}
    private static String removeUTF8BOM(String s) {
    	String UTF8_BOM = "\uFEFF";
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
	public static Object getFile(String path, String encoding, boolean istext ) {
		Object res = null;
		File f = new File(path);
		if (f.exists() && !f.isDirectory()) {
				BufferedReader d;
				try {
					d = new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding));
					String str=null;
					StringBuilder sb = new StringBuilder();
					String line; boolean flag = false,firstLine = true;
					while ((line = d.readLine()) != null) {
						if (firstLine&&encoding.compareToIgnoreCase("UTF8")==0) {
		                    line = removeUTF8BOM(line);
		                    firstLine = false;
		                }
						sb.append(flag? System.getProperty("line.separator"): "").append(line);
						flag = true;
					}
					sb.append(System.getProperty("line.separator"));
					str = sb.toString();
					d.close();
					byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
					res = ( istext?new String(bytes, StandardCharsets.UTF_8):bytes);
				} catch (UnsupportedEncodingException | FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			/*try {
				byte[] encoded;
				try {
					encoded = Files.readAllBytes(Paths.get(path));
					res = ( istext?new String(encoded, "utf-8"):encoded);
				} catch (IOException e) {
					putLog(e);
					// e.printStackTrace();
				}
			}catch(Exception e) {
				putLog(e);
			}*/ catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}else {
			putLog("w","File is not exist:"+path);
		}
		return res;
	}

	public static String getFile(String path, String encoding) {
		return (String) getFile( path, encoding, true );
	}
	
	public static Utils putFile(String path, Object data, boolean append) {
		if(data == null) {
			putLog("e","Trying write null data into: " + path);
			return getInstance();
		}
		try {
		     
		    Path path_ = Paths.get(path);
		  
		    //Files.write(path_, (byte[])data);
		    
			
			String className = data.getClass().getName();
			File f = new File(path);
			f.getParentFile().mkdirs();
			f.createNewFile();
			//System.out.println("append:"+append);
			if( className.contains("java.lang.String")) {
				//PrintWriter writer = (append ? new PrintWriter(new FileOutputStream(f, true)) : new PrintWriter(path, "UTF-8"));
				//System.out.println("putFile if:"+path_);
				//Files.write(path_, ( ((String) data)).getBytes(), ( append? StandardOpenOption.APPEND: StandardOpenOption.WRITE));

				
				//OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f));//, StandardCharsets.UTF_8
				BufferedWriter out = new BufferedWriter(
					    new OutputStreamWriter(
					        new FileOutputStream(path, append),  //true to append
					        StandardCharsets.UTF_8                  // Set encoding
					    )
					);
				out.write((String) data);
				out.close();
			}else {
				//System.out.println("putFile else:"+path_);
				Files.write(path_, (byte[])data, ( append? StandardOpenOption.APPEND: StandardOpenOption.WRITE));
			}
			
		} catch (Exception e) {
			//putLog(e);
			System.out.print(e);
		}
		return getInstance();
	}
	public static Utils writeFile(String path, Object data, boolean append) {
		if(data == null) {
			putLog("e","Trying write null data into: " + path);
			return getInstance();
		}
		try {
		     
		    Path path_ = Paths.get(path);
		  
		    //Files.write(path_, (byte[])data);
		    
			
			String className = data.getClass().getName();
			File f = new File(path);
			f.getParentFile().mkdirs();
			f.createNewFile();
	        
			if( className.contains("java.lang.String")) {
				PrintWriter out = new PrintWriter(f, "Cp1251");
	            try {
	                out.print((String) data);
	            } finally {
	                out.close();
	            }		
				/*OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append)));
				out.write((String) data);
				out.close();*/
			}else {
				//System.out.println("putFile else:"+path_);
				Files.write(path_, (byte[])data, ( append? StandardOpenOption.APPEND: StandardOpenOption.WRITE));
			}
			
		} catch (Exception e) {
			//putLog(e);
			System.out.print(e);
		}
		return getInstance();
	}

	public static Utils putFile(String path, Object data) {
		return putFile(path, data, false);
	}

	public static Date convertToDate(String input) {
        Date date = null;
        if(null == input) {
            return null;
        }
        for (SimpleDateFormat format : dateFormats) {
            try {
            	format.setLenient(false);
                date = format.parse(input);
            } catch (ParseException e) {
                //Shhh.. try other formats
            }
            if (date != null) {
                break;
            }
        }
 
        return date;
    }
	public static String getView(String dir, String file) {
		String res = DIR_VIEW;
		switch(dir) {
			case "css":
				res = DIR_VIEW+"css/";
			break;
			default:
				res += "fxml/"+dir+"s/"+file+".fxml";
		}
		if(!res.endsWith(".fxml")) {
			res+=file+"."+dir;
		}
		return res;
	}
	public static ResourceBundle getResource(String language, String dir, String file) {
		String res = DIR_VIEW.substring(1, DIR_VIEW.length()).replace("/", ".")+"languages."+language.toLowerCase()+"."+dir+"s."+file;
		String country = language.toUpperCase();
		switch (language) {
			case "uk":country = "UA";
			break;
			case "ru":country = "RU";
			break;
			case "en":country = "US";
			break;
			default:country = "UA";
			break;
		}
		return ResourceBundle.getBundle(res,new Locale(language,country));
	}
	public static ResourceBundle getResource(String language, String country, String dir, String file) {
		String res = DIR_VIEW.substring(1, DIR_VIEW.length()).replace("/", ".")+"languages."+language.toLowerCase()+"."+dir+"s."+file;
		return ResourceBundle.getBundle(res,new Locale(language,country));
	}
	public static String getPath(String dir, String file) {
		String res = DIR_RES;
		switch(dir) {
			case "img":
				res = DIR_RES+"images/";
			break;
			case "tpl":
				res = DIR_RES+"templates/";
			break;
			case "tplimg":
				res = DIR_RES+"templates/";
			break;
			case "sys":
				res = DIR_RES+"system/";
			break;
			case "tmp":
				res = DIR_RES+"temp/";
			break;
		}
		if(file!=null) {
			res += (file+"."+dir);
		}
		/*if(file!=null) {
			res = (dir!="img")?res + file + "."+dir:res + file;
		}*/
		out(res);
		return res;
	}
	public static String getPath(String dir, int id) {
		return getPath(dir,id + "");
	}
	public static String getPath(String dir) {
		return getPath(dir,null);
	}
	public static Image getImg(String dir, String name, double width, double height) {
		InputStream is;
		Image image = null;
		try {
			is = new FileInputStream(getPath(dir, name));
			image = ( width + height == 0? new Image(is) :new Image(is, width, height,false, true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
	public static Image getImg(String dir, int id) {
		return getImg(dir,id + "", 0, 0);
	}
	public static Image getImg(String dir, int id, double width, double height) {
		return getImg(dir,id + "", width, height);
	}
	public static Image getImg(String dir) {
		return getImg(dir,null,0,0);
	}
	public static void out(String str) {
		System.out.println(str + "");
		return;
	}
	public static void copyFile(String source, String dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(new File(source));
	        os = new FileOutputStream(new File(dest));
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	public static String getPathFile(File file) {
		String path = (file.getParentFile().getPath().replace("\\", "/"))+file.getName();
		System.out.println(path);
		return path;
	}
	public static String getPathCur() {
		CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
		File jarFile = null;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			return "";
		}
		return jarFile.getParentFile().getPath().replace("\\", "/");
	}
	public static String getType(Field f) {
		String type = f.getType().getTypeName().replace(".", " ");
		if(type.split(" ").length>0) {
			String[] typ = type.split(" ");
			type = typ[typ.length-1];
		}
		return type;
	}
	public static String getTypeObj(Object obj) {
		String[] type = obj.getClass().getTypeName().replace(".", " ").split(" ");
		return type[type.length-1];
	}
}
