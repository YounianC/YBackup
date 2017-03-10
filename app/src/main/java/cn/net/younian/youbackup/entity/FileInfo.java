package cn.net.younian.youbackup.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cn.net.younian.youbackup.util.Constants;

public class FileInfo {

    private String path;
    private String name;
    private boolean checked;

    private int countContacts;
    private int countSMS;
    private int countCallLog;


    public FileInfo(String path, String name, boolean checked) {
        super();
        this.path = path + File.separator + name + File.separator;
        this.name = name;
        this.checked = checked;

        File urlFile = new File(this.path + Constants.File_Info);
        if (urlFile.exists()) {
            InputStreamReader isr = null;
            String str = "";
            try {
                isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String mimeTypeLine = null;
                while ((mimeTypeLine = br.readLine()) != null) {
                    str = str + mimeTypeLine;
                }
                JSONObject infoObject = new JSONObject(str);
                this.countContacts = infoObject.getInt(Constants.Info_CountContacts);
                this.countSMS = infoObject.getInt(Constants.Info_CountSMS);
                this.countCallLog = infoObject.getInt(Constants.Info_CountCallLog);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCountCallLog() {
        return countCallLog;
    }

    public void setCountCallLog(int countCallLog) {
        this.countCallLog = countCallLog;
    }

    public int getCountContacts() {
        return countContacts;
    }

    public void setCountContacts(int countContacts) {
        this.countContacts = countContacts;
    }

    public int getCountSMS() {
        return countSMS;
    }

    public void setCountSMS(int countSMS) {
        this.countSMS = countSMS;
    }
}
