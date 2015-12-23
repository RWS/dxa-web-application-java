/*
 * Copyright (c) 2015 R. Oudshoorn
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
package org.dd4t.springmvc.view.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * Class wraps around a HttpServletResponse to catch everything that is written to it. 
 * It catches both the printwriter AND the OutputStream channels.
 * 
 * @author rooudsho
 *
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
	protected static Logger logger = LoggerFactory.getLogger(CharResponseWrapper.class);
	
    private CharArrayWriter output;
    private ServletOutputStreamWrapper stream;
    private boolean writerUsed;

    @Override
    public String toString() {
    	StringBuffer buf = new StringBuffer();
    	
    	// if the stream is used, empty it
    	if(stream.isWrittenTo()){
    		logger.debug("Appending stream.");
    		buf.append( stream.toString());
    	}
    	
    	// if the writer is used, empty it
    	if(writerUsed){
    		logger.debug("Appending printwriter.");
    		buf.append( output.toString());        
    	}
    	
    	return buf.toString();
    }

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new CharArrayWriter();
        stream = new ServletOutputStreamWrapper();
        writerUsed = false;
        
        logger.debug("CharResponseWrapper initialized.");
    }

    @Override
    public PrintWriter getWriter() {
    	logger.debug("Writer requested.");
    	writerUsed = true;
        return new PrintWriter(output);
    }
    
    @Override
    public ServletOutputStream getOutputStream(){
    	logger.debug("Outputstream requested.");
		return stream;    	
    }
}
