/*
 * Copyright 2008 Android4ME
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bihe0832.packageinfo.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.decoder.AXmlResourceParser;
import brut.androlib.res.decoder.ResAttrDecoder;
import brut.androlib.res.decoder.ResStreamDecoderContainer;
import brut.androlib.res.decoder.XmlPullStreamDecoder;
import brut.androlib.res.util.ExtMXSerializer;
import brut.androlib.res.util.ExtXmlSerializer;
import brut.directory.Directory;
import brut.directory.DirectoryException;
import brut.directory.ExtFile;

/**
 * This is example usage of AXMLParser class.
 * 
 * Prints xml document from Android's binary xml file.
 */
public class AXMLPrinter2 {
	private static final String DEFAULT_XML = "AndroidManifest.xml";
	private static Androlib sAndrolib = new Androlib();
    private static ResStreamDecoderContainer sDecoders = new ResStreamDecoderContainer();
    private static AXmlResourceParser sAxmlParser = new AXmlResourceParser();
    private static ResAttrDecoder sAttrDecoder = new ResAttrDecoder();

    static {
        sAxmlParser.setAttrDecoder(sAttrDecoder);
        sDecoders.setDecoder("xml", new XmlPullStreamDecoder(sAxmlParser, getResXmlSerializer()));
    }

	public static String decode(String apkPath) throws AndrolibException, DirectoryException {


        ExtFile apkFile = new ExtFile(new File(apkPath));
        ResTable resTable = sAndrolib.getResTable(apkFile, true);
        resTable.setAnalysisMode(true);
        ResAttrDecoder attrDecoder = sAxmlParser.getAttrDecoder();
        attrDecoder.setCurrentPackage(resTable.listMainPackages().iterator().next());

        InputStream in = null;
        ByteArrayOutputStream out = null;
        Directory inApk = apkFile.getDirectory();
        String xmlStr = "";
        try {
            in = inApk.getFileInput(DEFAULT_XML); // apk 里的 manifest
            out = new ByteArrayOutputStream(); // memory based
            ((XmlPullStreamDecoder) sDecoders.getDecoder("xml")).decodeManifest(in, out);
            xmlStr = new String(out.toByteArray(), "UTF-8");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch(IOException e) {
                // ignore
            }
        }
        return xmlStr;
    }

    private static ExtMXSerializer getResXmlSerializer() {
        ExtMXSerializer serial = new ExtMXSerializer();
        serial.setProperty(ExtXmlSerializer.PROPERTY_SERIALIZER_INDENTATION, "    ");
        serial.setProperty(ExtXmlSerializer.PROPERTY_SERIALIZER_LINE_SEPARATOR, System.getProperty("line.separator"));
        serial.setProperty(ExtXmlSerializer.PROPERTY_DEFAULT_ENCODING, "utf-8");
        serial.setDisabledAttrEscape(true);
        return serial;
    }
}