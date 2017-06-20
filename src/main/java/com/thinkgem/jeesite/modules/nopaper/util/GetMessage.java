package com.thinkgem.jeesite.modules.nopaper.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetMessage {
	@JsonProperty("XYM")
	private  String   XYM;//响应码;

	public String getXYM() {
		return XYM;
	}

	public void setXYM(String XYM) {
		this.XYM = XYM;
	}
	
	
	

}
