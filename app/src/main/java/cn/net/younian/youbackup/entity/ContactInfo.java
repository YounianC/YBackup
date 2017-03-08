package cn.net.younian.youbackup.entity;

import java.util.List;

public class ContactInfo {

	private int id;
	private String name;
	private List<String> phoneNum;

	public ContactInfo() {
		super();
	}

	public ContactInfo(int id, String name, List<String> phoneNum) {
		super();
		this.id = id;
		this.name = name;
		this.phoneNum = phoneNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(List<String> phoneNum) {
		this.phoneNum = phoneNum;
	}

}
