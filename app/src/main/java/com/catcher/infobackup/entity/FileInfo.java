package com.catcher.infobackup.entity;

public class FileInfo {

	private String name;
	private boolean checked;

	public FileInfo() {
	}

	public FileInfo(String name, boolean checked) {
		super();
		this.name = name;
		this.checked = checked;
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
}
