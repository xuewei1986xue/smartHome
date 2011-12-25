package cn.com.ehome.utill;

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
import cn.com.ehome.Website;

public class WebsiteList {
	
	String mXmlPath = null;
	Document mDoc = null;
	Context context;
	
	//private static final String 
	
	public WebsiteList(Context context, String xmlPath){
		mXmlPath = xmlPath;
		this.context = context;
	}
	
	public List<Website> parse(){
		if(init()==false){
			return null;
		}
		NodeList nodeList = mDoc.getElementsByTagName("website");
		if (nodeList != null) {
			List<Website> websites = new ArrayList<Website>(nodeList.getLength());
			for(int i = 0; i < nodeList.getLength(); i++){
				Website w = new Website();
				Node node = nodeList.item(i);
				NamedNodeMap nodeMap = node.getAttributes();
				if(nodeMap != null && nodeMap.getNamedItem("name") != null){
					w.name = nodeMap.getNamedItem("name").getTextContent();
				}
				if(nodeMap != null && nodeMap.getNamedItem("url") != null){
					w.url = nodeMap.getNamedItem("url").getTextContent();
				}
				if(nodeMap != null && nodeMap.getNamedItem("icon") != null){
					w.icon = nodeMap.getNamedItem("icon").getTextContent();
				}
				if(nodeMap != null && nodeMap.getNamedItem("discription") != null){
					w.descrip = nodeMap.getNamedItem("discription").getTextContent();
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

}
