package cn.com.ehome.until;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlPreference {
	String mXmlPath = null;
	Document mDoc = null;
	
	public XmlPreference(String xmlPath){
		mXmlPath = xmlPath;	
	}
	public String getKeyValue(String key){
		if(init() == false){
			return null;
		}
		String value = null;
		NodeList nodeList = mDoc.getElementsByTagName(key);
		if (nodeList != null && nodeList.getLength() != 0) {
			value = nodeList.item(0).getTextContent();
		}
		return value;
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
			inputStream = new FileInputStream(new File(mXmlPath));
			// inputStream = getResources().getAssets().open("hotel_init.xml");
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
