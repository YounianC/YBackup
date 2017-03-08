package com.catcher.infobackup.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * XMLд������ڲ�����StringBuilder��BufferedWriter���Ż�д�����ܣ�ͬʱ��д��
 * @author catch
 *
 */
public class XMLWriter {

	private static final String START_START = "<";
	private static final String END = ">";
	private static final String END_START = "</";
	private static final String START_DATA = "<![CDATA[";
	private static final String END_DATA = "]]>";
	private String charset = "utf-8";
	private StringBuilder sb = new StringBuilder();
	private BufferedWriter writer; 
	
	/**
	 * XMLWriter����Ĭ�ϱ���Ϊutf-8
	 * @param file	Ҫд����ļ�
	 * @throws FileNotFoundException
	 */
	public XMLWriter(File file) throws FileNotFoundException {
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * XMLWriter����
	 * @param file	Ҫд����ļ�
	 * @param charset ����
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public XMLWriter(File file, String charset) throws UnsupportedEncodingException, FileNotFoundException {
		//this(file);
		this.charset = charset;
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
	}
	
	/**
	 * д��ʼ��ǩ
	 * @param tag	��ǩ����
	 */
	public void writeStartTAG(String tag){
		sb.append(START_START);
		sb.append(tag);
		sb.append(END);
	}
	
	/**
	 * д������ǩ
	 * @param tag	��ǩ����
	 */
	public void writeEndTAG(String tag){
		sb.append(END_START);
		sb.append(tag);
		sb.append(END);
	}
	
	/**
	 * д���ı�
	 * @param text
	 */
	public void writeText(String text){
		sb.append(text);
	}
	
	/**
	 * д���<![CDATA[ ]]>���ı�
	 * @param text
	 */
	public void writeTextData(String text){
		sb.append(START_DATA);
		sb.append(text);
		sb.append(END_DATA);
	}
	
	/**
	 * �ı���ǩһ��д��
	 * @param text
	 * @param tag
	 */
	public void writeText(String text, String tag){
		writeStartTAG(tag);
		writeText(text);
		writeEndTAG(tag);
	}
	
	/**
	 * �ı���ǩһ��д�벢����<![CDATA[ ]]>
	 * @param text
	 * @param tag
	 */
	public void writeTextData(String text, String tag){
		writeStartTAG(tag);
		writeTextData(text);
		writeEndTAG(tag);
	}
	
	/**
	 * дͷ�ļ�
	 */
	public void writeDocument(){
		sb.append("<?xml version=\"1.0\" encoding=\"" + charset + "\" standalone=\"yes\" ?>");
	}

	/**
	 * ��StringBuilder�е�����ˢ�µ�BufferedWriter�У������StringBuidler
	 * @throws IOException
	 */
	public void flush() throws IOException{
		writer.write(sb.toString());
		sb.delete(0, sb.length());
	}
	
	/**
	 * �ر�BufferedWriter��Դ����֮ǰ�����flush()����
	 * @throws IOException
	 */
	public void close() throws IOException{
		flush();
		writer.close();
	}
	
}
