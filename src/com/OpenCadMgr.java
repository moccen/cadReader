package com;

import com.esri.arcgis.carto.IElement;
import com.esri.arcgis.carto.IGraphicsContainer;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.IMapDocument;
import com.esri.arcgis.carto.IMarkerElement;
import com.esri.arcgis.carto.IMxdContents;
import com.esri.arcgis.carto.Map;
import com.esri.arcgis.carto.MapDocument;
import com.esri.arcgis.carto.MarkerElement;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IZAware;
import java.io.IOException;
import java.util.List;

import com.CADLayerCreator;
import com.esri.arcgis.system.EngineInitializer;

public class OpenCadMgr {
	CADLayerCreator _cadLyrCreator;
	String nameOfCadFileString;

	//根据dgn，dwg文件名称和路径，初始化CADLayerCreator对象，该对象用于获取CadResult对象
	public OpenCadMgr() {
		String nameOfPathString = "C:\\Users\\lee\\Documents\\code\\data";
		nameOfCadFileString = "NNN+201301081618137++船闸上闸首检修叠梁门门体总图(115S66-03-Ⅲ-08.00).dgn";
		this._cadLyrCreator = new CADLayerCreator(nameOfPathString, nameOfCadFileString);
		initLicence();
	}

	// 初始化arcengine license 权限
	private void initLicence() {
		EngineInitializer.initializeEngine();
		try {
			com.esri.arcgis.system.AoInitialize ao = new com.esri.arcgis.system.AoInitialize();
			if (ao.isProductCodeAvailable(
					com.esri.arcgis.system.esriLicenseProductCode.esriLicenseProductCodeEngine) == com.esri.arcgis.system.esriLicenseStatus.esriLicenseAvailable)
				ao.initialize(com.esri.arcgis.system.esriLicenseProductCode.esriLicenseProductCodeEngine);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OpenCadMgr(String filePath) {
		// TODO add new CADLayerCreator constructor
	}

	public void showTexts() {
		try {
			IFeatureClass pTxtFeatureClass = this._cadLyrCreator.getCadFC(FeatureType.multitext);
			if (pTxtFeatureClass != null) {
				IFeatureCursor pFeatureCursor = pTxtFeatureClass.search(null, false);
				IFeature pFeature = pFeatureCursor.nextFeature();
				// String logString = "";
				int featureIndex = 0;
				while (pFeature != null) {
					int index = pFeature.getFields().findField("text");
					String txtString = pFeature.getValue(index).toString();
					IPoint ptIPoint = (IPoint) pFeature.getShape();
					System.out.println(String.format("Feature:%s->text:%s->X:%s;Y->%s", String.valueOf(featureIndex),
							txtString, String.valueOf(ptIPoint.getX()), String.valueOf(ptIPoint.getY())));
					pFeature = pFeatureCursor.nextFeature();
					featureIndex++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 返回开cad文件的结果类，CadResult包含cadMultitext对象集合和cadpolyline读取方法
	private CadResult getOpenResult() {
		try {
			IFeatureClass pTxtFC = this._cadLyrCreator.getCadFC(FeatureType.multitext);
			IFeatureClass pPolylineFC = this._cadLyrCreator.getCadFC(FeatureType.polyline);
			// changeFont(pTxtFC);
			CadResult cadRst = new CadResult(pTxtFC, pPolylineFC);

			return cadRst;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	String outputStr = "";

	//测试用，显示读取的cadMultitext字符串
	public void showTxt() {
		try {
			CadResult cadRst = getOpenResult();
			int featureIndex = 0;
			for (CadMultiTxt cadMultiTxt : cadRst.getLyrMultiTxts()) {
				IPoint pt = cadMultiTxt.get_pt();
				outputStr += String.format("textFC:%s; text:%s; loacation:%s,%s \n", featureIndex,
						cadMultiTxt.getText(), pt.getX(), pt.getY());
				// System.out.println(String.format("textFC:%s; text:%s;
				// loacation:%s,%s", featureIndex,
				// cadMultiTxt.getText(), pt.getX(), pt.getY()));
				featureIndex++;
			}
			outputStr += "\n<!-------------------------txt end--------------------------->\n";
			// LogWriter.log(outputStr, nameOfCadFileString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//测试方法
	public void createMxd() throws Exception, IOException {
		try {
			// IFeatureClass pFC =
			// this._cadLyrCreator.getCadFC(FeatureType.polyline);
			IFeatureClass pFC = com.debuger.FeatureClassCreator.getFeatureClass();

			setVertexsFC(pFC);
			// IFeatureLayer pFeatureLayer = new FeatureLayer();
			// pFeatureLayer.setFeatureClassByRef(pFC);
			IMap pMap = new Map();
			// drawVertexs(pMap);
			// int count = ((IGraphicsContainer)pMap).updateElement(element);
			// pMap.addLayer(pFeatureLayer);
			IMxdContents mxdContents = (IMxdContents) pMap;
			IMapDocument pMapDoc = new MapDocument();
			String nameOfCad = nameOfCadFileString.split(".dwg")[0] + ".mxd";
			String pathStr = String.format("C:\\Users\\lee\\Documents\\code\\data\\%s", nameOfCad);
			pMapDoc.esri_new(pathStr);
			pMapDoc.replaceContents(mxdContents);
			pMapDoc.save(true, true);
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("createMxd failed!!!" + e.getMessage());
		}

	}

	//测试方法
	public void showVertexs() {
		try {
			CadResult cadRst = getOpenResult();
			// List<CadPolyline> cadPolylines = cadRst.getLyrPolylines();
			int counter = 0;
			CadPolyline pCadPolyline = cadRst.getNextPolyLine();
			int i = 0;

			while (pCadPolyline != null) {
				String polyVertexsStr = "";
				List<IPoint> vertexs = pCadPolyline.getVertexs();
				pCadPolyline = cadRst.getNextPolyLine();
				if (vertexs == null) {
					continue;
				}
				int ptCounter = 0;
				for (IPoint pt : vertexs) {
					polyVertexsStr += String.format("polyline:%s;point:%s;x:%s;y:%s ->", counter, ptCounter++,
							pt.getX(), pt.getY());
					// System.out.println(String.format("polyline:%s;point:%s;x:%s;y:%s
					// ->", counter, ptCounter++,
					// pt.getX(), pt.getY()));
				}
				counter++;
				polyVertexsStr += "\n";
				System.out.println(polyVertexsStr);
				i++;
				if (i == 100) {
					i = 0;
					System.gc();
				}
			}

			// for (CadPolyline cadPolyline : cadPolylines) {
			// List<IPoint> vertexs = cadPolyline.getVertexs();
			// int ptCounter = 0;
			// for (IPoint pt : vertexs) {
			//
			// // outputStr +=
			// // String.format("polyline:%s;point:%s;x:%s;y:%s ->",
			// // counter, ptCounter++, pt.getX(),
			// // pt.getY());
			// System.out.println(String.format("polyline:%s;point:%s;x:%s;y:%s
			// ->", counter, ptCounter++,
			// pt.getX(), pt.getY()));
			// }
			// counter++;
			// outputStr += "\n";
			// }

			// LogWriter.log(outputStr,nameOfCadFileString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//测试用，将读取的节点保存到Featureclass中
	private void setVertexsFC(IFeatureClass pVertexsFC) {
		try {
			CadResult cadRst = getOpenResult();
			// List<CadPolyline> cadPolylines = cadRst.getLyrPolylines();
			CadPolyline pCadPolyline = cadRst.getNextPolyLine();
			while (pCadPolyline != null) {
				List<IPoint> vertexs = pCadPolyline.getVertexs();
				pCadPolyline = cadRst.getNextPolyLine();
				if (vertexs == null)
					continue;
				for (IPoint pt : vertexs) {
					IFeature pFeature = pVertexsFC.createFeature();
					IZAware pIzAware = (IZAware)pt;
					pIzAware.setZAware(false);
					pFeature.setShapeByRef(pt);
					pFeature.store();
					// System.out.println(pFeature.toString());
				}
			}
			// for (CadPolyline cadPolyline : cadPolylines) {
			// List<IPoint> vertexs = cadPolyline.getVertexs();
			// for (IPoint pt : vertexs) {
			// IFeature pFeature = pVertexsFC.createFeature();
			// pFeature.setShapeByRef(pt);
			// pFeature.store();
			// }
			// }

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//测试方法
	private void drawVertexs(IMap pMap) {
		try {
			CadResult cadRst = getOpenResult();
			CadPolyline pCadPolyline = cadRst.getNextPolyLine();
			int i = 0;

			IGraphicsContainer pContainer = (IGraphicsContainer) pMap;
			while (pCadPolyline != null) {
				List<IPoint> vertexs = pCadPolyline.getVertexs();
				pCadPolyline = cadRst.getNextPolyLine();
				if (vertexs == null)
					continue;
				for (IPoint pt : vertexs) {
					IMarkerElement pMarkerElement = new MarkerElement();
					IElement pElement = (IElement) pMarkerElement;
					pElement.setGeometry(pt);
					pContainer.addElement(pElement, 0);
					System.out.println(pElement.toString());
				}
				i++;
				if (i == 100) {
					i = 0;
					System.gc();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
