/**
 * 
 */
package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPointCollection;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.interop.AutomationException;

/**
 * @author chopic ��װ�򿪵Ľ������Ϊͬ��Ľ������
 */
public class CadResult {

	private List<CadMultiTxt> lyrMultiTxts;
	private List<CadPolyline> lyrPolylines;
	private IFeatureClass multiTxtFC;
	private IFeatureClass polylineFC;
	private IFeatureCursor pCursor;

	public CadResult(IFeatureClass multiTxtFC, IFeatureClass polylineFC) throws AutomationException, IOException {
		this.multiTxtFC = multiTxtFC;
		this.polylineFC = polylineFC;
		if (polylineFC != null) {
			this.pCursor = polylineFC.search(null, false);
		}
	}

	public List<CadMultiTxt> getLyrMultiTxts() {
		try {
			if (this.lyrMultiTxts == null) {
				this.lyrMultiTxts = new ArrayList<CadMultiTxt>();
				setMultiTxts();
			}
			// return this.lyrMultiTxts;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return lyrMultiTxts;
	}

	//����lyrMultiTxts
	private void setMultiTxts() throws Exception {
		try {
			// TODO Auto-generated method stub
			if (this.multiTxtFC == null) {
				throw new Exception("CadResult -> setMultiTxts -> multiTxtFCҪ����Ϊ��");
			}
			IFeatureCursor pCursor = this.multiTxtFC.search(null, false);
			int txtIndex = this.multiTxtFC.findField("text");
			IFeature pFeature = pCursor.nextFeature();
			while (pFeature != null) {
				CadMultiTxt pCadMultiTxt = new CadMultiTxt();
				String cadTxt = getFeatureTxt(pFeature, txtIndex);
				pCadMultiTxt.setText(cadTxt);
				IPoint txtPt = (IPoint) pFeature.getShape();
				pCadMultiTxt.set_pt(txtPt);
				this.lyrMultiTxts.add(pCadMultiTxt);

				pFeature = pCursor.nextFeature();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// ��ȡcad��ע�ı�
	private String getFeatureTxt(IFeature pFeature, int txtIndex) throws Exception {
		try {
			// û���ҵ�text������
			if (txtIndex == -1) {
				return "δ�ҵ�text����";
			}
			return pFeature.getValue(txtIndex).toString();
		} catch (Exception ex) {
			throw new Exception("CadResult -> getFeatureTxt -> " + ex.getMessage());
		}
	}

	public List<CadPolyline> getLyrPolylines() {
		try {
			if (this.lyrPolylines == null) {
				this.lyrPolylines = new ArrayList<CadPolyline>();
				setPolylines();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return this.lyrPolylines;

	}

	public CadPolyline getNextPolyLine() throws Exception {
		if (this.pCursor == null) {
			throw new Exception("CadPolylineMgr -> pCursorΪ�գ�");
		}
		IFeature pFeature = this.pCursor.nextFeature();
		if (pFeature == null) {
			return null;
		}
		IGeometry pGeometry = pFeature.getShape();
		if (pGeometry instanceof IPolyline) {
			IPolyline polyline = (IPolyline) pGeometry;
			//IPointCollection pointCollection = (IPointCollection)polyline;
			CadPolyline pCadPolyline = new CadPolyline(polyline);
			return pCadPolyline;
		}
		else {
			return null;
		}
	}
	
	//����lyrPolylines
	private void setPolylines() {
		// TODO Auto-generated method stub
		try {
			if (this.polylineFC == null) {
				throw new Exception("CadResult -> setPolylines -> polylineFCҪ����Ϊ��");
			}
			//System.out.println(this.polylineFC.featureCount(null));
			IFeatureCursor pCursor = this.polylineFC.search(null, false);
			IFeature pFeature = pCursor.nextFeature();
			while (pFeature != null) {
				IGeometry pGeometry = pFeature.getShapeCopy();
				if (pGeometry instanceof IPolyline) {
					IPolyline polyline = (IPolyline) pGeometry;
					//IPointCollection ptCollection = (IPointCollection)polyline;
					CadPolyline pCadPolyline = new CadPolyline(polyline);
					this.lyrPolylines.add(pCadPolyline);
					pFeature = pCursor.nextFeature();
				}else {
					pFeature = pCursor.nextFeature();;
					System.out.println(String.format("%s->��Ҫ�ز�����Ҫ�أ�->%s", pFeature.getOID(),pFeature.getFeatureType()));
				}						
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
