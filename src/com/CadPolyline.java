package com;

import java.util.ArrayList;
import java.util.List;

import com.esri.arcgis.geometry.IGeometryCollection;
import com.esri.arcgis.geometry.IPath;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPointCollection;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.ISegment;
import com.esri.arcgis.geometry.ISegmentCollection;
import com.esri.arcgis.geometry.Path;

public class CadPolyline {
	IPolyline polyline;
	List<IPoint> vertexs;// 存储polyline的节点
	// IPointCollection ptCollection;

	public CadPolyline(IPolyline pIPolyline) {
		// TODO Auto-generated constructor stub
		this.polyline = pIPolyline;
	}

	public List<IPoint> getVertexs() {
		try {
			if (this.polyline == null) {
				throw new Exception("CadPolyline -> getVertexs -> polyline为null");
			}
			if (vertexs == null) {
				setVertexs();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return vertexs;
	}

	//获取每一天polyline的所有节点
	private void setVertexs() throws Exception {
		// TODO Auto-generated method stub
		try {

			// IGeometryCollection geoCol = (IGeometryCollection)this.polyline;
			// int geomSize = geoCol.getGeometryCount();
			// IPath path = new Path();
			// for(int lgeom = 0;lgeom < geomSize;lgeom++){
			// //将geometry转成segmentcollection
			// //ISegmentCollection segCol =
			// (ISegmentCollection)geoCol.getGeometry(lgeom);
			// path = (IPath)geoCol.getGeometry(lgeom);
			// this.vertexs.add(path.getFromPoint());
			// this.vertexs.add(path.getToPoint());
			// }
			IPointCollection ptCollection = (IPointCollection) this.polyline;
			int ptCount = ptCollection.getPointCount();
			// 一条线节点数目大于50跳过不读
			//此处节点集合可能为空
			if (ptCount > 50) {
				return;
			}
			this.vertexs = new ArrayList<IPoint>();
			for (int i = 0; i < ptCollection.getPointCount(); i++) {
				IPoint pVertex = ptCollection.getPoint(i);
				this.vertexs.add(pVertex);
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("CADPolyline -> SetVertexs -> " + e.getMessage());
		}
	}
}
