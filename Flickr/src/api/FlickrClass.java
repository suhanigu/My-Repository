package api;

import java.awt.Desktop;

import javax.xml.bind.Binder;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.file.attribute.AclEntry.Builder;
import java.security.MessageDigest;
import java.util.*;


public class FlickrClass {
	
	protected static String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }

	public static String MD5(String text)
	{
		String md5Text = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			md5Text = new BigInteger(1, digest.digest((text).getBytes())).toString(16);
		} catch (Exception e) {
			System.out.println("Error in call to MD5");
		}
		
        if (md5Text.length() == 31) {
            md5Text = "0" + md5Text;
        }
		return md5Text;
	}
	
	    @SuppressWarnings("deprecation")
		public static void main(String args[]) throws Exception {
	    	String my_apikey = "3d6ae1c7fe2d1083fb36c82c4dfb1db7";
	    	String secret = "0381f20eb0a8fd61";
	    	String userid = "88592258@N02";
	    	String photoid = "8085848880 - DSC_0531";
	    	
		    String my_apisig = MD5(secret+"api_key"+my_apikey+"methodflickr.auth.getFrob");
		    System.out.println("api_sig: " + my_apisig);
		    String url = "http://api.flickr.com/services/rest/?method=flickr.auth.getFrob&api_key="+ my_apikey + "&api_sig=" + my_apisig;
	    	URLConnection uc = new URL(url).openConnection();
	    	
	    	DataInputStream dis = new DataInputStream(uc.getInputStream());
	        FileWriter fw = new FileWriter(new File("E:\\\\frob.xml"));
	        String nextline;
	        while((nextline= dis.readLine()) != null)
	        	fw.append(nextline);
	        dis.close();
	        fw.close();
	        String frob;
	        String filename = "E:\\\\frob.xml";
	        
	        DocumentBuilderFactory factory_frob = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory_frob.newDocumentBuilder();
	        Document document = builder.parse(filename);
	        Element rootElement = document.getDocumentElement();
	        
	        int i = -1;
	        frob = getString("frob", rootElement);
	        System.out.println("frob: " + frob);
	        
	        String output = MD5(secret+"api_key"+my_apikey+"frob"+frob+"permsread");
	        System.out.println("output: " + output);
	        url = "http://www.flickr.com/services/auth/?api_key="+my_apikey+"&perms=read&frob="+frob+"&api_sig="+output;

	        System.out.println("Browse to the following flickr url to authenticate yourself and then press enter.");
			System.out.println(url);
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			BufferedReader infile = new BufferedReader ( new InputStreamReader (System.in) );
			String line = infile.readLine();
			
	        
	        String signature = MD5(secret+"api_key"+my_apikey+"frob"+frob+"methodflickr.auth.getToken");
	        url = "http://api.flickr.com/services/rest/?method=flickr.auth.getToken&api_key=" + my_apikey + "&frob=" + frob + "&api_sig=" + signature;
	        uc = new URL(url).openConnection();
	        
	        dis = new DataInputStream(uc.getInputStream());
	        fw = new FileWriter(new File("E:\\\\authtoken.xml"));
	     
	        while((nextline= dis.readLine()) != null)
	        	fw.append(nextline);
	        dis.close();
	        fw.close();
	        String authtoken;
	        filename = "E:\\\\authtoken.xml";
	        
	        DocumentBuilderFactory factory_auth = DocumentBuilderFactory.newInstance();
	        builder = factory_auth.newDocumentBuilder();
	        document = builder.parse(filename);
	        rootElement = document.getDocumentElement();
	        
	        authtoken = getString("token", rootElement);
	        System.out.println("authtoken: " + authtoken);
	        
	        NodeList nl = document.getElementsByTagName("user");
            for(i = 0 ; i < nl.getLength(); i++){                   
                    Element e = (Element)nl.item(i);
                    userid = e.getAttribute("nsid");
            }
            System.out.println("userid: " + userid);
            
	        
	        signature = MD5(secret+"api_key"+my_apikey+"auth_token"+authtoken+"methodflickr.people.getPhotosuser_id"+userid);
	        url = "http://api.flickr.com/services/rest/?method=flickr.people.getPhotos&api_key=" + my_apikey + "&auth_token=" + authtoken + "&user_id=" + userid + "&api_sig=" + signature;
	        uc = new URL(url).openConnection();
	        dis = new DataInputStream(uc.getInputStream());
	        fw = new FileWriter(new File("E:\\\\Pic1.xml"));
	        String[] servers = new String[10];
	        String[] ids = new String[10];
	        String[] secrets = new String[10];
	        while ((nextline = dis.readLine()) != null) {
	            fw.append(nextline);
	        }
	        dis.close();
	        fw.close();
	        filename = "E:\\\\Pic1.xml";
	        XMLInputFactory factory = XMLInputFactory.newInstance();
	        System.out.println("FACTORY: " + factory);

	        XMLEventReader r = factory.createXMLEventReader(filename, new FileInputStream(filename));
	        i = -1;
	        while (r.hasNext()) {

	            XMLEvent event = r.nextEvent();
	            if (event.isStartElement()) {
	                StartElement element = (StartElement) event;
	                String elementName = element.getName().toString();
	                if (elementName.equals("photo")) {
	                    i++;
	                    Iterator iterator = element.getAttributes();

	                    while (iterator.hasNext()) {

	                        Attribute attribute = (Attribute) iterator.next();
	                        QName name = attribute.getName();
	                        String value = attribute.getValue();
	                        System.out.println("Attribute name/value: " + name + "/" + value);
	                        if ((name.toString()).equals("server")) {
	                            servers[i] = value;
	                            System.out.println("Server Value" + servers[0]);
	                        }
	                        if ((name.toString()).equals("id")) {
	                            ids[i] = value;
	                        }
	                        if ((name.toString()).equals("secret")) {
	                            secrets[i] = value;
	                        }
	                    }
	                }
	            }
	        }
	        System.out.println(i);
	        
	        for(int j=0;j<=i;j++){
	        String flickrurl = "http://static.flickr.com/" + servers[j] + "/" + ids[j] + "_" + secrets[j] + ".jpg";
	        try {
	            URI uri = new URI(flickrurl);
	            Desktop desktop = null;
	            if (Desktop.isDesktopSupported()) {
	                desktop = Desktop.getDesktop();
	            }

	            if (desktop != null) {
	                desktop.browse(uri);
	            }
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        } catch (URISyntaxException use) {
	            use.printStackTrace();
	        }
	      }
	        
	        
	        
	        signature = MD5(secret+"api_key"+my_apikey+"auth_token"+authtoken+"methodflickr.activity.userCommentsuser_id"+userid);
	        url = "http://api.flickr.com/services/rest/?method=flickr.activity.userComments&api_key=" + my_apikey + "&auth_token=" + authtoken + "&user_id=" + userid + "&api_sig=" + signature;
	        uc = new URL(url).openConnection();
	        dis = new DataInputStream(uc.getInputStream());
	        fw = new FileWriter(new File("E:\\\\comment.xml"));
	        String[] user = new String[10];
	        String[] dateadded = new String[10];
	        String[] comment = new String[10];
	        while ((nextline = dis.readLine()) != null) {
	            fw.append(nextline);
	        }
	        dis.close();
	        fw.close();
	        filename = "E:\\\\comment.xml";
	        
	        DocumentBuilderFactory factory_comment = DocumentBuilderFactory.newInstance();
	        builder = factory_comment.newDocumentBuilder();
	        document = builder.parse(filename);
	        rootElement = document.getDocumentElement();
	        
	        NodeList n = document.getElementsByTagName("event");
            for(i = 0 ; i < n.getLength(); i++){                   
                    Element e = (Element)n.item(i);
                    user[i] = e.getAttribute("user");
                    dateadded[i] = e.getAttribute("dateadded");
                    comment[i] = getString("event", rootElement);
        	        System.out.println("user: " + user[i] + " commented " + comment[i] + " on " + dateadded[i]);
            }
	        
	        
	}
}
