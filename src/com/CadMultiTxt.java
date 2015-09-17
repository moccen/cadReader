package com;

import com.esri.arcgis.geometry.IPoint;

public class CadMultiTxt {

	private IPoint pt;//store cad multitext position
	public IPoint get_pt() {
		return pt;
	}

	public void set_pt(IPoint pt) {
		this.pt = pt;
	}
	
	private String text;//store cad multitext text
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}		
}
