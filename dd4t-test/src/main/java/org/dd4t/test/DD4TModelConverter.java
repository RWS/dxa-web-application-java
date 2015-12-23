/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.test;
/**
 * Created by rai on 03/06/14.
 */

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.databind.DataBindFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.GZIPInputStream;

public class DD4TModelConverter {

	public static void main (String[] args) throws IOException, XMLStreamException, SerializationException, URISyntaxException {

		// Load Spring
		ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");


//		System.out.println(testXml + xml2 + xml3);
		String completeXml = FileUtils.readFileToString(new File("dd4t-test/target/classes/xml-without-java-xslt.xml"));
//
		String homepage = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
		//System.out.println(completeXml);
		//deserializeXmlJackson(completeXml);
		deserializeJson(homepage);


	}

//	private static void deserializeXmlJackson (final String completeXml) throws IOException {
//		JacksonXmlModule module = new JacksonXmlModule();
//		ObjectMapper mapper = new XmlMapper(module);
//		mapper.registerModule(configureXmlMapper());
//		PageImpl page = mapper.readValue(completeXml, PageImpl.class);
//
//		System.out.println(page.getTitle());
//	}

//	protected static SimpleModule configureXmlMapper () {
//		// TODO: to concrete basefield and customizable CT impls and into DataBinder
//		final XmlBaseFieldsDeserializer<BaseField> xmlBaseFieldsDeserializer = new XmlBaseFieldsDeserializer<>(BaseField.class);
//		final ComponentPresentationDeserializer componentPresentationDeserializer = new ComponentPresentationDeserializer(ComponentPresentationImpl.class, ComponentTemplateImpl.class, ComponentImpl.class);
//		final XmlValuesDeserializer<List> xmlValuesDeserializer = new XmlValuesDeserializer<>(List.class);
//		final SimpleModule module = new SimpleModule();
//		module.addDeserializer(BaseField.class, xmlBaseFieldsDeserializer);
//		module.addDeserializer(ComponentPresentation.class,componentPresentationDeserializer);
//		module.addDeserializer(List.class,xmlValuesDeserializer);
//		return module;
//
//	}

	// TODO: OrderOnPage always is 0 in the JSon

	private static void deserializeJson (String content) throws IOException, SerializationException {
		String content1 = CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(content));//test4;//

		PageImpl page = DataBindFactory.buildPage(content1, PageImpl.class);
		System.out.println(content1);
		System.out.println("Page Title: " + page.getTitle());
	}

	private static String decodeAndDecompressContent (String content) throws IOException {
		byte[] decoded;
		if (Base64.isBase64(content)) {
			System.out.println(">> length before base64 decode: " + content.getBytes("UTF-8").length);

			decoded = Base64.decodeBase64(content);
			System.out.println(">> length after base64 decode: " + decoded.length);
		} else {
			decoded = content.getBytes();
		}

		String r = decompress(decoded);

		System.out.println(">> length after decompress: " + r.getBytes("UTF-8").length);
		System.out.println("Content is: " + r);
		return r;
	}

	public static String decompress (byte[] bytes) throws IOException {
		GZIPInputStream gis = null;
		String result = null;

		try {
			gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
			result = IOUtils.toString(gis);
		} finally {
			IOUtils.closeQuietly(gis);
		}
		return result;
	}

	static String[] splitBuffer (String input, int maxLength) {
		int elements = (input.length() + maxLength - 1) / maxLength;
		String[] ret = new String[elements];
		for (int i = 0; i < elements; i++) {
			int start = i * maxLength;
			ret[i] = input.substring(start, Math.min(input.length(), start + maxLength));
		}
		return ret;
	}
}
