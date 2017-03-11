package cn.net.younian.youbackup.entity;

import android.support.annotation.NonNull;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import cn.net.younian.youbackup.util.Constants;

public class FileInfo implements Comparable {

    private String name;
    private String path;
    private String time;
    private boolean checked;

    private int countContacts;
    private int countSMS;
    private int countCallLog;


    public FileInfo(String path, String time, boolean checked) {
        super();
        this.path = path + File.separator + time + File.separator;
        this.name = time;
        this.time = time;
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
                this.name = infoObject.getString(Constants.Info_Name);
                if (this.name == null || this.name.equals("")) {
                    this.name = this.time;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            updateInfoFile();
        }
    }

    public void updateCount() {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(path + Constants.File_Contacts));
            countContacts = document.getRootElement().elements().size();
            document = reader.read(new File(path + Constants.File_SMS));
            countSMS = document.getRootElement().elements().size();
            document = reader.read(new File(path + Constants.File_CallLog));
            countCallLog = document.getRootElement().elements().size();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void updateInfoFile() {
        try {
            File urlFile = new File(this.path + Constants.File_Info);
            updateCount();
            JSONObject infoObject = new JSONObject();
            infoObject.put(Constants.Info_CountContacts, countContacts);
            infoObject.put(Constants.Info_CountSMS, countSMS);
            infoObject.put(Constants.Info_CountCallLog, countCallLog);
            infoObject.put(Constants.Info_Name, name);

            FileOutputStream writerStream = new FileOutputStream(urlFile, false);
            BufferedWriter oWriter = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            oWriter.write(infoObject.toString());
            oWriter.close();
            writerStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        FileInfo fileInfo = (FileInfo) o;
        return fileInfo.getTime().compareTo(this.time);
    }
}
