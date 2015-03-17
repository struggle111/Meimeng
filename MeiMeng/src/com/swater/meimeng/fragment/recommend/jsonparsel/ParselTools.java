package com.swater.meimeng.fragment.recommend.jsonparsel;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParselTools {
	static ParselTools ins=new ParselTools();
	public static ParselTools getInstance(){
		
		return ins;
	}
	private ParselTools() {
		
	}
	public void readJson2Map(String strjson) {

	    String json = strjson;

	    try {
	    	ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, Map<String, Object>> maps = objectMapper.readValue(json, Map.class);

	        System.out.println(maps.size());

	        Set<String> key = maps.keySet();

	        Iterator<String> iter = key.iterator();

	        while (iter.hasNext()) {

	            String field = iter.next();

	            System.out.println(field + ":" + maps.get(field));

	        }
	        
	        

	    } catch (JsonParseException e) {

	        e.printStackTrace();

	    } catch (JsonMappingException e) {

	        e.printStackTrace();

	    } catch (IOException e) {

	        e.printStackTrace();

	    }

	}
	public static BeanUser parsel_UserInfo(String strjson){
        ObjectMapper mapper = new ObjectMapper();
        BeanUser bean=null;
        try {
        	byte[] content=strjson.getBytes();
             bean = mapper.readValue(content, BeanUser.class);

    //读取 

            System.out.println("Country:"+bean.getVip_level());
            System.out.println("CountryInfo:"+bean.getBase_info().getUid());
            System.out.println("orgood:"+bean.getBase_info().getHeight());        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return bean;
    }
}
