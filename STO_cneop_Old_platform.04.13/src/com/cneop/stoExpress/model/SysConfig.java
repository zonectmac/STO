package com.cneop.stoExpress.model;

import java.util.Date;

public class SysConfig {

	public SysConfig(String config_name,String config_value){
		this.config_name=config_name;
		this.config_value=config_value;
	}
	
	public SysConfig(){}
	
	/**
	 * config_name
	 */
	private String config_name;

	public String getConfig_name() {
		return config_name;
	}

	public void setConfig_name(String config_name) {
		this.config_name = config_name;
	}

	/**
	 * config_value
	 */
	private String config_value;

	public String getConfig_value() {
		return config_value;
	}

	public void setConfig_value(String config_value) {
		this.config_value = config_value;
	}

	/**
	 * config_desc
	 */
	private String config_desc;

	public String getConfig_desc() {
		return config_desc;
	}

	public void setConfig_desc(String config_desc) {
		this.config_desc = config_desc;
	}

	/**
	 * lasttime
	 */
	private Date lasttime;

	public Date getLasttime() {
		return lasttime;
	}

	public void setLasttime(Date lasttime) {
		this.lasttime = lasttime;
	}
}
