package jp.co.axa.apidemo.Util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class TestUtil {

	
	/** 
	 * Convert from Json to List
	 * @param json
	 * @param token
	 * @return List
	 */
	@SuppressWarnings("rawtypes")
	public static List jsonToList(String json, TypeToken token) {
		Gson gson = new Gson();
		return gson.fromJson(json, token.getType());
	}

	
	/** 
	 * Convert from Object to Json
	 * @param obj
	 * @return String
	 */
	public static String objectToJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	
	/** 
	 * Convert from Json to Object
	 * @param json
	 * @param classOf
	 * @return T
	 */
	public static <T> T jsonToObject(String json, Class<T> classOf) {
		Gson gson = new Gson();
		return gson.fromJson(json, classOf);
	}

	
	/** 
	 * Convert json to List
	 * @param jsonString
	 * @param cls
	 * @return List<T>
	 */
	public static <T> List<T> jsonToObjectList(String jsonString, Class<T> cls){
		List<T> list = new ArrayList<T>();
		try {
			Gson gson = new Gson();
			JsonArray arry = JsonParser.parseString(jsonString).getAsJsonArray();
			for (JsonElement jsonElement : arry) {
				list.add(gson.fromJson(jsonElement, cls));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
