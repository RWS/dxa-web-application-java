package org.dd4t.test;
/**
 * Created by rai on 03/06/14.
 */

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.DataBindFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class DD4TModelConverter {
	static String test3 = "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";
	static String testPage2 = "H4sIAAAAAAAEANVYbW+jRhD+K4hPrWQcXgy2+VLVeWlOzZtimqt0OlVrmNh7hcWC5XJu5P/eWcCwi53kUqeNK1mJPDM7+8zsM49hH/Vb+EpzmrITwkH3ddu0BobpGZYdWKbvuL416lvuUO/pZzQGRhIRlIf4/YLk/KaYxTRfQFSvNk3TMspPYJp++cHIGzKHAJJlXAY9lplOv3FgYl9cteBJjGFPIbECy/OdsW+b/dFIILkETiLCyRmFOMpFxq8UHq5KbI969b819fQ7EhclbE5YRLJoY8Kln1rj555+VSSQ0bBxokmACWgCsu2Csj8hOk6TZcqAcdlVQgpWS9zO7Om/wuohzaLSt16jN40jyATIsnEh4Vjuhwih8TDxTcNyDAvBNRb8bg8MG00B5bEoQbRS2/Qy1zGnlEkk3s62WeqapnacslzUqE0gntMi0X44vfpRZLl+YJTNn841UlLZmOoEcjpnYq0C17aQOiMpdrrp73rHwaGtaeNNBjn+K3cXHXtsXQLM99PtGUbbvuMJRo9cBwOn4QISIpLfpik/jSHBvWr65MUMGwHzNFt1T8R1DaVAKXJdM6AkJVMJyVQyHpNM4aH4/i9Q8Pcbwhc1eDx9HDp+FBY5TxNfKnFjqjEqvO3pCyARskOqZWNpy/m4gAy0KNVWaaE9EMY1nmozwL8RWf2kVPpC6Ls3oS2u0wekVUFiRWNKQ9uFhiKeUrFk3r+6NxgF0wusoW8OfXvQH5vDF0ahYBHcUwZRdxDssTIId1U35Bl4aeabE7osYo71R7TE8FuGXdaPaIJqlx8tKWFpQpjh/IE7V33sf1mKA7rENVUOvQw++rKEef1bVaNvV9drur8+lXVK/8JoxzKdQU//SCPBF8R1DnS+QP0xBeqKJ3RzQHeQVSmsrkB4smKL/TUEoK9VXrqv5WXDtg4tKbtPs6TUzSpzw8+upyXqBBieKc8VmjbGdx/BbeCdmuEbz8iHNkoqesvVVi1X+yY1niYziCKIpOGMMTpADBKixtQiETk1YdN+Vo5Atr/7KXQ7+cn6vHFJJXVORng6te8QSG+3QHoHIpDVM+dg7LvD/th56VlhVs3N1pPbQJHHerr+gT5ar9Aez9veErnU0R7vjanQpcFamo3nOgd1zEWVQ22fpbRvk04r91sr5Qz2LWebxsssjYqQTyGGkKeZxOiu57DlZfKEvEzeX146jXxLdRnuVpfhIamLa/oDpz8eH7K6OF11Ge5Ql8ne6vI8E/5v4rItEN17gD1Pxe4+b8qn0nkdbdNKFzDXBV8W/KxUP1xyHlxeaGcZmYvmfcdFjGvhj+JeFzEtxnN82QL1TXjbe6BXM45yNdNsetD3M+7YcOzddNEWVbfFkvxkxUhCQ92/J3EOCCBD1zUT90/ibQj7OeUZsrzI4JcsLZav6tzANgYShluYZ5ALxmHr8lLDnlOk/6ajr8mlauTY8OTq8lBf/w05djnIZxUAAA==";
	static String testPage = "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";

	public static void main (String[] args) throws IOException, XMLStreamException, SerializationException {

		// Load Spring
		ApplicationContext context = new FileSystemXmlApplicationContext("dd4t-test/target/classes/application-context.xml");



//		System.out.println(testXml + xml2 + xml3);
//		String complete = (withItemsXml+withItemsXml2+withItemsXml3);
//

		// deserializeXmlJackson(complete);
		deserializeJson();


	}

	private static void deserializeXmlJackson (final String complete) throws IOException {
		JacksonXmlModule module = new JacksonXmlModule();
// to default to using "unwrapped" Lists:

//		//SimpleModule module = new SimpleModule("XmlBaseFieldDeserializerModule", new Version(1, 0, 0, "RELEASE", "org.dd4t", "core"));
//		XmlBaseFieldDeserializer deserializer = new XmlBaseFieldDeserializer();
//
//		module.addDeserializer(BaseField.class, deserializer);
//
//		ObjectMapper mapper = new XmlMapper(module);



		//mapper.registerModule(module);
// and then configure, for example:
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//		Field f = mapper.readValue(field,BaseField.class);
//		FieldSet set = mapper.readValue(multipleFields,FieldSetImpl.class);

		//f.getValues();

		//complete.replaceAll("<item>", "").replaceAll("</item>", "").replaceAll("(?s)<key>.*?</key>", "").replaceAll("<value>", "").replaceAll("</value>", "");
		//PageImpl page = mapper.readValue(complete,PageImpl.class);
	}

	// TODO: OrderOnPage always is 0 in the JSon

	private static void deserializeJson () throws IOException, SerializationException {
		String content = decodeAndDecompressContent(test3);

		PageImpl page = DataBindFactory.buildPage(content, PageImpl.class);
		System.out.println(content);
		System.out.println("Page Title: " + page.getTitle());
	}

	private static String decodeAndDecompressContent (String content) throws IOException {
		byte[] decoded;
		if (Base64.isBase64(content)) {
			System.out.println(">> length before base64 decode: " + content.getBytes().length);

			decoded = Base64.decodeBase64(content);
			System.out.println(">> length after base64 decode: " + decoded.length);
		} else {
			decoded = content.getBytes();
		}

		String r = decompress(decoded);

		System.out.println(">> length after decompress: " + r.getBytes().length);
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

	static String xml2 = "<ComponentType>Multimedia</ComponentType>                    <Multimedia>                      <Url>/Preview/media/wine-bottle-medium_tcm17-4919.jpg</Url>                      <MimeType>image/jpeg</MimeType>                      <FileName>wine-bottle-medium.jpg</FileName>                      <FileExtension>jpg</FileExtension>                      <Size>36642</Size>                      <Width>0</Width>                      <Height>0</Height>                    </Multimedia>                    <Folder>                      <Id>tcm:17-1405-2</Id>                      <Title>ADC Evora</Title>                      <PublicationId>tcm:0-17-1</PublicationId>                    </Folder>                    <Categories />                    <Version>1</Version>                  </Component>                </LinkedComponentValues>                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>link</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Content/custom:Teaser/custom:link\">                <Name>link</Name>                <Values />                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues>                  <FieldSet>                    <item>                      <key>                        <string>internalLink</string>                      </key>                      <value>                        <Field FieldType=\"ComponentLink\" XPath=\"tcm:Content/custom:Teaser/custom:link[1]/custom:internalLink\">                          <Name>internalLink</Name>                          <Values>                            <string>tcm:17-4962</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues>                            <Component>                              <Id>tcm:17-4962</Id>                              <Title>Food &amp; Drink</Title>                              <Publication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </Publication>                              <OwningPublication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </OwningPublication>                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                              <RevisionDate>2014-09-10T17:42:27.14</RevisionDate>                              <Schema>                                <Id>tcm:17-139-8</Id>                                <Title>Item List</Title>                                <Publication>                                  <Id>tcm:0-17-1</Id>                                  <Title>500 adcevora.com</Title>                                </Publication>                                <Folder>                                  <Id>tcm:17-11-2</Id>                                  <Title>Schemas</Title>                                  <PublicationId>tcm:0-17-1</PublicationId>                                </Folder>                                <RootElementName>ItemList</RootElementName>                              </Schema>                              <Fields>                                <item>                                  <key>                                    <string>headline</string>                                  </key>                                  <value>                                    <Field FieldType=\"Text\" XPath=\"tcm:Content/custom:ItemList/custom:headline\">                                      <Name>headline</Name>                                      <Values>                                        <string>Food &amp; Drink</string>                                      </Values>                                      <NumericValues />                                      <DateTimeValues />                                      <LinkedComponentValues />                                      <EmbeddedValues />                                      <Keywords />                                    </Field>                                  </value>                                </item>                                <item>                                  <key>                                    <string>itemListElement</string>                                  </key>                                  <value>                                    <Field FieldType=\"Embedded\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement\">                                      <Name>itemListElement</Name>                                      <Values />                                      <NumericValues />                                      <DateTimeValues />                                      <LinkedComponentValues />                                      <EmbeddedValues>                                        <FieldSet>                                          <item>                                            <key>                                              <string>subheading</string>                                            </key>                                            <value>                                              <Field FieldType=\"Text\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:subheading\">                                                <Name>subheading</Name>                                                <Values>                                                  <string>Breakfast</string>                                                </Values>                                                <NumericValues />                                                <DateTimeValues />                                                <LinkedComponentValues />                                                <EmbeddedValues />                                                <Keywords />                                              </Field>                                            </value>                                          </item>                                          <item>                                            <key>                                              <string>content</string>                                            </key>                                            <value>                                              <Field FieldType=\"Xhtml\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:content\">                                                <Name>content</Name>                                                <Values>                                                  <string>&lt;p&gt;&lt;img xlink:href=\"tcm:17-4959\" title=\"Pies\" alt=\"Pies\" class=\"pull-right\" xlink:title=\"Pies\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" src=\"/Preview/media/food-tarte-1-childs_rooms_tcm17-4959.jpg\" /&gt;&lt;/p&gt;&lt;p&gt;Sharing a meal is a great way to get to know someone. Perhaps the single best service we offer is our exceptional all-inclusive breakfast. It’s the one time when nearly every guest gathers each morning to enjoy a common experience. We feature fresh, organic, and locally-grown products in abundant supply. Our menu rotates seasonally—but our delicious pancakes and free-range eggs are forever!&lt;/p&gt;&lt;p&gt;We serve breakfast every day from 8:00 A.M. to 10:30 A.M. including varieties of organic coffees, teas, and fresh squeezed juices. If you would like breakfast before 8 or after 10:30, just try to tell us a little bit in advance. We can also serve you breakfast in your room if you’d like. You can show up any time you want for a cup of organic coffee.&lt;/p&gt;&lt;p&gt;Breakfast in Portuguese is pequeno-almoço, which literally translates to “small lunch.” Enjoy yours indoors or outside in our courtyard.&lt;/p&gt;&lt;p&gt;View our current breakfast selection here:&lt;/p&gt;</string>                                                </Values>                                                <NumericValues />                                                <DateTimeValues />                                                <LinkedComponentValues />                                                <EmbeddedValues />                                                <Keywords />                                              </Field>                                            </value>                                          </item>                                          <item>                                            <key>                                              <string>media</string>                                            </key>                                            <value>                                              <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:media\">                                                <Name>media</Name>                                                <Values>                                                  <string>tcm:17-4965</string>                                                </Values>                                                <NumericValues />                                                <DateTimeValues />                                                <LinkedComponentValues>                                                  <Component>                                                    <Id>tcm:17-4965</Id>                                                    <Title>Breakfast Menu</Title>                                                    <Publication>                                                      <Id>tcm:0-17-1</Id>                                                      <Title>500 adcevora.com</Title>                                                    </Publication>                                                    <OwningPublication>                                                      <Id>tcm:0-17-1</Id>                                                      <Title>500 adcevora.com</Title>                                                    </OwningPublication>                                                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                                                    <RevisionDate>2014-08-25T15:19:29.817</RevisionDate>                                                    <Schema>                                                      <Id>tcm:17-4465-8</Id>                                                      <Title>Download</Title>                                                      <Publication>                                                        <Id>tcm:0-17-1</Id>                                                        <Title>500 adcevora.com</Title>                                                      </Publication>                                                      <Folder>                                                        <Id>tcm:17-11-2</Id>                                                        <Title>Schemas</Title>                                                        <PublicationId>tcm:0-17-1</PublicationId>                                                      </Folder>                                                      <RootElementName>undefined</RootElementName>                                                    </Schema>                                                    <Fields />                                                    <MetadataFields />                                                    <ComponentType>Multimedia</ComponentType>                                                    <Multimedia>                                                      <Url>/Preview/media/breakfast-menu_tcm17-4965.pdf</Url>                                                      <MimeType>application/pdf</MimeType>                                                      <FileName>breakfast-menu.pdf</FileName>                                                      <FileExtension>pdf</FileExtension>                                                      <Size>446104</Size>                                                      <Width>0</Width>                                                      <Height>0</Height>                                                    </Multimedia>                                                    <Folder>                                                      <Id>tcm:17-667-2</Id>                                                      <Title>Downloads</Title>                                                      <PublicationId>tcm:0-17-1</PublicationId>                                                    </Folder>                                                    <Categories />                                                    <Version>1</Version>                                                  </Component>                                                </LinkedComponentValues>                                                <EmbeddedValues />                                                <Keywords />                                              </Field>                                            </value>                                          </item>                                        </FieldSet>                                        <FieldSet>                                          <item>                                            <key>                                              <string>subheading</string>                                            </key>                                            <value>                                              <Field FieldType=\"Text\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:subheading\">                                                <Name>subheading</Name>                                                <Values>                                                  <string>MóBar</string>                                                </Values>                                                <NumericValues />                                                <DateTimeValues />                                                <LinkedComponentValues />                                                <EmbeddedValues />                                                <Keywords />                                              </Field>                                            </value>                                          </item>                                          <item>                                            <key>                                              <string>content</string>                                            </key>                                            <value>                                              <Field FieldType=\"Xhtml\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:content\">                                                <Name>content</Name>                                                <Values>                                                  <string>&lt;p&gt;&lt;img title=\"Wine Closeup\" alt=\"Wine Closeup\" class=\"pull-left\" xlink:href=\"tcm:17-4960\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:title=\"Wine Closeup\" src=\"/Preview/media/detail-wine-bottle2-childs_rooms_tcm17-4960.jpg\" /&gt;&lt;/p&gt;&lt;p&gt;All of your favorite drinks are available anytime from our MóBar. We have sampled many of the wines from the Alentejo and have picked our favorites. Consider an aperitif or digestif of an aged port or Portuguese aguardente. Or try one of our signature cocktails—we make a more than decent gin and tonic and, given our Brazilian roots, we make a great caipirinha. We will always try to make your favorite drink – even if you have to teach us how to make it!&lt;/p&gt;&lt;p&gt;If what you want is not on the menu, please ask, there is always the chance we have what you are looking for.&lt;/p&gt;&lt;p&gt;&lt;br /&gt;Lunch and In-between&lt;/p&gt;&lt;p&gt;One of our best kept secrets is our hand-selected menu of homemade tapas—served whenever you wish. Seasonal soup, regional cheeses, exceptional sandwiches, healthy salads, Portuguese presunto, olives, and bread can be combined in the most delightful ways which we invite you to enjoy anytime, anywhere —we’ll even pack a lunch for you if you like.&lt;/p&gt;&lt;p&gt;The name MóBar&lt;/p&gt;&lt;p&gt;MóBar is a a play on the Portuguese word for millstone (the big round stone you see in our courtyard that was used to crush olives) - mó)&lt;/p&gt;</string>                                                </Values>                                                <NumericValues />                                                <DateTimeValues />                                                <LinkedComponentValues />                                                <EmbeddedValues />                                                <Keywords />                                              </Field>                                            </value>                                          </item>                                          <item>                                            <key>                                              <string>media</string>                                            </key>                                            <value>                                              <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:media\">                                                <Name>media</Name>                                                <Values>                                                  <string>tcm:17-4964</string>                                                </Values>                                                <NumericValues />                                                <DateTimeValues />                                                <LinkedComponentValues>                                                  <Component>                                                    <Id>tcm:17-4964</Id>                                                    <Title>Bar Menu</Title>                                                    <Publication>                                                      <Id>tcm:0-17-1</Id>                                                      <Title>500 adcevora.com</Title>                                                    </Publication>                                                    <OwningPublication>                                                      <Id>tcm:0-17-1</Id>                                                      <Title>500 adcevora.com</Title>                                                    </OwningPublication>                                                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                                                    <RevisionDate>2014-08-25T15:19:00.783</RevisionDate>                                                    <Schema>                                                      <Id>tcm:17-4465-8</Id>                                                      <Title>Download</Title>                                                      <Publication>                                                        <Id>tcm:0-17-1</Id>                                                        <Title>500 adcevora.com</Title>                                                      </Publication>                                                      <Folder>                                                        <Id>tcm:17-11-2</Id>                                                        <Title>Schemas</Title>                                                        <PublicationId>tcm:0-17-1</PublicationId>                                                      </Folder>                                                      <RootElementName>undefined</RootElementName>                                                    </Schema>                                                    <Fields />                                                    <MetadataFields />                                                    <ComponentType>Multimedia</ComponentType>                                                    <Multimedia>                                                      <Url>/Preview/media/bar-menu_tcm17-4964.pdf</Url>                                                      <MimeType>application/pdf</MimeType>                                                      <FileName>bar-menu.pdf</FileName>                                                      <FileExtension>pdf</FileExtension>                                                      <Size>161661</Size>                                                      <Width>0</Width>                                                      <Height>0</Height>                                                    </Multimedia>                                                    <Folder>                                                      <Id>tcm:17-667-2</Id>                                                      <Title>Downloads</Title>                                                      <PublicationId>tcm:0-17-1</PublicationId>                                                    </Folder>                                                    <Categories />                                                    <Version>1</Version>                                                  </Component>                                                </LinkedComponentValues>                                                <EmbeddedValues />                                                <Keywords />                                              </Field>                                            </value>                                          </item>                                        </FieldSet>                                      </EmbeddedValues>                                      <EmbeddedSchema>                                        <Id>tcm:17-3275-8</Id>                                        <Title>Item List Element</Title>                                        <Publication>                                          <Id>tcm:0-17-1</Id>                                          <Title>500 adcevora.com</Title>                                        </Publication>                                        <Folder>                                          <Id>tcm:17-27-2</Id>                                          <Title>Embedded</Title>                                          <PublicationId>tcm:0-17-1</PublicationId>                                        </Folder>                                        <RootElementName>LinkedContent</RootElementName>                                      </EmbeddedSchema>                                      <Keywords />                                    </Field>                                  </value>                                </item>                              </Fields>                              <MetadataFields />                              <ComponentType>Normal</ComponentType>                              <Folder>                                <Id>tcm:17-1410-2</Id>                                <Title>More...</Title>                                <PublicationId>tcm:0-17-1</PublicationId>                              </Folder>                              <Categories />                              <Version>11</Version>                            </Component>                          </LinkedComponentValues>                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                  </FieldSet>                </EmbeddedValues>                <EmbeddedSchema>                  <Id>tcm:17-115-8</Id>                  <Title>Link</Title>                  <Publication>                    <Id>tcm:0-17-1</Id>                    <Title>500 adcevora.com</Title>                  </Publication>                  <Folder>                    <Id>tcm:17-27-2</Id>                    <Title>Embedded</Title>                    <PublicationId>tcm:0-17-1</PublicationId>                  </Folder>                  <RootElementName>EmbeddedLink</RootElementName>                </EmbeddedSchema>                <Keywords />              </Field>            </value>          </item>        </Fields>        <MetadataFields />        <ComponentType>Normal</ComponentType>        <Folder>          <Id>tcm:17-661-2</Id>          <Title>Homepage</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>        <Categories />        <Version>3</Version>      </Component>      <ComponentTemplate>        <Id>tcm:17-130-32</Id>        <Title>Teaser [3-Column]</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OutputFormat>HTML Fragment</OutputFormat>        <RevisionDate>2014-08-01T16:11:58.627</RevisionDate>        <MetadataFields>          <item>            <key>              <string>view</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>view</Name>                <Values>                  <string>Teaser</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>regionView</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>regionView</Name>                <Values>                  <string>3-Column</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>        </MetadataFields>        <Folder>          <Id>tcm:17-12-2</Id>          <Title>Templates</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>      </ComponentTemplate>      <IsDynamic>false</IsDynamic>    </ComponentPresentation>    <ComponentPresentation>      <Component>        <Id>tcm:17-4922</Id>        <Title>Day Trips Teaser</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OwningPublication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </OwningPublication>        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>        <RevisionDate>2014-09-10T16:47:46.613</RevisionDate>        <Schema>          <Id>tcm:17-117-8</Id>          <Title>Teaser</Title>          <Publication>            <Id>tcm:0-17-1</Id>            <Title>500 adcevora.com</Title>          </Publication>          <Folder>            <Id>tcm:17-11-2</Id>            <Title>Schemas</Title>            <PublicationId>tcm:0-17-1</PublicationId>          </Folder>          <RootElementName>Teaser</RootElementName>        </Schema>        <Fields>          <item>            <key>              <string>headline</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:headline\">                <Name>headline</Name>                <Values>                  <string>Day Trips</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>content</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:content\">                <Name>content</Name>                <Values>                  <string>Use our hotel as a base for exploring the Alentejo. Light traffic and worth the drive.</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>media</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:Teaser/custom:media\">                <Name>media</Name>                <Values>                  <string>tcm:17-4921</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues>                  <Component>                    <Id>tcm:17-4921</Id>                    <Title>City Wall</Title>                    <Publication>                      <Id>tcm:0-17-1</Id>                      <Title>500 adcevora.com</Title>                    </Publication>                    <OwningPublication>                      <Id>tcm:0-17-1</Id>                      <Title>500 adcevora.com</Title>                    </OwningPublication>                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                    <RevisionDate>2014-08-25T13:55:56.22</RevisionDate>                    <Schema>                      <Id>tcm:17-79-8</Id>                      <Title>Image</Title>                      <Publication>                        <Id>tcm:0-17-1</Id>                        <Title>500 adcevora.com</Title>                      </Publication>                      <Folder>                        <Id>tcm:17-11-2</Id>                        <Title>Schemas</Title>                        <PublicationId>tcm:0-17-1</PublicationId>                      </Folder>                      <RootElementName>undefined</RootElementName>                    </Schema>                    <Fields />                    <MetadataFields />                    <ComponentType>Multimedia</ComponentType>                    <Multimedia>                      <Url>/Preview/media/outside-marvao-view-medium_tcm17-4921.jpg</Url>                      <MimeType>image/jpeg</MimeType>                      <FileName>outside-marvao-view-medium.jpg</FileName>                      <FileExtension>jpg</FileExtension>                      <Size>34347</Size>                      <Width>0</Width>                      <Height>0</Height>                    </Multimedia>                    <Folder>                      <Id>tcm:17-1405-2</Id>                      <Title>ADC Evora</Title>                      <PublicationId>tcm:0-17-1</PublicationId>                    </Folder>                    <Categories />                    <Version>1</Version>                  </Component>                </LinkedComponentValues>                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>        </Fields>        <MetadataFields />        <ComponentType>Normal</ComponentType>        <Folder>          <Id>tcm:17-661-2</Id>          <Title>Homepage</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>        <Categories />        <Version>2</Version>      </Component>      <ComponentTemplate>        <Id>tcm:17-130-32</Id>        <Title>Teaser [3-Column]</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OutputFormat>HTML Fragment</OutputFormat>        <RevisionDate>2014-08-01T16:11:58.627</RevisionDate>        <MetadataFields>          <item>            <key>              <string>view</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>view</Name>                <Values>                  <string>Teaser</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />";
	static String xml3 = "</Field>            </value>          </item>          <item>            <key>              <string>regionView</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>regionView</Name>                <Values>                  <string>3-Column</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>        </MetadataFields>        <Folder>          <Id>tcm:17-12-2</Id>          <Title>Templates</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>      </ComponentTemplate>      <IsDynamic>false</IsDynamic>    </ComponentPresentation>    <ComponentPresentation>      <Component>        <Id>tcm:17-4924</Id>        <Title>Wine Teaser</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OwningPublication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </OwningPublication>        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>        <RevisionDate>2014-08-25T13:57:34.313</RevisionDate>        <Schema>          <Id>tcm:17-117-8</Id>          <Title>Teaser</Title>          <Publication>            <Id>tcm:0-17-1</Id>            <Title>500 adcevora.com</Title>          </Publication>          <Folder>            <Id>tcm:17-11-2</Id>            <Title>Schemas</Title>            <PublicationId>tcm:0-17-1</PublicationId>          </Folder>          <RootElementName>Teaser</RootElementName>        </Schema>        <Fields>          <item>            <key>              <string>headline</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:headline\">                <Name>headline</Name>                <Values>                  <string>Wine Touring</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>content</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:content\">                <Name>content</Name>                <Values>                  <string>50 wineries within 50km</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>media</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:Teaser/custom:media\">                <Name>media</Name>                <Values>                  <string>tcm:17-4923</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues>                  <Component>                    <Id>tcm:17-4923</Id>                    <Title>Wine Barrels</Title>                    <Publication>                      <Id>tcm:0-17-1</Id>                      <Title>500 adcevora.com</Title>                    </Publication>                    <OwningPublication>                      <Id>tcm:0-17-1</Id>                      <Title>500 adcevora.com</Title>                    </OwningPublication>                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                    <RevisionDate>2014-08-25T13:57:27.23</RevisionDate>                    <Schema>                      <Id>tcm:17-79-8</Id>                      <Title>Image</Title>                      <Publication>                        <Id>tcm:0-17-1</Id>                        <Title>500 adcevora.com</Title>                      </Publication>                      <Folder>                        <Id>tcm:17-11-2</Id>                        <Title>Schemas</Title>                        <PublicationId>tcm:0-17-1</PublicationId>                      </Folder>                      <RootElementName>undefined</RootElementName>                    </Schema>                    <Fields />                    <MetadataFields />                    <ComponentType>Multimedia</ComponentType>                    <Multimedia>                      <Url>/Preview/media/outside-barrels-esquila-medium_tcm17-4923.jpg</Url>                      <MimeType>image/jpeg</MimeType>                      <FileName>outside-barrels-esquila-medium.jpg</FileName>                      <FileExtension>jpg</FileExtension>                      <Size>35066</Size>                      <Width>0</Width>                      <Height>0</Height>                    </Multimedia>                    <Folder>                      <Id>tcm:17-1405-2</Id>                      <Title>ADC Evora</Title>                      <PublicationId>tcm:0-17-1</PublicationId>                    </Folder>                    <Categories />                    <Version>1</Version>                  </Component>                </LinkedComponentValues>                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>        </Fields>        <MetadataFields />        <ComponentType>Normal</ComponentType>        <Folder>          <Id>tcm:17-661-2</Id>          <Title>Homepage</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>        <Categories />        <Version>1</Version>      </Component>      <ComponentTemplate>        <Id>tcm:17-130-32</Id>        <Title>Teaser [3-Column]</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OutputFormat>HTML Fragment</OutputFormat>        <RevisionDate>2014-08-01T16:11:58.627</RevisionDate>        <MetadataFields>          <item>            <key>              <string>view</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>view</Name>                <Values>                  <string>Teaser</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>regionView</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>regionView</Name>                <Values>                  <string>3-Column</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>        </MetadataFields>        <Folder>          <Id>tcm:17-12-2</Id>          <Title>Templates</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>      </ComponentTemplate>      <IsDynamic>false</IsDynamic>    </ComponentPresentation>  </ComponentPresentations>  <StructureGroup>    <Id>tcm:17-3-4</Id>    <Title>Home</Title>    <PublicationId>tcm:0-17-1</PublicationId>  </StructureGroup>  <Categories />  <Version>17</Version></Page>";


	static String multipleFields = "        <Fields>\n" +
//			"          <item>\n" +
//			"            <key>\n" +
//			"              <string>Body</string>\n" +
//			"            </key>\n" +
//			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiLineText\" XPath=\"tcm:Content/custom:GeneralContent/custom:Body\">\n" +
			"                <Name>Body</Name>\n" +
			"                <Values>\n" +
			"                  <string>BLAAAA</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
//			"            </value>\n" +
//			"          </item>\n" +
//			"          <item>\n" +
//			"            <key>\n" +
//			"              <string>Body</string>\n" +
//			"            </key>\n" +
//			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiLineText\" XPath=\"tcm:Content/custom:GeneralContent/custom:Body\">\n" +
			"                <Name>ANOTHER BODY</Name>\n" +
			"                <Values>\n" +
			"                  <string>YEPYEP</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
//			"            </value>\n" +
//			"          </item>\n" +
			"        </Fields>";
	static String field = " <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:link\">            <Name>includes</Name>            <Values>              <string>system/include/header</string>              <string>system/include/footer</string>            </Values>            <NumericValues />            <DateTimeValues />            <LinkedComponentValues />            <Keywords />          </Field>  ";
	static String testXml = "<Page><Id>tcm:17-1677-64</Id>  <Title>000 Home</Title>  <Publication>    <Id>tcm:0-17-1</Id>    <Title>500 adcevora.com</Title>  </Publication>  <OwningPublication>    <Id>tcm:0-17-1</Id>    <Title>500 adcevora.com</Title>  </OwningPublication>  <RevisionDate>2014-09-18T18:37:09.46</RevisionDate>  <Filename>index</Filename>  <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>  <PageTemplate>    <Id>tcm:17-131-128</Id>    <Title>Home Page</Title>    <Publication>      <Id>tcm:0-17-1</Id>      <Title>500 adcevora.com</Title>    </Publication>    <FileExtension>html</FileExtension>    <RevisionDate>2014-06-25T14:37:12.393</RevisionDate>    <MetadataFields>      <item>        <key>          <string>includes</string>        </key>        <value>          <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:link\">            <Name>includes</Name>            <Values>              <string>system/include/header</string>              <string>system/include/footer</string>            </Values>            <NumericValues />            <DateTimeValues />            <LinkedComponentValues />            <Keywords />          </Field>        </value>      </item>      <item>        <key>          <string>view</string>        </key>        <value>          <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">            <Name>view</Name>            <Values>              <string>GeneralPage</string>            </Values>            <NumericValues />            <DateTimeValues />            <LinkedComponentValues />            <Keywords />          </Field>        </value>      </item>    </MetadataFields>    <Folder>      <Id>tcm:17-12-2</Id>      <Title>Templates</Title>      <PublicationId>tcm:0-17-1</PublicationId>    </Folder>  </PageTemplate>  <MetadataFields />  <ComponentPresentations>    <ComponentPresentation>      <Component>        <Id>tcm:17-1672</Id>        <Title>Homepage List</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OwningPublication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </OwningPublication>        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>        <RevisionDate>2014-10-13T10:51:27.15</RevisionDate>        <Schema>          <Id>tcm:17-139-8</Id>          <Title>Item List</Title>          <Publication>            <Id>tcm:0-17-1</Id>            <Title>500 adcevora.com</Title>          </Publication>          <Folder>            <Id>tcm:17-11-2</Id>            <Title>Schemas</Title>            <PublicationId>tcm:0-17-1</PublicationId>          </Folder>          <RootElementName>ItemList</RootElementName>        </Schema>        <Fields>          <item>            <key>              <string>itemListElement</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement\">                <Name>itemListElement</Name>                <Values />                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues>                  <FieldSet>                    <item>                      <key>                        <string>media</string>                      </key>                      <value>                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:media\">                          <Name>media</Name>                          <Values>                            <string>tcm:17-4907</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues>                            <Component>                              <Id>tcm:17-4907</Id>                              <Title>Esplanade</Title>                              <Publication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </Publication>                              <OwningPublication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </OwningPublication>                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                              <RevisionDate>2014-08-25T10:58:37.237</RevisionDate>                              <Schema>                                <Id>tcm:17-79-8</Id>                                <Title>Image</Title>                                <Publication>                                  <Id>tcm:0-17-1</Id>                                  <Title>500 adcevora.com</Title>                                </Publication>                                <Folder>                                  <Id>tcm:17-11-2</Id>                                  <Title>Schemas</Title>                                  <PublicationId>tcm:0-17-1</PublicationId>                                </Folder>                                <RootElementName>undefined</RootElementName>                              </Schema>                              <Fields />                              <MetadataFields />                              <ComponentType>Multimedia</ComponentType>                              <Multimedia>                                <Url>/Preview/media/esplanade-angle_tcm17-4907.jpg</Url>                                <MimeType>image/jpeg</MimeType>                                <FileName>esplanade-angle.jpg</FileName>                                <FileExtension>jpg</FileExtension>                                <Size>406754</Size>                                <Width>0</Width>                                <Height>0</Height>                              </Multimedia>                              <Folder>                                <Id>tcm:17-1405-2</Id>                                <Title>ADC Evora</Title>                                <PublicationId>tcm:0-17-1</PublicationId>                              </Folder>                              <Categories />                              <Version>1</Version>                            </Component>                          </LinkedComponentValues>                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                  </FieldSet>                  <FieldSet>                    <item>                      <key>                        <string>media</string>                      </key>                      <value>                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:media\">                          <Name>media</Name>                          <Values>                            <string>tcm:17-4906</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues>                            <Component>                              <Id>tcm:17-4906</Id>                              <Title>Pink Orchids</Title>                              <Publication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </Publication>                              <OwningPublication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </OwningPublication>                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                              <RevisionDate>2014-08-25T10:58:22.353</RevisionDate>                              <Schema>                                <Id>tcm:17-79-8</Id>                                <Title>Image</Title>                                <Publication>                                  <Id>tcm:0-17-1</Id>                                  <Title>500 adcevora.com</Title>                                </Publication>                                <Folder>                                  <Id>tcm:17-11-2</Id>                                  <Title>Schemas</Title>                                  <PublicationId>tcm:0-17-1</PublicationId>                                </Folder>                                <RootElementName>undefined</RootElementName>                              </Schema>                              <Fields />                              <MetadataFields />                              <ComponentType>Multimedia</ComponentType>                              <Multimedia>                                <Url>/Preview/media/detail-orchids-pink_tcm17-4906.jpg</Url>                                <MimeType>image/jpeg</MimeType>                                <FileName>detail-orchids-pink.jpg</FileName>                                <FileExtension>jpg</FileExtension>                                <Size>377965</Size>                                <Width>0</Width>                                <Height>0</Height>                              </Multimedia>                              <Folder>                                <Id>tcm:17-1405-2</Id>                                <Title>ADC Evora</Title>                                <PublicationId>tcm:0-17-1</PublicationId>                              </Folder>                              <Categories />                              <Version>1</Version>                            </Component>                          </LinkedComponentValues>                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                  </FieldSet>                  <FieldSet>                    <item>                      <key>                        <string>media</string>                      </key>                      <value>                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[3]/custom:media\">                          <Name>media</Name>                          <Values>                            <string>tcm:17-4908</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues>                            <Component>                              <Id>tcm:17-4908</Id>                              <Title>Lamp</Title>                              <Publication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </Publication>                              <OwningPublication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </OwningPublication>                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                              <RevisionDate>2014-08-25T10:58:51.24</RevisionDate>                              <Schema>                                <Id>tcm:17-79-8</Id>                                <Title>Image</Title>                                <Publication>                                  <Id>tcm:0-17-1</Id>                                  <Title>500 adcevora.com</Title>                                </Publication>                                <Folder>                                  <Id>tcm:17-11-2</Id>                                  <Title>Schemas</Title>                                  <PublicationId>tcm:0-17-1</PublicationId>                                </Folder>                                <RootElementName>undefined</RootElementName>                              </Schema>                              <Fields />                              <MetadataFields />                              <ComponentType>Multimedia</ComponentType>                              <Multimedia>                                <Url>/Preview/media/outside-andorinha-by-lamp_tcm17-4908.jpg</Url>                                <MimeType>image/jpeg</MimeType>                                <FileName>outside-andorinha-by-lamp.jpg</FileName>                                <FileExtension>jpg</FileExtension>                                <Size>368370</Size>                                <Width>0</Width>                                <Height>0</Height>                              </Multimedia>                              <Folder>                                <Id>tcm:17-1405-2</Id>                                <Title>ADC Evora</Title>                                <PublicationId>tcm:0-17-1</PublicationId>                              </Folder>                              <Categories />                              <Version>1</Version>                            </Component>                          </LinkedComponentValues>                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                  </FieldSet>                  <FieldSet>                    <item>                      <key>                        <string>media</string>                      </key>                      <value>                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[4]/custom:media\">                          <Name>media</Name>                          <Values>                            <string>tcm:17-4909</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues>                            <Component>                              <Id>tcm:17-4909</Id>                              <Title>Headboard</Title>                              <Publication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </Publication>                              <OwningPublication>                                <Id>tcm:0-17-1</Id>                                <Title>500 adcevora.com</Title>                              </OwningPublication>                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                              <RevisionDate>2014-08-25T10:59:06.2</RevisionDate>                              <Schema>                                <Id>tcm:17-79-8</Id>                                <Title>Image</Title>                                <Publication>                                  <Id>tcm:0-17-1</Id>                                  <Title>500 adcevora.com</Title>                                </Publication>                                <Folder>                                  <Id>tcm:17-11-2</Id>                                  <Title>Schemas</Title>                                  <PublicationId>tcm:0-17-1</PublicationId>                                </Folder>                                <RootElementName>undefined</RootElementName>                              </Schema>                              <Fields />                              <MetadataFields />                              <ComponentType>Multimedia</ComponentType>                              <Multimedia>                                <Url>/Preview/media/premium-headboard_tcm17-4909.jpg</Url>                                <MimeType>image/jpeg</MimeType>                                <FileName>premium-headboard.jpg</FileName>                                <FileExtension>jpg</FileExtension>                                <Size>300386</Size>                                <Width>0</Width>                                <Height>0</Height>                              </Multimedia>                              <Folder>                                <Id>tcm:17-1405-2</Id>                                <Title>ADC Evora</Title>                                <PublicationId>tcm:0-17-1</PublicationId>                              </Folder>                              <Categories />                              <Version>1</Version>                            </Component>                          </LinkedComponentValues>                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                  </FieldSet>                </EmbeddedValues>                <EmbeddedSchema>                  <Id>tcm:17-3275-8</Id>                  <Title>Item List Element</Title>                  <Publication>                    <Id>tcm:0-17-1</Id>                    <Title>500 adcevora.com</Title>                  </Publication>                  <Folder>                    <Id>tcm:17-27-2</Id>                    <Title>Embedded</Title>                    <PublicationId>tcm:0-17-1</PublicationId>                  </Folder>                  <RootElementName>LinkedContent</RootElementName>                </EmbeddedSchema>                <Keywords />              </Field>            </value>          </item>        </Fields>        <MetadataFields />        <ComponentType>Normal</ComponentType>        <Folder>          <Id>tcm:17-661-2</Id>          <Title>Homepage</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>        <Categories />        <Version>14</Version>      </Component>      <ComponentTemplate>        <Id>tcm:17-141-32</Id>        <Title>Carousel [Hero]</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OutputFormat>HTML Fragment</OutputFormat>        <RevisionDate>2014-07-29T13:16:00.59</RevisionDate>        <MetadataFields>          <item>            <key>              <string>view</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>view</Name>                <Values>                  <string>Carousel</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>regionView</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>regionView</Name>                <Values>                  <string>Hero</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>        </MetadataFields>        <Folder>          <Id>tcm:17-12-2</Id>          <Title>Templates</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>      </ComponentTemplate>      <IsDynamic>false</IsDynamic>    </ComponentPresentation>    <ComponentPresentation>      <Component>        <Id>tcm:17-4918</Id>        <Title>Welcome</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OwningPublication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </OwningPublication>        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>        <RevisionDate>2014-08-25T13:59:42.237</RevisionDate>        <Schema>          <Id>tcm:17-80-8</Id>          <Title>Article</Title>          <Publication>            <Id>tcm:0-17-1</Id>            <Title>500 adcevora.com</Title>          </Publication>          <Folder>            <Id>tcm:17-11-2</Id>            <Title>Schemas</Title>            <PublicationId>tcm:0-17-1</PublicationId>          </Folder>          <RootElementName>Article</RootElementName>        </Schema>        <Fields>          <item>            <key>              <string>headline</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Article/custom:headline\">                <Name>headline</Name>                <Values>                  <string>WELCOME TO ADC HOTEL</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>articleBody</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Content/custom:Article/custom:articleBody\">                <Name>articleBody</Name>                <Values />                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues>                  <FieldSet>                    <item>                      <key>                        <string>content</string>                      </key>                      <value>                        <Field FieldType=\"Xhtml\" XPath=\"tcm:Content/custom:Article/custom:articleBody[1]/custom:content\">                          <Name>content</Name>                          <Values>                            <string>&lt;p&gt;Thanks for considering our hotel for your visit to Évora. Our purpose is to meet your needs, handle the details, and reveal our town and region to you in a way that only we can. We have created this website to help you determine if we are the right hotel for your stay, to help you determine how long you should stay, select the right room type and provide you with some ideas ahead of your visit.&lt;/p&gt;&lt;p&gt;Obviously, contact us if you have additional questions. We look forward to meeting you.&lt;/p&gt;&lt;p&gt;This website is new as of November 2013. If you have suggestions for improvement, we welcome and encourage your feedback.&lt;/p&gt;</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues />                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                  </FieldSet>                </EmbeddedValues>                <EmbeddedSchema>                  <Id>tcm:17-232-8</Id>                  <Title>Paragraph</Title>                  <Publication>                    <Id>tcm:0-17-1</Id>                    <Title>500 adcevora.com</Title>                  </Publication>                  <Folder>                    <Id>tcm:17-27-2</Id>                    <Title>Embedded</Title>                    <PublicationId>tcm:0-17-1</PublicationId>                  </Folder>                  <RootElementName>Paragraph</RootElementName>                </EmbeddedSchema>                <Keywords />              </Field>            </value>          </item>        </Fields>        <MetadataFields>          <item>            <key>              <string>standardMeta</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta\">                <Name>standardMeta</Name>                <Values />                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues>                  <FieldSet>                    <item>                      <key>                        <string>description</string>                      </key>                      <value>                        <Field FieldType=\"MultiLineText\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta[1]/custom:description\">                          <Name>description</Name>                          <Values>                            <string>Charm hotel inside the walls of Évora in Alentejo</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues />                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                    <item>                      <key>                        <string>name</string>                      </key>                      <value>                        <Field FieldType=\"Text\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta[1]/custom:name\">                          <Name>name</Name>                          <Values>                            <string>Home</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues />                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                    <item>                      <key>                        <string>introText</string>                      </key>                      <value>                        <Field FieldType=\"Text\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta[1]/custom:introText\">                          <Name>introText</Name>                          <Values>                            <string>Home page</string>                          </Values>                          <NumericValues />                          <DateTimeValues />                          <LinkedComponentValues />                          <EmbeddedValues />                          <Keywords />                        </Field>                      </value>                    </item>                  </FieldSet>                </EmbeddedValues>                <EmbeddedSchema>                  <Id>tcm:17-231-8</Id>                  <Title>Standard Metadata</Title>                  <Publication>                    <Id>tcm:0-17-1</Id>                    <Title>500 adcevora.com</Title>                  </Publication>                  <Folder>                    <Id>tcm:17-27-2</Id>                    <Title>Embedded</Title>                    <PublicationId>tcm:0-17-1</PublicationId>                  </Folder>                  <RootElementName>StandardMetadata</RootElementName>                </EmbeddedSchema>                <Keywords />              </Field>            </value>          </item>        </MetadataFields>        <ComponentType>Normal</ComponentType>        <Folder>          <Id>tcm:17-661-2</Id>          <Title>Homepage</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>        <Categories />        <Version>2</Version>      </Component>      <ComponentTemplate>        <Id>tcm:17-83-32</Id>        <Title>Article</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OutputFormat>HTML Fragment</OutputFormat>        <RevisionDate>2014-07-29T13:16:22.64</RevisionDate>        <MetadataFields>          <item>            <key>              <string>view</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">                <Name>view</Name>                <Values>                  <string>Article</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <Keywords />              </Field>            </value>          </item>        </MetadataFields>        <Folder>          <Id>tcm:17-12-2</Id>          <Title>Templates</Title>          <PublicationId>tcm:0-17-1</PublicationId>        </Folder>      </ComponentTemplate>      <IsDynamic>false</IsDynamic>    </ComponentPresentation>    <ComponentPresentation>      <Component>        <Id>tcm:17-4920</Id>        <Title>Food Teaser</Title>        <Publication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </Publication>        <OwningPublication>          <Id>tcm:0-17-1</Id>          <Title>500 adcevora.com</Title>        </OwningPublication>        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>        <RevisionDate>2014-08-25T15:13:07.807</RevisionDate>        <Schema>          <Id>tcm:17-117-8</Id>          <Title>Teaser</Title>          <Publication>            <Id>tcm:0-17-1</Id>            <Title>500 adcevora.com</Title>          </Publication>          <Folder>            <Id>tcm:17-11-2</Id>            <Title>Schemas</Title>            <PublicationId>tcm:0-17-1</PublicationId>          </Folder>          <RootElementName>Teaser</RootElementName>        </Schema>        <Fields>          <item>            <key>              <string>headline</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:headline\">                <Name>headline</Name>                <Values>                  <string>Food &amp; Drink</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>content</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:content\">                <Name>content</Name>                <Values>                  <string>Learn about our famous organic, local breakfast and our hand selected all day food options</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues />                <EmbeddedValues />                <Keywords />              </Field>            </value>          </item>          <item>            <key>              <string>media</string>            </key>            <value>              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:Teaser/custom:media\">                <Name>media</Name>                <Values>                  <string>tcm:17-4919</string>                </Values>                <NumericValues />                <DateTimeValues />                <LinkedComponentValues>                  <Component>                    <Id>tcm:17-4919</Id>                    <Title>Wine Bottle</Title>                    <Publication>                      <Id>tcm:0-17-1</Id>                      <Title>500 adcevora.com</Title>                    </Publication>                    <OwningPublication>                      <Id>tcm:0-17-1</Id>                      <Title>500 adcevora.com</Title>                    </OwningPublication>                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>                    <RevisionDate>2014-08-25T13:53:45.233</RevisionDate>                    <Schema>                      <Id>tcm:17-79-8</Id>                      <Title>Image</Title>                      <Publication>                        <Id>tcm:0-17-1</Id>                        <Title>500 adcevora.com</Title>                      </Publication>                      <Folder>                        <Id>tcm:17-11-2</Id>                        <Title>Schemas</Title>                        <PublicationId>tcm:0-17-1</PublicationId>                      </Folder>                      <RootElementName>undefined</RootElementName>                    </Schema>                    <Fields />                    <MetadataFields />                    ";

	static String withItemsXml = "<Page>" +
			"  <Id>tcm:17-1677-64</Id>\n" +
			"  <Title>000 Home</Title>\n" +
			"  <Publication>\n" +
			"    <Id>tcm:0-17-1</Id>\n" +
			"    <Title>500 adcevora.com</Title>\n" +
			"  </Publication>\n" +
			"  <OwningPublication>\n" +
			"    <Id>tcm:0-17-1</Id>\n" +
			"    <Title>500 adcevora.com</Title>\n" +
			"  </OwningPublication>\n" +
			"  <RevisionDate>2014-09-18T18:37:09.46</RevisionDate>\n" +
			"  <Filename>index</Filename>\n" +
			"  <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"  <PageTemplate>\n" +
			"    <Id>tcm:17-131-128</Id>\n" +
			"    <Title>Home Page</Title>\n" +
			"    <Publication>\n" +
			"      <Id>tcm:0-17-1</Id>\n" +
			"      <Title>500 adcevora.com</Title>\n" +
			"    </Publication>\n" +
			"    <FileExtension>html</FileExtension>\n" +
			"    <RevisionDate>2014-06-25T14:37:12.393</RevisionDate>\n" +
			"    <MetadataFields>\n" +
			"      <item>\n" +
			"        <key>\n" +
			"          <string>includes</string>\n" +
			"        </key>\n" +
			"        <value>\n" +
			"          <Field FieldType=\"Text\">\n" +
			"            <Name>includes</Name>\n" +
			"            <Values>\n" +
			"              <string>system/include/header</string>\n" +
			"              <string>system/include/footer</string>\n" +
			"            </Values>\n" +
			"            <NumericValues />\n" +
			"            <DateTimeValues />\n" +
			"            <LinkedComponentValues />\n" +
			"            <Keywords />\n" +
			"          </Field>\n" +
			"        </value>\n" +
			"      </item>\n" +
			"      <item>\n" +
			"        <key>\n" +
			"          <string>view</string>\n" +
			"        </key>\n" +
			"        <value>\n" +
			"          <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"            <Name>view</Name>\n" +
			"            <Values>\n" +
			"              <string>GeneralPage</string>\n" +
			"            </Values>\n" +
			"            <NumericValues />\n" +
			"            <DateTimeValues />\n" +
			"            <LinkedComponentValues />\n" +
			"            <Keywords />\n" +
			"          </Field>\n" +
			"        </value>\n" +
			"      </item>\n" +
			"    </MetadataFields>\n" +
			"    <Folder>\n" +
			"      <Id>tcm:17-12-2</Id>\n" +
			"      <Title>Templates</Title>\n" +
			"      <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"    </Folder>\n" +
			"  </PageTemplate>\n" +
			"  <MetadataFields />\n" +
			"  <ComponentPresentations>\n" +
			"    <ComponentPresentation>\n" +
			"      <Component>\n" +
			"        <Id>tcm:17-1672</Id>\n" +
			"        <Title>Homepage List</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OwningPublication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </OwningPublication>\n" +
			"        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"        <RevisionDate>2014-10-13T10:51:27.15</RevisionDate>\n" +
			"        <Schema>\n" +
			"          <Id>tcm:17-139-8</Id>\n" +
			"          <Title>Item List</Title>\n" +
			"          <Publication>\n" +
			"            <Id>tcm:0-17-1</Id>\n" +
			"            <Title>500 adcevora.com</Title>\n" +
			"          </Publication>\n" +
			"          <Folder>\n" +
			"            <Id>tcm:17-11-2</Id>\n" +
			"            <Title>Schemas</Title>\n" +
			"            <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"          </Folder>\n" +
			"          <RootElementName>ItemList</RootElementName>\n" +
			"        </Schema>\n" +
			"        <Fields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>itemListElement</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement\">\n" +
			"                <Name>itemListElement</Name>\n" +
			"                <Values />\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues>\n" +
			"                  <FieldSet>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>media</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:media\">\n" +
			"                          <Name>media</Name>\n" +
			"                          <Values>\n" +
			"                            <string>tcm:17-4907</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues>\n" +
			"                            <Component>\n" +
			"                              <Id>tcm:17-4907</Id>\n" +
			"                              <Title>Esplanade</Title>\n" +
			"                              <Publication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </Publication>\n" +
			"                              <OwningPublication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </OwningPublication>\n" +
			"                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                              <RevisionDate>2014-08-25T10:58:37.237</RevisionDate>\n" +
			"                              <Schema>\n" +
			"                                <Id>tcm:17-79-8</Id>\n" +
			"                                <Title>Image</Title>\n" +
			"                                <Publication>\n" +
			"                                  <Id>tcm:0-17-1</Id>\n" +
			"                                  <Title>500 adcevora.com</Title>\n" +
			"                                </Publication>\n" +
			"                                <Folder>\n" +
			"                                  <Id>tcm:17-11-2</Id>\n" +
			"                                  <Title>Schemas</Title>\n" +
			"                                  <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                </Folder>\n" +
			"                                <RootElementName>undefined</RootElementName>\n" +
			"                              </Schema>\n" +
			"                              <Fields />\n" +
			"                              <MetadataFields />\n" +
			"                              <ComponentType>Multimedia</ComponentType>\n" +
			"                              <Multimedia>\n" +
			"                                <Url>/Preview/media/esplanade-angle_tcm17-4907.jpg</Url>\n" +
			"                                <MimeType>image/jpeg</MimeType>\n" +
			"                                <FileName>esplanade-angle.jpg</FileName>\n" +
			"                                <FileExtension>jpg</FileExtension>\n" +
			"                                <Size>406754</Size>\n" +
			"                                <Width>0</Width>\n" +
			"                                <Height>0</Height>\n" +
			"                              </Multimedia>\n" +
			"                              <Folder>\n" +
			"                                <Id>tcm:17-1405-2</Id>\n" +
			"                                <Title>ADC Evora</Title>\n" +
			"                                <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                              </Folder>\n" +
			"                              <Categories />\n" +
			"                              <Version>1</Version>\n" +
			"                            </Component>\n" +
			"                          </LinkedComponentValues>\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                  </FieldSet>\n" +
			"                  <FieldSet>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>media</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:media\">\n" +
			"                          <Name>media</Name>\n" +
			"                          <Values>\n" +
			"                            <string>tcm:17-4906</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues>\n" +
			"                            <Component>\n" +
			"                              <Id>tcm:17-4906</Id>\n" +
			"                              <Title>Pink Orchids</Title>\n" +
			"                              <Publication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </Publication>\n" +
			"                              <OwningPublication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </OwningPublication>\n" +
			"                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                              <RevisionDate>2014-08-25T10:58:22.353</RevisionDate>\n" +
			"                              <Schema>\n" +
			"                                <Id>tcm:17-79-8</Id>\n" +
			"                                <Title>Image</Title>\n" +
			"                                <Publication>\n" +
			"                                  <Id>tcm:0-17-1</Id>\n" +
			"                                  <Title>500 adcevora.com</Title>\n" +
			"                                </Publication>\n" +
			"                                <Folder>\n" +
			"                                  <Id>tcm:17-11-2</Id>\n" +
			"                                  <Title>Schemas</Title>\n" +
			"                                  <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                </Folder>\n" +
			"                                <RootElementName>undefined</RootElementName>\n" +
			"                              </Schema>\n" +
			"                              <Fields />\n" +
			"                              <MetadataFields />\n" +
			"                              <ComponentType>Multimedia</ComponentType>\n" +
			"                              <Multimedia>\n" +
			"                                <Url>/Preview/media/detail-orchids-pink_tcm17-4906.jpg</Url>\n" +
			"                                <MimeType>image/jpeg</MimeType>\n" +
			"                                <FileName>detail-orchids-pink.jpg</FileName>\n" +
			"                                <FileExtension>jpg</FileExtension>\n" +
			"                                <Size>377965</Size>\n" +
			"                                <Width>0</Width>\n" +
			"                                <Height>0</Height>\n" +
			"                              </Multimedia>\n" +
			"                              <Folder>\n" +
			"                                <Id>tcm:17-1405-2</Id>\n" +
			"                                <Title>ADC Evora</Title>\n" +
			"                                <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                              </Folder>\n" +
			"                              <Categories />\n" +
			"                              <Version>1</Version>\n" +
			"                            </Component>\n" +
			"                          </LinkedComponentValues>\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                  </FieldSet>\n" +
			"                  <FieldSet>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>media</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[3]/custom:media\">\n" +
			"                          <Name>media</Name>\n" +
			"                          <Values>\n" +
			"                            <string>tcm:17-4908</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues>\n" +
			"                            <Component>\n" +
			"                              <Id>tcm:17-4908</Id>\n" +
			"                              <Title>Lamp</Title>\n" +
			"                              <Publication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </Publication>\n" +
			"                              <OwningPublication>\n";
	static String withItemsXml2=		"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </OwningPublication>\n" +
			"                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                              <RevisionDate>2014-08-25T10:58:51.24</RevisionDate>\n" +
			"                              <Schema>\n" +
			"                                <Id>tcm:17-79-8</Id>\n" +
			"                                <Title>Image</Title>\n" +
			"                                <Publication>\n" +
			"                                  <Id>tcm:0-17-1</Id>\n" +
			"                                  <Title>500 adcevora.com</Title>\n" +
			"                                </Publication>\n" +
			"                                <Folder>\n" +
			"                                  <Id>tcm:17-11-2</Id>\n" +
			"                                  <Title>Schemas</Title>\n" +
			"                                  <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                </Folder>\n" +
			"                                <RootElementName>undefined</RootElementName>\n" +
			"                              </Schema>\n" +
			"                              <Fields />\n" +
			"                              <MetadataFields />\n" +
			"                              <ComponentType>Multimedia</ComponentType>\n" +
			"                              <Multimedia>\n" +
			"                                <Url>/Preview/media/outside-andorinha-by-lamp_tcm17-4908.jpg</Url>\n" +
			"                                <MimeType>image/jpeg</MimeType>\n" +
			"                                <FileName>outside-andorinha-by-lamp.jpg</FileName>\n" +
			"                                <FileExtension>jpg</FileExtension>\n" +
			"                                <Size>368370</Size>\n" +
			"                                <Width>0</Width>\n" +
			"                                <Height>0</Height>\n" +
			"                              </Multimedia>\n" +
			"                              <Folder>\n" +
			"                                <Id>tcm:17-1405-2</Id>\n" +
			"                                <Title>ADC Evora</Title>\n" +
			"                                <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                              </Folder>\n" +
			"                              <Categories />\n" +
			"                              <Version>1</Version>\n" +
			"                            </Component>\n" +
			"                          </LinkedComponentValues>\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                  </FieldSet>\n" +
			"                  <FieldSet>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>media</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[4]/custom:media\">\n" +
			"                          <Name>media</Name>\n" +
			"                          <Values>\n" +
			"                            <string>tcm:17-4909</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues>\n" +
			"                            <Component>\n" +
			"                              <Id>tcm:17-4909</Id>\n" +
			"                              <Title>Headboard</Title>\n" +
			"                              <Publication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </Publication>\n" +
			"                              <OwningPublication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </OwningPublication>\n" +
			"                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                              <RevisionDate>2014-08-25T10:59:06.2</RevisionDate>\n" +
			"                              <Schema>\n" +
			"                                <Id>tcm:17-79-8</Id>\n" +
			"                                <Title>Image</Title>\n" +
			"                                <Publication>\n" +
			"                                  <Id>tcm:0-17-1</Id>\n" +
			"                                  <Title>500 adcevora.com</Title>\n" +
			"                                </Publication>\n" +
			"                                <Folder>\n" +
			"                                  <Id>tcm:17-11-2</Id>\n" +
			"                                  <Title>Schemas</Title>\n" +
			"                                  <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                </Folder>\n" +
			"                                <RootElementName>undefined</RootElementName>\n" +
			"                              </Schema>\n" +
			"                              <Fields />\n" +
			"                              <MetadataFields />\n" +
			"                              <ComponentType>Multimedia</ComponentType>\n" +
			"                              <Multimedia>\n" +
			"                                <Url>/Preview/media/premium-headboard_tcm17-4909.jpg</Url>\n" +
			"                                <MimeType>image/jpeg</MimeType>\n" +
			"                                <FileName>premium-headboard.jpg</FileName>\n" +
			"                                <FileExtension>jpg</FileExtension>\n" +
			"                                <Size>300386</Size>\n" +
			"                                <Width>0</Width>\n" +
			"                                <Height>0</Height>\n" +
			"                              </Multimedia>\n" +
			"                              <Folder>\n" +
			"                                <Id>tcm:17-1405-2</Id>\n" +
			"                                <Title>ADC Evora</Title>\n" +
			"                                <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                              </Folder>\n" +
			"                              <Categories />\n" +
			"                              <Version>1</Version>\n" +
			"                            </Component>\n" +
			"                          </LinkedComponentValues>\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                  </FieldSet>\n" +
			"                </EmbeddedValues>\n" +
			"                <EmbeddedSchema>\n" +
			"                  <Id>tcm:17-3275-8</Id>\n" +
			"                  <Title>Item List Element</Title>\n" +
			"                  <Publication>\n" +
			"                    <Id>tcm:0-17-1</Id>\n" +
			"                    <Title>500 adcevora.com</Title>\n" +
			"                  </Publication>\n" +
			"                  <Folder>\n" +
			"                    <Id>tcm:17-27-2</Id>\n" +
			"                    <Title>Embedded</Title>\n" +
			"                    <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                  </Folder>\n" +
			"                  <RootElementName>LinkedContent</RootElementName>\n" +
			"                </EmbeddedSchema>\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </Fields>\n" +
			"        <MetadataFields />\n" +
			"        <ComponentType>Normal</ComponentType>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-661-2</Id>\n" +
			"          <Title>Homepage</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"        <Categories />\n" +
			"        <Version>14</Version>\n" +
			"      </Component>\n" +
			"      <ComponentTemplate>\n" +
			"        <Id>tcm:17-141-32</Id>\n" +
			"        <Title>Carousel [Hero]</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OutputFormat>HTML Fragment</OutputFormat>\n" +
			"        <RevisionDate>2014-07-29T13:16:00.59</RevisionDate>\n" +
			"        <MetadataFields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>view</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>view</Name>\n" +
			"                <Values>\n" +
			"                  <string>Carousel</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>regionView</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>regionView</Name>\n" +
			"                <Values>\n" +
			"                  <string>Hero</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </MetadataFields>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-12-2</Id>\n" +
			"          <Title>Templates</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"      </ComponentTemplate>\n" +
			"      <IsDynamic>false</IsDynamic>\n" +
			"    </ComponentPresentation>\n" +
			"    <ComponentPresentation>\n" +
			"      <Component>\n" +
			"        <Id>tcm:17-4918</Id>\n" +
			"        <Title>Welcome</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OwningPublication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </OwningPublication>\n" +
			"        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"        <RevisionDate>2014-08-25T13:59:42.237</RevisionDate>\n" +
			"        <Schema>\n" +
			"          <Id>tcm:17-80-8</Id>\n" +
			"          <Title>Article</Title>\n" +
			"          <Publication>\n" +
			"            <Id>tcm:0-17-1</Id>\n" +
			"            <Title>500 adcevora.com</Title>\n" +
			"          </Publication>\n" +
			"          <Folder>\n" +
			"            <Id>tcm:17-11-2</Id>\n" +
			"            <Title>Schemas</Title>\n" +
			"            <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"          </Folder>\n" +
			"          <RootElementName>Article</RootElementName>\n" +
			"        </Schema>\n" +
			"        <Fields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>headline</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Article/custom:headline\">\n" +
			"                <Name>headline</Name>\n" +
			"                <Values>\n" +
			"                  <string>WELCOME TO ADC HOTEL</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>articleBody</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Content/custom:Article/custom:articleBody\">\n" +
			"                <Name>articleBody</Name>\n" +
			"                <Values />\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues>\n" +
			"                  <FieldSet>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>content</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"Xhtml\" XPath=\"tcm:Content/custom:Article/custom:articleBody[1]/custom:content\">\n" +
			"                          <Name>content</Name>\n" +
			"                          <Values>\n" +
			"                            <string>&lt;p&gt;Thanks for considering our hotel for your visit to Évora. Our purpose is to meet your needs, handle the details, and reveal our town and region to you in a way that only we can. We have created this website to help you determine if we are the right hotel for your stay, to help you determine how long you should stay, select the right room type and provide you with some ideas ahead of your visit.&lt;/p&gt;&lt;p&gt;Obviously, contact us if you have additional questions. We look forward to meeting you.&lt;/p&gt;&lt;p&gt;This website is new as of November 2013. If you have suggestions for improvement, we welcome and encourage your feedback.&lt;/p&gt;</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues />\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                  </FieldSet>\n" +
			"                </EmbeddedValues>\n" +
			"                <EmbeddedSchema>\n" +
			"                  <Id>tcm:17-232-8</Id>\n" +
			"                  <Title>Paragraph</Title>\n" +
			"                  <Publication>\n" +
			"                    <Id>tcm:0-17-1</Id>\n" +
			"                    <Title>500 adcevora.com</Title>\n" +
			"                  </Publication>\n" +
			"                  <Folder>\n" +
			"                    <Id>tcm:17-27-2</Id>\n" +
			"                    <Title>Embedded</Title>\n" +
			"                    <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                  </Folder>\n" +
			"                  <RootElementName>Paragraph</RootElementName>\n" +
			"                </EmbeddedSchema>\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </Fields>\n" +
			"        <MetadataFields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>standardMeta</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta\">\n" +
			"                <Name>standardMeta</Name>\n" +
			"                <Values />\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues>\n" +
			"                  <FieldSet>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>description</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"MultiLineText\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta[1]/custom:description\">\n" +
			"                          <Name>description</Name>\n" +
			"                          <Values>\n" +
			"                            <string>Charm hotel inside the walls of Évora in Alentejo</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues />\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>name</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"Text\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta[1]/custom:name\">\n" +
			"                          <Name>name</Name>\n" +
			"                          <Values>\n" +
			"                            <string>Home</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues />\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>introText</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"Text\" XPath=\"tcm:Metadata/custom:Metadata/custom:standardMeta[1]/custom:introText\">\n" +
			"                          <Name>introText</Name>\n" +
			"                          <Values>\n" +
			"                            <string>Home page</string>\n";
	static String withItemsXml3=			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues />\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                  </FieldSet>\n" +
			"                </EmbeddedValues>\n" +
			"                <EmbeddedSchema>\n" +
			"                  <Id>tcm:17-231-8</Id>\n" +
			"                  <Title>Standard Metadata</Title>\n" +
			"                  <Publication>\n" +
			"                    <Id>tcm:0-17-1</Id>\n" +
			"                    <Title>500 adcevora.com</Title>\n" +
			"                  </Publication>\n" +
			"                  <Folder>\n" +
			"                    <Id>tcm:17-27-2</Id>\n" +
			"                    <Title>Embedded</Title>\n" +
			"                    <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                  </Folder>\n" +
			"                  <RootElementName>StandardMetadata</RootElementName>\n" +
			"                </EmbeddedSchema>\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </MetadataFields>\n" +
			"        <ComponentType>Normal</ComponentType>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-661-2</Id>\n" +
			"          <Title>Homepage</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"        <Categories />\n" +
			"        <Version>2</Version>\n" +
			"      </Component>\n" +
			"      <ComponentTemplate>\n" +
			"        <Id>tcm:17-83-32</Id>\n" +
			"        <Title>Article</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OutputFormat>HTML Fragment</OutputFormat>\n" +
			"        <RevisionDate>2014-07-29T13:16:22.64</RevisionDate>\n" +
			"        <MetadataFields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>view</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>view</Name>\n" +
			"                <Values>\n" +
			"                  <string>Article</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </MetadataFields>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-12-2</Id>\n" +
			"          <Title>Templates</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"      </ComponentTemplate>\n" +
			"      <IsDynamic>false</IsDynamic>\n" +
			"    </ComponentPresentation>\n" +
			"    <ComponentPresentation>\n" +
			"      <Component>\n" +
			"        <Id>tcm:17-4920</Id>\n" +
			"        <Title>Food Teaser</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OwningPublication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </OwningPublication>\n" +
			"        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"        <RevisionDate>2014-08-25T15:13:07.807</RevisionDate>\n" +
			"        <Schema>\n" +
			"          <Id>tcm:17-117-8</Id>\n" +
			"          <Title>Teaser</Title>\n" +
			"          <Publication>\n" +
			"            <Id>tcm:0-17-1</Id>\n" +
			"            <Title>500 adcevora.com</Title>\n" +
			"          </Publication>\n" +
			"          <Folder>\n" +
			"            <Id>tcm:17-11-2</Id>\n" +
			"            <Title>Schemas</Title>\n" +
			"            <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"          </Folder>\n" +
			"          <RootElementName>Teaser</RootElementName>\n" +
			"        </Schema>\n" +
			"        <Fields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>headline</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:headline\">\n" +
			"                <Name>headline</Name>\n" +
			"                <Values>\n" +
			"                  <string>Food &amp; Drink</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>content</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:content\">\n" +
			"                <Name>content</Name>\n" +
			"                <Values>\n" +
			"                  <string>Learn about our famous organic, local breakfast and our hand selected all day food options</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>media</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:Teaser/custom:media\">\n" +
			"                <Name>media</Name>\n" +
			"                <Values>\n" +
			"                  <string>tcm:17-4919</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues>\n" +
			"                  <Component>\n" +
			"                    <Id>tcm:17-4919</Id>\n" +
			"                    <Title>Wine Bottle</Title>\n" +
			"                    <Publication>\n" +
			"                      <Id>tcm:0-17-1</Id>\n" +
			"                      <Title>500 adcevora.com</Title>\n" +
			"                    </Publication>\n" +
			"                    <OwningPublication>\n" +
			"                      <Id>tcm:0-17-1</Id>\n" +
			"                      <Title>500 adcevora.com</Title>\n" +
			"                    </OwningPublication>\n" +
			"                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                    <RevisionDate>2014-08-25T13:53:45.233</RevisionDate>\n" +
			"                    <Schema>\n" +
			"                      <Id>tcm:17-79-8</Id>\n" +
			"                      <Title>Image</Title>\n" +
			"                      <Publication>\n" +
			"                        <Id>tcm:0-17-1</Id>\n" +
			"                        <Title>500 adcevora.com</Title>\n" +
			"                      </Publication>\n" +
			"                      <Folder>\n" +
			"                        <Id>tcm:17-11-2</Id>\n" +
			"                        <Title>Schemas</Title>\n" +
			"                        <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                      </Folder>\n" +
			"                      <RootElementName>undefined</RootElementName>\n" +
			"                    </Schema>\n" +
			"                    <Fields />\n" +
			"                    <MetadataFields />\n" +
			"                    <ComponentType>Multimedia</ComponentType>\n" +
			"                    <Multimedia>\n" +
			"                      <Url>/Preview/media/wine-bottle-medium_tcm17-4919.jpg</Url>\n" +
			"                      <MimeType>image/jpeg</MimeType>\n" +
			"                      <FileName>wine-bottle-medium.jpg</FileName>\n" +
			"                      <FileExtension>jpg</FileExtension>\n" +
			"                      <Size>36642</Size>\n" +
			"                      <Width>0</Width>\n" +
			"                      <Height>0</Height>\n" +
			"                    </Multimedia>\n" +
			"                    <Folder>\n" +
			"                      <Id>tcm:17-1405-2</Id>\n" +
			"                      <Title>ADC Evora</Title>\n" +
			"                      <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                    </Folder>\n" +
			"                    <Categories />\n" +
			"                    <Version>1</Version>\n" +
			"                  </Component>\n" +
			"                </LinkedComponentValues>\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>link</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Embedded\" XPath=\"tcm:Content/custom:Teaser/custom:link\">\n" +
			"                <Name>link</Name>\n" +
			"                <Values />\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues>\n" +
			"                  <FieldSet>\n" +
			"                    <item>\n" +
			"                      <key>\n" +
			"                        <string>internalLink</string>\n" +
			"                      </key>\n" +
			"                      <value>\n" +
			"                        <Field FieldType=\"ComponentLink\" XPath=\"tcm:Content/custom:Teaser/custom:link[1]/custom:internalLink\">\n" +
			"                          <Name>internalLink</Name>\n" +
			"                          <Values>\n" +
			"                            <string>tcm:17-4962</string>\n" +
			"                          </Values>\n" +
			"                          <NumericValues />\n" +
			"                          <DateTimeValues />\n" +
			"                          <LinkedComponentValues>\n" +
			"                            <Component>\n" +
			"                              <Id>tcm:17-4962</Id>\n" +
			"                              <Title>Food &amp; Drink</Title>\n" +
			"                              <Publication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </Publication>\n" +
			"                              <OwningPublication>\n" +
			"                                <Id>tcm:0-17-1</Id>\n" +
			"                                <Title>500 adcevora.com</Title>\n" +
			"                              </OwningPublication>\n" +
			"                              <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                              <RevisionDate>2014-09-10T17:42:27.14</RevisionDate>\n" +
			"                              <Schema>\n" +
			"                                <Id>tcm:17-139-8</Id>\n" +
			"                                <Title>Item List</Title>\n" +
			"                                <Publication>\n" +
			"                                  <Id>tcm:0-17-1</Id>\n" +
			"                                  <Title>500 adcevora.com</Title>\n" +
			"                                </Publication>\n" +
			"                                <Folder>\n" +
			"                                  <Id>tcm:17-11-2</Id>\n" +
			"                                  <Title>Schemas</Title>\n" +
			"                                  <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                </Folder>\n" +
			"                                <RootElementName>ItemList</RootElementName>\n" +
			"                              </Schema>\n" +
			"                              <Fields>\n" +
			"                                <item>\n" +
			"                                  <key>\n" +
			"                                    <string>headline</string>\n" +
			"                                  </key>\n" +
			"                                  <value>\n" +
			"                                    <Field FieldType=\"Text\" XPath=\"tcm:Content/custom:ItemList/custom:headline\">\n" +
			"                                      <Name>headline</Name>\n" +
			"                                      <Values>\n" +
			"                                        <string>Food &amp; Drink</string>\n" +
			"                                      </Values>\n" +
			"                                      <NumericValues />\n" +
			"                                      <DateTimeValues />\n" +
			"                                      <LinkedComponentValues />\n" +
			"                                      <EmbeddedValues />\n" +
			"                                      <Keywords />\n" +
			"                                    </Field>\n" +
			"                                  </value>\n" +
			"                                </item>\n" +
			"                                <item>\n" +
			"                                  <key>\n" +
			"                                    <string>itemListElement</string>\n" +
			"                                  </key>\n" +
			"                                  <value>\n" +
			"                                    <Field FieldType=\"Embedded\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement\">\n" +
			"                                      <Name>itemListElement</Name>\n" +
			"                                      <Values />\n" +
			"                                      <NumericValues />\n" +
			"                                      <DateTimeValues />\n" +
			"                                      <LinkedComponentValues />\n" +
			"                                      <EmbeddedValues>\n" +
			"                                        <FieldSet>\n" +
			"                                          <item>\n" +
			"                                            <key>\n" +
			"                                              <string>subheading</string>\n" +
			"                                            </key>\n" +
			"                                            <value>\n" +
			"                                              <Field FieldType=\"Text\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:subheading\">\n" +
			"                                                <Name>subheading</Name>\n" +
			"                                                <Values>\n" +
			"                                                  <string>Breakfast</string>\n" +
			"                                                </Values>\n" +
			"                                                <NumericValues />\n" +
			"                                                <DateTimeValues />\n" +
			"                                                <LinkedComponentValues />\n" +
			"                                                <EmbeddedValues />\n" +
			"                                                <Keywords />\n" +
			"                                              </Field>\n" +
			"                                            </value>\n" +
			"                                          </item>\n" +
			"                                          <item>\n" +
			"                                            <key>\n" +
			"                                              <string>content</string>\n" +
			"                                            </key>\n" +
			"                                            <value>\n" +
			"                                              <Field FieldType=\"Xhtml\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:content\">\n" +
			"                                                <Name>content</Name>\n" +
			"                                                <Values>\n" +
			"                                                  <string>&lt;p&gt;&lt;img xlink:href=\"tcm:17-4959\" title=\"Pies\" alt=\"Pies\" class=\"pull-right\" xlink:title=\"Pies\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" src=\"/Preview/media/food-tarte-1-childs_rooms_tcm17-4959.jpg\" /&gt;&lt;/p&gt;&lt;p&gt;Sharing a meal is a great way to get to know someone. Perhaps the single best service we offer is our exceptional all-inclusive breakfast. It’s the one time when nearly every guest gathers each morning to enjoy a common experience. We feature fresh, organic, and locally-grown products in abundant supply. Our menu rotates seasonally—but our delicious pancakes and free-range eggs are forever!&lt;/p&gt;&lt;p&gt;We serve breakfast every day from 8:00 A.M. to 10:30 A.M. including varieties of organic coffees, teas, and fresh squeezed juices. If you would like breakfast before 8 or after 10:30, just try to tell us a little bit in advance. We can also serve you breakfast in your room if you’d like. You can show up any time you want for a cup of organic coffee.&lt;/p&gt;&lt;p&gt;Breakfast in Portuguese is pequeno-almoço, which literally translates to “small lunch.” Enjoy yours indoors or outside in our courtyard.&lt;/p&gt;&lt;p&gt;View our current breakfast selection here:&lt;/p&gt;</string>\n" +
			"                                                </Values>\n" +
			"                                                <NumericValues />\n" +
			"                                                <DateTimeValues />\n" +
			"                                                <LinkedComponentValues />\n" +
			"                                                <EmbeddedValues />\n" +
			"                                                <Keywords />\n" +
			"                                              </Field>\n" +
			"                                            </value>\n" +
			"                                          </item>\n" +
			"                                          <item>\n" +
			"                                            <key>\n" +
			"                                              <string>media</string>\n" +
			"                                            </key>\n" +
			"                                            <value>\n" +
			"                                              <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[1]/custom:media\">\n" +
			"                                                <Name>media</Name>\n" +
			"                                                <Values>\n" +
			"                                                  <string>tcm:17-4965</string>\n" +
			"                                                </Values>\n" +
			"                                                <NumericValues />\n" +
			"                                                <DateTimeValues />\n" +
			"                                                <LinkedComponentValues>\n" +
			"                                                  <Component>\n" +
			"                                                    <Id>tcm:17-4965</Id>\n" +
			"                                                    <Title>Breakfast Menu</Title>\n" +
			"                                                    <Publication>\n" +
			"                                                      <Id>tcm:0-17-1</Id>\n" +
			"                                                      <Title>500 adcevora.com</Title>\n" +
			"                                                    </Publication>\n" +
			"                                                    <OwningPublication>\n" +
			"                                                      <Id>tcm:0-17-1</Id>\n" +
			"                                                      <Title>500 adcevora.com</Title>\n" +
			"                                                    </OwningPublication>\n" +
			"                                                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                                                    <RevisionDate>2014-08-25T15:19:29.817</RevisionDate>\n" +
			"                                                    <Schema>\n" +
			"                                                      <Id>tcm:17-4465-8</Id>\n" +
			"                                                      <Title>Download</Title>\n" +
			"                                                      <Publication>\n" +
			"                                                        <Id>tcm:0-17-1</Id>\n" +
			"                                                        <Title>500 adcevora.com</Title>\n" +
			"                                                      </Publication>\n" +
			"                                                      <Folder>\n" +
			"                                                        <Id>tcm:17-11-2</Id>\n" +
			"                                                        <Title>Schemas</Title>\n" +
			"                                                        <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                                      </Folder>\n" +
			"                                                      <RootElementName>undefined</RootElementName>\n" +
			"                                                    </Schema>\n" +
			"                                                    <Fields />\n" +
			"                                                    <MetadataFields />\n" +
			"                                                    <ComponentType>Multimedia</ComponentType>\n" +
			"                                                    <Multimedia>\n" +
			"                                                      <Url>/Preview/media/breakfast-menu_tcm17-4965.pdf</Url>\n" +
			"                                                      <MimeType>application/pdf</MimeType>\n" +
			"                                                      <FileName>breakfast-menu.pdf</FileName>\n" +
			"                                                      <FileExtension>pdf</FileExtension>\n" +
			"                                                      <Size>446104</Size>\n" +
			"                                                      <Width>0</Width>\n" +
			"                                                      <Height>0</Height>\n" +
			"                                                    </Multimedia>\n" +
			"                                                    <Folder>\n" +
			"                                                      <Id>tcm:17-667-2</Id>\n" +
			"                                                      <Title>Downloads</Title>\n" +
			"                                                      <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                                    </Folder>\n" +
			"                                                    <Categories />\n" +
			"                                                    <Version>1</Version>\n" +
			"                                                  </Component>\n" +
			"                                                </LinkedComponentValues>\n" +
			"                                                <EmbeddedValues />\n" +
			"                                                <Keywords />\n" +
			"                                              </Field>\n" +
			"                                            </value>\n" +
			"                                          </item>\n" +
			"                                        </FieldSet>\n" +
			"                                        <FieldSet>\n" +
			"                                          <item>\n" +
			"                                            <key>\n" +
			"                                              <string>subheading</string>\n" +
			"                                            </key>\n" +
			"                                            <value>\n" +
			"                                              <Field FieldType=\"Text\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:subheading\">\n" +
			"                                                <Name>subheading</Name>\n" +
			"                                                <Values>\n" +
			"                                                  <string>MóBar</string>\n" +
			"                                                </Values>\n" +
			"                                                <NumericValues />\n" +
			"                                                <DateTimeValues />\n" +
			"                                                <LinkedComponentValues />\n" +
			"                                                <EmbeddedValues />\n" +
			"                                                <Keywords />\n" +
			"                                              </Field>\n" +
			"                                            </value>\n" +
			"                                          </item>\n" +
			"                                          <item>\n" +
			"                                            <key>\n" +
			"                                              <string>content</string>\n" +
			"                                            </key>\n" +
			"                                            <value>\n" +
			"                                              <Field FieldType=\"Xhtml\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:content\">\n" +
			"                                                <Name>content</Name>\n" +
			"                                                <Values>\n" +
			"                                                  <string>&lt;p&gt;&lt;img title=\"Wine Closeup\" alt=\"Wine Closeup\" class=\"pull-left\" xlink:href=\"tcm:17-4960\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:title=\"Wine Closeup\" src=\"/Preview/media/detail-wine-bottle2-childs_rooms_tcm17-4960.jpg\" /&gt;&lt;/p&gt;&lt;p&gt;All of your favorite drinks are available anytime from our MóBar. We have sampled many of the wines from the Alentejo and have picked our favorites. Consider an aperitif or digestif of an aged port or Portuguese aguardente. Or try one of our signature cocktails—we make a more than decent gin and tonic and, given our Brazilian roots, we make a great caipirinha. We will always try to make your favorite drink – even if you have to teach us how to make it!&lt;/p&gt;&lt;p&gt;If what you want is not on the menu, please ask, there is always the chance we have what you are looking for.&lt;/p&gt;&lt;p&gt;&lt;br /&gt;\n" +
			"Lunch and In-between&lt;/p&gt;&lt;p&gt;One of our best kept secrets is our hand-selected menu of homemade tapas—served whenever you wish. Seasonal soup, regional cheeses, exceptional sandwiches, healthy salads, Portuguese presunto, olives, and bread can be combined in the most delightful ways which we invite you to enjoy anytime, anywhere —we’ll even pack a lunch for you if you like.&lt;/p&gt;&lt;p&gt;The name MóBar&lt;/p&gt;&lt;p&gt;MóBar is a a play on the Portuguese word for millstone (the big round stone you see in our courtyard that was used to crush olives) - mó)&lt;/p&gt;</string>\n" +
			"                                                </Values>\n" +
			"                                                <NumericValues />\n" +
			"                                                <DateTimeValues />\n" +
			"                                                <LinkedComponentValues />\n" +
			"                                                <EmbeddedValues />\n" +
			"                                                <Keywords />\n" +
			"                                              </Field>\n" +
			"                                            </value>\n" +
			"                                          </item>\n" +
			"                                          <item>\n" +
			"                                            <key>\n" +
			"                                              <string>media</string>\n" +
			"                                            </key>\n" +
			"                                            <value>\n" +
			"                                              <Field FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:ItemList/custom:itemListElement[2]/custom:media\">\n" +
			"                                                <Name>media</Name>\n" +
			"                                                <Values>\n" +
			"                                                  <string>tcm:17-4964</string>\n" +
			"                                                </Values>\n" +
			"                                                <NumericValues />\n" +
			"                                                <DateTimeValues />\n" +
			"                                                <LinkedComponentValues>\n" +
			"                                                  <Component>\n" +
			"                                                    <Id>tcm:17-4964</Id>\n" +
			"                                                    <Title>Bar Menu</Title>\n" +
			"                                                    <Publication>\n" +
			"                                                      <Id>tcm:0-17-1</Id>\n" +
			"                                                      <Title>500 adcevora.com</Title>\n" +
			"                                                    </Publication>\n" +
			"                                                    <OwningPublication>\n" +
			"                                                      <Id>tcm:0-17-1</Id>\n" +
			"                                                      <Title>500 adcevora.com</Title>\n" +
			"                                                    </OwningPublication>\n" +
			"                                                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                                                    <RevisionDate>2014-08-25T15:19:00.783</RevisionDate>\n" +
			"                                                    <Schema>\n" +
			"                                                      <Id>tcm:17-4465-8</Id>\n" +
			"                                                      <Title>Download</Title>\n" +
			"                                                      <Publication>\n" +
			"                                                        <Id>tcm:0-17-1</Id>\n" +
			"                                                        <Title>500 adcevora.com</Title>\n" +
			"                                                      </Publication>\n" +
			"                                                      <Folder>\n" +
			"                                                        <Id>tcm:17-11-2</Id>\n" +
			"                                                        <Title>Schemas</Title>\n" +
			"                                                        <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                                      </Folder>\n" +
			"                                                      <RootElementName>undefined</RootElementName>\n" +
			"                                                    </Schema>\n" +
			"                                                    <Fields />\n" +
			"                                                    <MetadataFields />\n" +
			"                                                    <ComponentType>Multimedia</ComponentType>\n" +
			"                                                    <Multimedia>\n" +
			"                                                      <Url>/Preview/media/bar-menu_tcm17-4964.pdf</Url>\n" +
			"                                                      <MimeType>application/pdf</MimeType>\n" +
			"                                                      <FileName>bar-menu.pdf</FileName>\n" +
			"                                                      <FileExtension>pdf</FileExtension>\n" +
			"                                                      <Size>161661</Size>\n" +
			"                                                      <Width>0</Width>\n" +
			"                                                      <Height>0</Height>\n" +
			"                                                    </Multimedia>\n" +
			"                                                    <Folder>\n" +
			"                                                      <Id>tcm:17-667-2</Id>\n" +
			"                                                      <Title>Downloads</Title>\n" +
			"                                                      <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                                    </Folder>\n" +
			"                                                    <Categories />\n" +
			"                                                    <Version>1</Version>\n" +
			"                                                  </Component>\n" +
			"                                                </LinkedComponentValues>\n" +
			"                                                <EmbeddedValues />\n" +
			"                                                <Keywords />\n" +
			"                                              </Field>\n" +
			"                                            </value>\n" +
			"                                          </item>\n" +
			"                                        </FieldSet>\n" +
			"                                      </EmbeddedValues>\n" +
			"                                      <EmbeddedSchema>\n" +
			"                                        <Id>tcm:17-3275-8</Id>\n" +
			"                                        <Title>Item List Element</Title>\n" +
			"                                        <Publication>\n" +
			"                                          <Id>tcm:0-17-1</Id>\n" +
			"                                          <Title>500 adcevora.com</Title>\n" +
			"                                        </Publication>\n" +
			"                                        <Folder>\n" +
			"                                          <Id>tcm:17-27-2</Id>\n" +
			"                                          <Title>Embedded</Title>\n" +
			"                                          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                                        </Folder>\n" +
			"                                        <RootElementName>LinkedContent</RootElementName>\n" +
			"                                      </EmbeddedSchema>\n" +
			"                                      <Keywords />\n" +
			"                                    </Field>\n" +
			"                                  </value>\n" +
			"                                </item>\n" +
			"                              </Fields>\n" +
			"                              <MetadataFields />\n" +
			"                              <ComponentType>Normal</ComponentType>\n" +
			"                              <Folder>\n" +
			"                                <Id>tcm:17-1410-2</Id>\n" +
			"                                <Title>More...</Title>\n" +
			"                                <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                              </Folder>\n" +
			"                              <Categories />\n" +
			"                              <Version>11</Version>\n" +
			"                            </Component>\n" +
			"                          </LinkedComponentValues>\n" +
			"                          <EmbeddedValues />\n" +
			"                          <Keywords />\n" +
			"                        </Field>\n" +
			"                      </value>\n" +
			"                    </item>\n" +
			"                  </FieldSet>\n" +
			"                </EmbeddedValues>\n" +
			"                <EmbeddedSchema>\n" +
			"                  <Id>tcm:17-115-8</Id>\n" +
			"                  <Title>Link</Title>\n" +
			"                  <Publication>\n" +
			"                    <Id>tcm:0-17-1</Id>\n" +
			"                    <Title>500 adcevora.com</Title>\n" +
			"                  </Publication>\n" +
			"                  <Folder>\n" +
			"                    <Id>tcm:17-27-2</Id>\n" +
			"                    <Title>Embedded</Title>\n" +
			"                    <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                  </Folder>\n" +
			"                  <RootElementName>EmbeddedLink</RootElementName>\n" +
			"                </EmbeddedSchema>\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </Fields>\n" +
			"        <MetadataFields />\n" +
			"        <ComponentType>Normal</ComponentType>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-661-2</Id>\n" +
			"          <Title>Homepage</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"        <Categories />\n" +
			"        <Version>3</Version>\n" +
			"      </Component>\n" +
			"      <ComponentTemplate>\n" +
			"        <Id>tcm:17-130-32</Id>\n" +
			"        <Title>Teaser [3-Column]</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OutputFormat>HTML Fragment</OutputFormat>\n" +
			"        <RevisionDate>2014-08-01T16:11:58.627</RevisionDate>\n" +
			"        <MetadataFields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>view</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>view</Name>\n" +
			"                <Values>\n" +
			"                  <string>Teaser</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>regionView</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>regionView</Name>\n" +
			"                <Values>\n" +
			"                  <string>3-Column</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </MetadataFields>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-12-2</Id>\n" +
			"          <Title>Templates</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"      </ComponentTemplate>\n" +
			"      <IsDynamic>false</IsDynamic>\n" +
			"    </ComponentPresentation>\n" +
			"    <ComponentPresentation>\n" +
			"      <Component>\n" +
			"        <Id>tcm:17-4922</Id>\n" +
			"        <Title>Day Trips Teaser</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OwningPublication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </OwningPublication>\n" +
			"        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"        <RevisionDate>2014-09-10T16:47:46.613</RevisionDate>\n" +
			"        <Schema>\n" +
			"          <Id>tcm:17-117-8</Id>\n" +
			"          <Title>Teaser</Title>\n" +
			"          <Publication>\n" +
			"            <Id>tcm:0-17-1</Id>\n" +
			"            <Title>500 adcevora.com</Title>\n" +
			"          </Publication>\n" +
			"          <Folder>\n" +
			"            <Id>tcm:17-11-2</Id>\n" +
			"            <Title>Schemas</Title>\n" +
			"            <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"          </Folder>\n" +
			"          <RootElementName>Teaser</RootElementName>\n" +
			"        </Schema>\n" +
			"        <Fields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>headline</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:headline\">\n" +
			"                <Name>headline</Name>\n" +
			"                <Values>\n" +
			"                  <string>Day Trips</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>content</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:content\">\n" +
			"                <Name>content</Name>\n" +
			"                <Values>\n" +
			"                  <string>Use our hotel as a base for exploring the Alentejo. Light traffic and worth the drive.</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>media</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:Teaser/custom:media\">\n" +
			"                <Name>media</Name>\n" +
			"                <Values>\n" +
			"                  <string>tcm:17-4921</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues>\n" +
			"                  <Component>\n" +
			"                    <Id>tcm:17-4921</Id>\n" +
			"                    <Title>City Wall</Title>\n" +
			"                    <Publication>\n" +
			"                      <Id>tcm:0-17-1</Id>\n" +
			"                      <Title>500 adcevora.com</Title>\n" +
			"                    </Publication>\n" +
			"                    <OwningPublication>\n" +
			"                      <Id>tcm:0-17-1</Id>\n" +
			"                      <Title>500 adcevora.com</Title>\n" +
			"                    </OwningPublication>\n" +
			"                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                    <RevisionDate>2014-08-25T13:55:56.22</RevisionDate>\n" +
			"                    <Schema>\n" +
			"                      <Id>tcm:17-79-8</Id>\n" +
			"                      <Title>Image</Title>\n" +
			"                      <Publication>\n" +
			"                        <Id>tcm:0-17-1</Id>\n" +
			"                        <Title>500 adcevora.com</Title>\n" +
			"                      </Publication>\n" +
			"                      <Folder>\n" +
			"                        <Id>tcm:17-11-2</Id>\n" +
			"                        <Title>Schemas</Title>\n" +
			"                        <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                      </Folder>\n" +
			"                      <RootElementName>undefined</RootElementName>\n" +
			"                    </Schema>\n" +
			"                    <Fields />\n" +
			"                    <MetadataFields />\n" +
			"                    <ComponentType>Multimedia</ComponentType>\n" +
			"                    <Multimedia>\n" +
			"                      <Url>/Preview/media/outside-marvao-view-medium_tcm17-4921.jpg</Url>\n" +
			"                      <MimeType>image/jpeg</MimeType>\n" +
			"                      <FileName>outside-marvao-view-medium.jpg</FileName>\n" +
			"                      <FileExtension>jpg</FileExtension>\n" +
			"                      <Size>34347</Size>\n" +
			"                      <Width>0</Width>\n" +
			"                      <Height>0</Height>\n" +
			"                    </Multimedia>\n" +
			"                    <Folder>\n" +
			"                      <Id>tcm:17-1405-2</Id>\n" +
			"                      <Title>ADC Evora</Title>\n" +
			"                      <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                    </Folder>\n" +
			"                    <Categories />\n" +
			"                    <Version>1</Version>\n" +
			"                  </Component>\n" +
			"                </LinkedComponentValues>\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </Fields>\n" +
			"        <MetadataFields />\n" +
			"        <ComponentType>Normal</ComponentType>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-661-2</Id>\n" +
			"          <Title>Homepage</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"        <Categories />\n" +
			"        <Version>2</Version>\n" +
			"      </Component>\n" +
			"      <ComponentTemplate>\n" +
			"        <Id>tcm:17-130-32</Id>\n" +
			"        <Title>Teaser [3-Column]</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OutputFormat>HTML Fragment</OutputFormat>\n" +
			"        <RevisionDate>2014-08-01T16:11:58.627</RevisionDate>\n" +
			"        <MetadataFields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>view</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>view</Name>\n" +
			"                <Values>\n" +
			"                  <string>Teaser</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>regionView</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>regionView</Name>\n" +
			"                <Values>\n" +
			"                  <string>3-Column</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </MetadataFields>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-12-2</Id>\n" +
			"          <Title>Templates</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"      </ComponentTemplate>\n" +
			"      <IsDynamic>false</IsDynamic>\n" +
			"    </ComponentPresentation>\n" +
			"    <ComponentPresentation>\n" +
			"      <Component>\n" +
			"        <Id>tcm:17-4924</Id>\n" +
			"        <Title>Wine Teaser</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OwningPublication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </OwningPublication>\n" +
			"        <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"        <RevisionDate>2014-08-25T13:57:34.313</RevisionDate>\n" +
			"        <Schema>\n" +
			"          <Id>tcm:17-117-8</Id>\n" +
			"          <Title>Teaser</Title>\n" +
			"          <Publication>\n" +
			"            <Id>tcm:0-17-1</Id>\n" +
			"            <Title>500 adcevora.com</Title>\n" +
			"          </Publication>\n" +
			"          <Folder>\n" +
			"            <Id>tcm:17-11-2</Id>\n" +
			"            <Title>Schemas</Title>\n" +
			"            <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"          </Folder>\n" +
			"          <RootElementName>Teaser</RootElementName>\n" +
			"        </Schema>\n" +
			"        <Fields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>headline</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:headline\">\n" +
			"                <Name>headline</Name>\n" +
			"                <Values>\n" +
			"                  <string>Wine Touring</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>content</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\" XPath=\"tcm:Content/custom:Teaser/custom:content\">\n" +
			"                <Name>content</Name>\n" +
			"                <Values>\n" +
			"                  <string>50 wineries within 50km</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>media</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"MultiMediaLink\" XPath=\"tcm:Content/custom:Teaser/custom:media\">\n" +
			"                <Name>media</Name>\n" +
			"                <Values>\n" +
			"                  <string>tcm:17-4923</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues>\n" +
			"                  <Component>\n" +
			"                    <Id>tcm:17-4923</Id>\n" +
			"                    <Title>Wine Barrels</Title>\n" +
			"                    <Publication>\n" +
			"                      <Id>tcm:0-17-1</Id>\n" +
			"                      <Title>500 adcevora.com</Title>\n" +
			"                    </Publication>\n" +
			"                    <OwningPublication>\n" +
			"                      <Id>tcm:0-17-1</Id>\n" +
			"                      <Title>500 adcevora.com</Title>\n" +
			"                    </OwningPublication>\n" +
			"                    <LastPublishedDate>0001-01-01T00:00:00</LastPublishedDate>\n" +
			"                    <RevisionDate>2014-08-25T13:57:27.23</RevisionDate>\n" +
			"                    <Schema>\n" +
			"                      <Id>tcm:17-79-8</Id>\n" +
			"                      <Title>Image</Title>\n" +
			"                      <Publication>\n" +
			"                        <Id>tcm:0-17-1</Id>\n" +
			"                        <Title>500 adcevora.com</Title>\n" +
			"                      </Publication>\n" +
			"                      <Folder>\n" +
			"                        <Id>tcm:17-11-2</Id>\n" +
			"                        <Title>Schemas</Title>\n" +
			"                        <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                      </Folder>\n" +
			"                      <RootElementName>undefined</RootElementName>\n" +
			"                    </Schema>\n" +
			"                    <Fields />\n" +
			"                    <MetadataFields />\n" +
			"                    <ComponentType>Multimedia</ComponentType>\n" +
			"                    <Multimedia>\n" +
			"                      <Url>/Preview/media/outside-barrels-esquila-medium_tcm17-4923.jpg</Url>\n" +
			"                      <MimeType>image/jpeg</MimeType>\n" +
			"                      <FileName>outside-barrels-esquila-medium.jpg</FileName>\n" +
			"                      <FileExtension>jpg</FileExtension>\n" +
			"                      <Size>35066</Size>\n" +
			"                      <Width>0</Width>\n" +
			"                      <Height>0</Height>\n" +
			"                    </Multimedia>\n" +
			"                    <Folder>\n" +
			"                      <Id>tcm:17-1405-2</Id>\n" +
			"                      <Title>ADC Evora</Title>\n" +
			"                      <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"                    </Folder>\n" +
			"                    <Categories />\n" +
			"                    <Version>1</Version>\n" +
			"                  </Component>\n" +
			"                </LinkedComponentValues>\n" +
			"                <EmbeddedValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </Fields>\n" +
			"        <MetadataFields />\n" +
			"        <ComponentType>Normal</ComponentType>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-661-2</Id>\n" +
			"          <Title>Homepage</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"        <Categories />\n" +
			"        <Version>1</Version>\n" +
			"      </Component>\n" +
			"      <ComponentTemplate>\n" +
			"        <Id>tcm:17-130-32</Id>\n" +
			"        <Title>Teaser [3-Column]</Title>\n" +
			"        <Publication>\n" +
			"          <Id>tcm:0-17-1</Id>\n" +
			"          <Title>500 adcevora.com</Title>\n" +
			"        </Publication>\n" +
			"        <OutputFormat>HTML Fragment</OutputFormat>\n" +
			"        <RevisionDate>2014-08-01T16:11:58.627</RevisionDate>\n" +
			"        <MetadataFields>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>view</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>view</Name>\n" +
			"                <Values>\n" +
			"                  <string>Teaser</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"          <item>\n" +
			"            <key>\n" +
			"              <string>regionView</string>\n" +
			"            </key>\n" +
			"            <value>\n" +
			"              <Field xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FieldType=\"Text\">\n" +
			"                <Name>regionView</Name>\n" +
			"                <Values>\n" +
			"                  <string>3-Column</string>\n" +
			"                </Values>\n" +
			"                <NumericValues />\n" +
			"                <DateTimeValues />\n" +
			"                <LinkedComponentValues />\n" +
			"                <Keywords />\n" +
			"              </Field>\n" +
			"            </value>\n" +
			"          </item>\n" +
			"        </MetadataFields>\n" +
			"        <Folder>\n" +
			"          <Id>tcm:17-12-2</Id>\n" +
			"          <Title>Templates</Title>\n" +
			"          <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"        </Folder>\n" +
			"      </ComponentTemplate>\n" +
			"      <IsDynamic>false</IsDynamic>\n" +
			"    </ComponentPresentation>\n" +
			"  </ComponentPresentations>\n" +
			"  <StructureGroup>\n" +
			"    <Id>tcm:17-3-4</Id>\n" +
			"    <Title>Home</Title>\n" +
			"    <PublicationId>tcm:0-17-1</PublicationId>\n" +
			"  </StructureGroup>\n" +
			"  <Categories />\n" +
			"  <Version>17</Version>\n" +
			"</Page>";
}
