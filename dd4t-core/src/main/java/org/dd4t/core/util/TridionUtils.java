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
package org.dd4t.core.util;

import com.tridion.storage.ItemMeta;
import com.tridion.util.TCMURI;

public class TridionUtils {
	
	public static final String TCM_REGEX = "(tcm:[0-9]+-[0-9]+(-[0-9]+)?)";
	
	public static TCMURI createUri(ItemMeta item) {
		return new TCMURI(item.getPublicationId(), item.getItemId(), item.getItemType(), 0);		
	}
	
	public static String getPublicationUri(int id){
		return getPublicationUri(String.valueOf(id));
	}
	
	public static String getPublicationUri(String id){
		return "tcm:0-"+id+"-1";
	}
}
