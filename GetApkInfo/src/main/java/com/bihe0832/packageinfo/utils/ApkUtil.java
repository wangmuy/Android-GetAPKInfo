package com.bihe0832.packageinfo.utils;

import com.bihe0832.packageinfo.bean.ApkInfo;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;


public class ApkUtil {

    private static final Namespace NS = Namespace.getNamespace("http://schemas.android.com/apk/res/android");

    public static void getApkInfo(String apkPath, ApkInfo info){
        SAXBuilder builder = new SAXBuilder();
        Document document = null;
        InputStream stream = null;
        try{
            String decoded = AXMLPrinter2.decode(apkPath);
            byte[] bytes = decoded.getBytes(StandardCharsets.UTF_8);
            stream = new ByteArrayInputStream(bytes);
            document = builder.build(stream);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch(IOException e) {
                // ignore
            }
        }
        Element root = document.getRootElement();
        info.versionCode = root.getAttributeValue("versionCode",NS);
        info.versionName = root.getAttributeValue("versionName", NS);
        String s = root.getAttributes().toString();
        String c[] = s.split(",");
        for(String a: c){
            if(a.contains("package")){
                info.packageName = a.substring(a.indexOf("package=\"")+9, a.lastIndexOf("\""));
            }
        }

        List booklist = null;
        booklist=root.getChildren("uses-sdk");
        if (booklist.size() > 0) {
            Element book = (Element) booklist.get(0);
            info.minSdkVersion = book.getAttributeValue("minSdkVersion", NS);
            info.targetSdkVersion = book.getAttributeValue("targetSdkVersion", NS);
        }

        booklist=root.getChildren("uses-permission");
        for (Iterator iter = booklist.iterator(); iter.hasNext();) {
            Element tempBook = (Element) iter.next();
            info.permissions.add(tempBook.getAttributeValue("name", NS));
        }
    }

}
