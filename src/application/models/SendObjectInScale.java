package application.models;

import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Templates;
import application.views.languages.uk.parts.LogInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class SendObjectInScale extends Task<Void>{
	private PackageSend pack = null;
    private int MAX_WORK = 10;
    
	public SendObjectInScale(PackageSend pack) {
		this.pack = pack;
		MAX_WORK = pack.getItems().size()*pack.getConnectSend().size();
	}
	
	@Override
    public Void call() {
		process();
        return null;
    }
	private void process() {
        updateMessage(LogInfo.sendObj[0]);
        ObservableList<ScaleItemMenu> c = pack.getConnectSend();
        ObservableList<Object> v = pack.getItems();
        int countSend = 0;
        for(int i = 0; i < c.size(); i++) {
        	String startText = pack.getType()+" > "+c.get(i).getName()+" "+c.get(i).getId()+": ";
	        for (int j = 0; j < v.size(); j++) {
	            if (isCancelled()) {
	                updateMessage(LogInfo.sendObj[1]);
	                return;
	            }
	            String s = send(v.get(j),c.get(i).getDB());
	            if(s.length()>0)countSend++;
	            updateMessage(startText+/*"("+i+","+j+")*/s+" all:"+(i*c.size()+j+1)+"/"+MAX_WORK);
	            updateProgress((i*c.size()+j), MAX_WORK);
	
	            try {
	                Thread.sleep(300);
	            } catch (InterruptedException interrupted) {
	                if (isCancelled()) {
	                    updateMessage(LogInfo.sendObj[1]);
	                    return;
	                }
	            }
	        }
        }
        updateMessage(LogInfo.sendObj[2]+" "+countSend+"/"+MAX_WORK);
    }
    protected void updateMessage(String message) {
        System.out.println(message);
        super.updateMessage(message);
    }
	private String send(Object obj, MySQL db) {
		System.out.println(obj.getClass().getSimpleName());
		switch (obj.getClass().getSimpleName()) {
			case "Templates":{
				Templates tmp = (Templates)obj;
				tmp.save(db);
				return tmp.getId()+" - "+tmp.getName();
			}
			case "templateCodes":{
				
				return "";
			}
			case "settings":{
				
				return "";
			}
			default:
				return "";
		}
    }
}
