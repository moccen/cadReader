package com;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.datasourcesfile.CadWorkspaceFactory;
import com.esri.arcgis.display.IFormattedTextSymbol;
import com.esri.arcgis.display.ISymbol;
import com.esri.arcgis.display.ISymbolCollection;
import com.esri.arcgis.display.SymbolCollection;
import com.esri.arcgis.display.TextSymbol;
import com.esri.arcgis.geodatabase.FeatureClassDescription;
import com.esri.arcgis.geodatabase.Field;
import com.esri.arcgis.geodatabase.FieldChecker;
import com.esri.arcgis.geodatabase.Fields;
import com.esri.arcgis.geodatabase.GeometryDef;
import com.esri.arcgis.geodatabase.IEnumFieldError;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureClassDescription;
import com.esri.arcgis.geodatabase.IFeatureDataset;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IFeatureWorkspaceAnno;
import com.esri.arcgis.geodatabase.IFeatureWorkspaceProxy;
import com.esri.arcgis.geodatabase.IField;
import com.esri.arcgis.geodatabase.IFieldEdit;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.geodatabase.IFieldsEdit;
import com.esri.arcgis.geodatabase.IGeometryDef;
import com.esri.arcgis.geodatabase.IGeometryDefEdit;
import com.esri.arcgis.geodatabase.IObjectClassDescription;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.IWorkspaceFactory;
import com.esri.arcgis.geodatabase.ObjectClassDescription;
import com.esri.arcgis.geodatabase.esriFeatureType;
import com.esri.arcgis.geodatabase.esriFieldType;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.support.ms.stdole.Font;
import com.esri.arcgis.support.ms.stdole.StdFont;
import com.esri.arcgis.system.UID;

public class CADLayerCreator {

	private String _nameOfPath;// cad文件所在的目录
	private String _nameOfCADFile;// cad文件名称，带后缀名称
//	private String _fullPath;// path+filename
//	private IFeatureWorkspace _pFeatureWorkspace;

	public CADLayerCreator(String nameOfPath, String nameOfCADFile) {
		this._nameOfPath = nameOfPath;
		this._nameOfCADFile = nameOfCADFile;
	}

	// 打开cad文件，根据FeatureType获取不同的FeatureClass
	public IFeatureClass getCadFC(FeatureType FT) throws Exception {
		try {
			if (!this.isFileExist()) {
				throw new IOException(String.format("在路径：‘%s’中未找到名为‘%s’的文件", this._nameOfPath, this._nameOfCADFile));
			}
			// get cad file full name
			String openString = getCadFullNameString(FT);
			// set the workspace factory
			IWorkspaceFactory pWorkspaceFactory = new CadWorkspaceFactory();
			// open the workspace
			IWorkspace pWorkspace = pWorkspaceFactory.openFromFile(this._nameOfPath, 0);
			// change the font test
			// changeFont(pWorkspace);
			// set the feature workspace
			IFeatureWorkspace pFeatureWorkspace = new IFeatureWorkspaceProxy(pWorkspace);
			// this._pFeatureWorkspace = pFeatureWorkspace;
			// open the feature class
			IFeatureClass pFeatureClass = pFeatureWorkspace.openFeatureClass(openString);

			return pFeatureClass;
		} catch (Exception e) {
			// TODO: handle exception
			// System.out.println(e.getMessage());
			throw new Exception("CADLayerCreator-> GetCadFC-> " + e.getMessage());
		}
	}

	// 尝试更改默认cad的字体为宋体，来解决乱码问题
	// private void changeFont(IWorkspace pWS) throws Exception, IOException {
	// try {
	// IFeatureWorkspaceAnno pAnno = (IFeatureWorkspaceAnno) pWS;
	// ISymbolCollection pSymbolCollection = new SymbolCollection();
	// IFormattedTextSymbol pFormattedTextSymbol = new TextSymbol();
	// Font pFont = new StdFont();
	// pFont.setName("宋体");
	// pFormattedTextSymbol.setFont(pFont);
	// pSymbolCollection.setSymbolByRef(0, (ISymbol) pFormattedTextSymbol);
	// pAnno.replaceSymbolCollection("annoTest", pSymbolCollection);
	// } catch (Exception e) {
	// // TODO: handle exception
	// throw new Exception("changeFont failed!!!" + e.getMessage());
	// }
	//
	// }

	// 调用getCadFC函数，将获取的FeatureClass文件包装成FeatureLayer
	public IFeatureLayer getCadFL(FeatureType FT) throws Exception {
		try {
			IFeatureClass pFeatureClass = getCadFC(FT);
			IFeatureLayer pFeatureLayer = new FeatureLayer();
			pFeatureLayer.setFeatureClassByRef(pFeatureClass);
			pFeatureLayer.setName(this._nameOfCADFile + getCadFullNameString(FT));

			return pFeatureLayer;
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("CADLayerCreator-> getCadFL-> " + e.getMessage());
		}
	}

	private boolean isFileExist() {
		String fullPath = this._nameOfPath + "\\" + this._nameOfCADFile;
		File file = new java.io.File(fullPath);
		return file.exists();
	}

	// 指定打开cad文件中不同的图层
	private String getCadFullNameString(FeatureType pFT) throws Exception {
		try {
			switch (pFT) {
			case polyline:
				return this._nameOfCADFile + ":Polyline";
			case multitext:
				return this._nameOfCADFile + ":Annotation";
			default:
				return this._nameOfCADFile + ":Polyline";
			}
		} catch (Exception e) {
			throw new Exception("CADLayerCreator -> getCadFullNameString -> " + e.getMessage());
		}
	}
}
