package utils;

import io.restassured.path.json.JsonPath;

public class UtilityFunctions {
	public static JsonPath rawToJSON(String str) {
		return new JsonPath(str);
	}
}
