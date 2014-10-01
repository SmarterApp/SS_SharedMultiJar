/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebValueCollection extends CaseInsensitiveMap { 

	private static final long serialVersionUID = -8189580537291609856L;
	private static final Logger _logger      = LoggerFactory.getLogger (WebValueCollection.class);


	@Override
	public String toString() {
        return this.toString(true, null);
    }

    public String toString(boolean urlencoded) {
        return this.toString(urlencoded, null);
    }

    public String toString(boolean urlencoded, Map<String, Object> excludeKeys) {
        int count = this.size();
        
        if (count == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        MapIterator iter = this.mapIterator();
        while (iter.hasNext()) {
        	String key = (String) iter.getKey();

            if (((excludeKeys == null) || (key == null)) || (excludeKeys.get(key) == null)) {
                String str3;
                if (urlencoded) {
					try {
						byte[] utf8Bytes = key.getBytes("UTF8");
	                    key = new String(utf8Bytes, "UTF8");
					} catch (UnsupportedEncodingException e) {
						_logger.error(e.getMessage());
						key = null;
					}
                }
                String str2 = !StringUtils.isEmpty(key) ? (key + "=") : "";
            	
                if (builder.length() > 0) {
                    builder.append('&');
                }
                
                Object value = iter.getValue();
                if (value instanceof List) {
                	List list = (List) value;
                	int num3 = (list != null) ? list.size() : 0;
                	if (num3 == 1) {
                        builder.append(str2);
                        str3 = list.get(0).toString();
                        if (urlencoded) {
    						try {
    							byte[] utf8Bytes = str3.getBytes("UTF8");
    							str3 = new String(utf8Bytes, "UTF8");
    						} catch (UnsupportedEncodingException e) {
    							_logger.error(e.getMessage());
    							str3 = null;
    						}
                        	
                        }
                        builder.append(str3);
                    }
                    else if (num3 == 0) {
                        builder.append(str2);
                    }
                    else {
                        for (int j = 0; j < num3; j++) {
                            if (j > 0) {
                                builder.append('&');
                            }
                            
                            builder.append(str2);
                            str3 = list.get(j).toString();
                            
                            if (urlencoded) {
                            	try {
        							byte[] utf8Bytes = str3.getBytes("UTF8");
        							str3 = new String(utf8Bytes, "UTF8");
        						} catch (UnsupportedEncodingException e) {
        							_logger.error(e.getMessage());
        							str3 = null;
        						}
                            }
                            
                            builder.append(str3);
                        }
                    }
                }
                else {
                	builder.append(str2);
                    str3 = value.toString();
                    if (urlencoded) {
						try {
							byte[] utf8Bytes = str3.getBytes("UTF8");
							str3 = new String(utf8Bytes, "UTF8");
						} catch (UnsupportedEncodingException e) {
							_logger.error(e.getMessage());
							str3 = null;
						}                    	
                    }
                    builder.append(str3);
                }
                                
                
            }
        }

        return builder.toString();
    }
    
    @Override
    public Object put(Object key, Object value) {
        if (key == null || StringUtils.isEmpty(key.toString()) || value == null) return null;
        return super.put(key.toString(), value);
    }

	
}


