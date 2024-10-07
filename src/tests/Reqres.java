package tests;

import static io.restassured.RestAssured.given;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import utils.UtilityFunctions;

public class Reqres {

	String baseURI = "https://reqres.in/";

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = baseURI;
	}

	@Test
	public void getListOfUsers() {
		String getListOfUsersRes = given().log().all().queryParam("page", "2")
				.when().get("/api/users").then().log()
				.all().assertThat().statusCode(200).extract().asString();

		JsonPath jsp = UtilityFunctions.rawToJSON(getListOfUsersRes);
		System.out.println(jsp.get().toString());
	}
}
