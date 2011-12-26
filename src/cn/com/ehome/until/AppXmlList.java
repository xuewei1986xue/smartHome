package cn.com.ehome.until;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import cn.com.ehome.AppObject;
import cn.com.ehome.Website;

public class AppXmlList {
	
	String mXmlPath = null;
	Document mDoc = null;
	Context context;
	
	//private static final String
	private static final String ITEM = "item";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String DISCRIPTION = "discription";
	
	public AppXmlList(Context context, String xmlPath){
		mXmlPath = xmlPath;
		this.context = context;
	}
	
	public List<AppObject> parse(){
		if(init()==false){
			return null;
		}
		NodeList nodeList = mDoc.getElementsByTagName(ITEM);
		if (nodeList != null) {
			List<AppObject> websites = new ArrayList<AppObject>(nodeList.getLength());
			for(int i = 0; i < nodeList.getLength(); i++){
				AppObject w = new AppObject();
				Node node = nodeList.item(i);
				NamedNodeMap nodeMap = node.getAttributes();
				if(nodeMap != null && nodeMap.getNamedItem(NAME) != null){
					w.name = nodeMap.getNamedItem(NAME).getTextContent();
				}
				if(nodeMap != null && nodeMap.getNamedItem(ID) != null){
					w.id = nodeMap.getNamedItem(ID).getTextContent();
				}				
				if(nodeMap != null && nodeMap.getNamedItem(DISCRIPTION) != null){
					w.description = nodeMap.getNamedItem(DISCRIPTION).getTextContent();
				}
				websites.add(w);
			}
			return websites;
		}
		
		return null;
	}
	
	private Boolean init(){
		if(mDoc != null){
			return true;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		InputStream inputStream = null;
		try {
			//inputStream = new FileInputStream(new File(mXmlPath));
			inputStream = context.getResources().getAssets().open(mXmlPath);
		} catch (IllegalStateException e) {
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			//e.printStackTrace();
			return false;
		}

		
		try {
			mDoc = db.parse(inputStream);
		} catch (SAXException e) {
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
		mDoc.getDocumentElement().normalize();
		return true;		
	}
	
	public Boolean writeXml(List<AppObject> list){
		Boolean bRet = true;
	/*	Document mDoc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		db.
		mDoc.*/
		return bRet;
	}

}
