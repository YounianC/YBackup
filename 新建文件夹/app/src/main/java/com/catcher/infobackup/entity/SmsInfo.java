package com.catcher.infobackup.entity;

public class SmsInfo {

	private int id;
	private int type;
	private String address;
	private String body;
	private long date;

	public SmsInfo() {
		super();
	}

	public SmsInfo(int id, int type, String address, String body, long date) {
		super();
		this.id = id;
		this.type = type;
		this.address = address;
		this.body = body;
		this.date = date;
	}

	public SmsInfo(int type, String address, String body, long date) {
		super();
		this.type = type;
		this.address = address;
		this.body = body;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

}
