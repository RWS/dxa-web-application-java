/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.SimpleBinary;
import org.dd4t.core.factories.BinaryFactory;

import com.tridion.data.BinaryData;
import com.tridion.storage.BinaryMeta;

public class SimpleBinaryImpl extends BasePublishedItem implements SimpleBinary {

	private BinaryData binaryData = null;
	private BinaryMeta nativeBinaryMetaData = null;
	private BinaryFactory factory;
	private Schema schema;
	private int height;
	private int width;
	private String alt;
	private String mimeType = null;

	public BinaryFactory getFactory() {
		return factory;
	}

	public void setFactory(BinaryFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	@Override
	public BinaryData getBinaryData() {
		if (binaryData == null) {
			factory.retrieveBinaryData(this);
		}
		return this.binaryData;
	}

	@Override
	public void setBinaryData(BinaryData binaryData) {
		this.binaryData = binaryData;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String getAlt() {
		return alt;
	}

	@Override
	public void setAlt(String alt) {
		this.alt = alt;
	}

	@Override
	public String getMimeType() {
		if (mimeType==null) {
			BinaryMeta binaryMeta = getNativeBinaryMetadata();
			if (binaryMeta == null) 
				return null;
			mimeType = binaryMeta.getBinaryType();
		}
		return mimeType;
	}

	@Override
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public BinaryMeta getNativeBinaryMetadata() {
		return this.nativeBinaryMetaData;
	}

	@Override
	public void setNativeBinaryMetadata(BinaryMeta nativeBinaryMeta) {
		this.nativeBinaryMetaData = nativeBinaryMeta;
	}
}