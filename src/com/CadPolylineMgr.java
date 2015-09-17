package com;

import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IPointCollection;
import com.esri.arcgis.geometry.IPolyline;

public class CadPolylineMgr {
	IFeatureCursor pCursor;
	
//	public CadPolyline getStartPolyline() throws Exception {
//		if (this.pCursor == null) {
//			throw new Exception("CadPolylineMgr -> pCursorÎª¿Õ£¡");
//		}
//		IFeature pFeature = this.pCursor.nextFeature();
//		IGeometry pGeometry = pFeature.getShape();
//		if (pGeometry instanceof IPolyline) {
//			IPolyline polyline = (IPolyline) pGeometry;
//			IPointCollection pointCollection = (IPointCollection)polyline;
//			CadPolyline pCadPolyline = new CadPolyline(pointCollection);
//			return pCadPolyline;
//		}
//		else {
//			return null;
//		}
//	}
	
	public CadPolyline getNextPolyLine() throws Exception {
		if (this.pCursor == null) {
			throw new Exception("CadPolylineMgr -> pCursorÎª¿Õ£¡");
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
}
