package tests;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import pojo.Data;
//import io.restassured.path.json.JsonPath;
import pojo.Resources;
//import utils.UtilityFunctions;

public class Reqres {

	String baseURI = "https://reqres.in/";

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = baseURI;
	}

//	@Test
//	public void getListOfUsers() {
//		String getListOfUsersRes = given().log().all().queryParam("page", "2")
//				.when().get("/api/users").then().log()
//				.all().assertThat().statusCode(200).extract().asString();
//
//		JsonPath jsp = UtilityFunctions.rawToJSON(getListOfUsersRes);
//		System.out.println(jsp.get().toString());
//	}
	
	@Test
	public void getListOfResources() {
		Resources getListOfUsersRes = given().log().all()
				.expect().defaultParser(Parser.JSON)
				.when().get("/api/unknown").as(pojo.Resources.class);

		System.out.println("Total: "+getListOfUsersRes.getTotal());
		List<Data> data = getListOfUsersRes.getData();
		for (Data dataObj : data) {
			System.out.println(dataObj.getId()+". "+dataObj.getName()+" - "+dataObj.getYear()+" - "+dataObj.getColor()+" - "+dataObj.getPantone_value());
		}
	}
}
