package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import utils.ExcelHandler;
import utils.UtilityFunctions;

import static io.restassured.RestAssured.*;
//import static org.hamcrest.Matchers.*; body("scope", equalTo("val") for equalTo

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import data.Payload;

public class Library {
	
	String baseURI = "http://216.10.245.166";
	String ID = "";
	String book_name = "";
	String isbn = "";
	String aisle = "";
	String author = "";

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = baseURI;
	}

	@Test(dataProvider = "addBooks")
	public void testE2E(HashMap<Object, Object> obj) {
		book_name = (String) obj.get("book_name");
		isbn = (String) obj.get("isbn");
		aisle = (Integer) obj.get("aisle")+"";
		author = (String) obj.get("author");
		ID = isbn+aisle;
		
		addBook();
		getBook();
		deleteBook();
	}
	
	public void addBook() {
		String addBookRes = given().log().all().header("Content-Type", "application/json")
				.body(Payload.addBook(book_name, isbn, aisle, author)).when().post("/Library/Addbook.php").then().log()
				.all().assertThat().statusCode(200).extract().asString();

		JsonPath jsp = UtilityFunctions.rawToJSON(addBookRes);
		String responseMsg = jsp.getString("Msg");
		if(responseMsg.equalsIgnoreCase("Book Already Exists")) {
			deleteBook();
			addBook();
		}else {
			Assert.assertTrue(responseMsg.equalsIgnoreCase("successfully added"));
		}
		ID = jsp.getString("ID");
	}

	public void getBook() {
		String getBookRes = given().log().all().queryParam("ID", ID).when().post("/Library/GetBook.php").then().log()
				.all().assertThat().statusCode(200).extract().asString();
		JsonPath jsp = UtilityFunctions.rawToJSON(getBookRes);
		Assert.assertTrue(jsp.getString("[0].book_name").toString().equalsIgnoreCase(book_name));
		Assert.assertTrue(jsp.getString("[0].isbn").toString().equalsIgnoreCase(isbn));
		Assert.assertTrue(jsp.getString("[0].aisle").toString().equalsIgnoreCase(aisle));
		Assert.assertTrue(jsp.getString("[0].author").toString().equalsIgnoreCase(author));
	}
	
	public void deleteBook() {
		String deleteBookRes = given().log().all().header("Content-Type", "application/json")
				.body(Payload.deleteBook(ID)).when().post("/Library/DeleteBook.php").then().log().all().assertThat()
				.statusCode(200).extract().asString();
		JsonPath jsp = UtilityFunctions.rawToJSON(deleteBookRes);
		Assert.assertTrue(jsp.getString("msg").equalsIgnoreCase("book is successfully deleted"));
	}
	
	@DataProvider(name = "addBooks")
	public Object[][] feedData() throws Exception {
		List<HashMap<Object, Object>> data = ExcelHandler.getExcelDataToMap("addBooks", "data");
		return IntStream.range(0, data.size()).mapToObj(i->new Object[] {data.get(i)}).toArray(Object[][]::new);
	}
}
