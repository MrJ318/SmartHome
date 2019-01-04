package com.jevon.smarthome.bean;

import com.google.gson.annotations.SerializedName;

public class DeviceInfo {
	private int id;
	
	@SerializedName("chipid")
	private String chipId;

	private String name;

	private String note;
	
	@SerializedName("create_at")
	private String createAt;

	private String latest;

	private String stat;

	private boolean online;

	private String regist;

	private String addr;

	private int msgId;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
