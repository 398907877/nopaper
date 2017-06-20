package com.thinkgem.jeesite.modules.nopaper.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendMessage {
	@JsonProperty("JYDM")
	private String  JYDM;//交易代码
	@JsonProperty("HM")
	private  String HM;//户名
	@JsonProperty("ZJLX")
	private String ZJLX;//证件类型
	@JsonProperty("ZJHM")
	private String ZJHM;//证件号码
	@JsonProperty("XM")
	private String    XM ;//姓名
	@JsonProperty("ZJSFCQYX")
	private String    ZJSFCQYX;//证件是否长期有效
	@JsonProperty("FZJGDQDM")
	private String    FZJGDQDM;//发证机关地区代码
	@JsonProperty("XB")
	private String    XB;//性别
	@JsonProperty("GJ")
	private String    GJ;//国籍
	@JsonProperty("GDDH")
	private String  GDDH;//固定电话
	@JsonProperty("YDDH")
	private String  YDDH;//移动电话
	@JsonProperty("TXDZ")
	private String   TXDZ;//通讯地址
	@JsonProperty("YZBM")
	private String   YZBM;//邮政编码
	@JsonProperty("ZY")
	private String  ZY;//职业
	@JsonProperty("JJLXR")
	private String  JJLXR;//紧急联系人
	@JsonProperty("JJLXDH")
	private String  JJLXDH;//紧急联系电话
	
	

	
	
	public SendMessage(String jYDM, String hM, String zJLX, String zJHM,
			String xM, String zJSFCQYX, String fZJGDQDM, String xB, String gJ,
			String gDDH, String yDDH, String tXDZ, String yZBM, String zY,
			String jJLXR, String jJLXDH) {
		super();
		JYDM = jYDM;
		HM = hM;
		ZJLX = zJLX;
		ZJHM = zJHM;
		XM = xM;
		ZJSFCQYX = zJSFCQYX;
		FZJGDQDM = fZJGDQDM;
		XB = xB;
		GJ = gJ;
		GDDH = gDDH;
		YDDH = yDDH;
		TXDZ = tXDZ;
		YZBM = yZBM;
		ZY = zY;
		JJLXR = jJLXR;
		JJLXDH = jJLXDH;
	}
	public String getJYDM() {
		return JYDM;
	}
	public void setJYDM(String jYDM) {
		JYDM = jYDM;
	}
	public String getHM() {
		return HM;
	}
	public void setHM(String hM) {
		HM = hM;
	}
	public String getZJLX() {
		return ZJLX;
	}
	public void setZJLX(String zJLX) {
		ZJLX = zJLX;
	}
	public String getZJHM() {
		return ZJHM;
	}
	public void setZJHM(String zJHM) {
		ZJHM = zJHM;
	}
	public String getXM() {
		return XM;
	}
	public void setXM(String xM) {
		XM = xM;
	}
	public String getZJSFCQYX() {
		return ZJSFCQYX;
	}
	public void setZJSFCQYX(String zJSFCQYX) {
		ZJSFCQYX = zJSFCQYX;
	}
	public String getFZJGDQDM() {
		return FZJGDQDM;
	}
	public void setFZJGDQDM(String fZJGDQDM) {
		FZJGDQDM = fZJGDQDM;
	}
	public String getXB() {
		return XB;
	}
	public void setXB(String xB) {
		XB = xB;
	}
	public String getGJ() {
		return GJ;
	}
	public void setGJ(String gJ) {
		GJ = gJ;
	}
	public String getGDDH() {
		return GDDH;
	}
	public void setGDDH(String gDDH) {
		GDDH = gDDH;
	}
	public String getYDDH() {
		return YDDH;
	}
	public void setYDDH(String yDDH) {
		YDDH = yDDH;
	}
	public String getTXDZ() {
		return TXDZ;
	}
	public void setTXDZ(String tXDZ) {
		TXDZ = tXDZ;
	}
	public String getYZBM() {
		return YZBM;
	}
	public void setYZBM(String yZBM) {
		YZBM = yZBM;
	}
	public String getZY() {
		return ZY;
	}
	public void setZY(String zY) {
		ZY = zY;
	}
	public String getJJLXR() {
		return JJLXR;
	}
	public void setJJLXR(String jJLXR) {
		JJLXR = jJLXR;
	}
	public String getJJLXDH() {
		return JJLXDH;
	}
	public void setJJLXDH(String jJLXDH) {
		JJLXDH = jJLXDH;
	}

	
	
	
	
	

}
