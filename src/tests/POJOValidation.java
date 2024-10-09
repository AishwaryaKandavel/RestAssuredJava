package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import data.Payload;
import io.restassured.path.json.JsonPath;
import utils.UtilityFunctions;

public class POJOValidation {
	JsonPath jsp;
	
	@BeforeClass
	public void setUp() {
		jsp = UtilityFunctions.rawToJSON(Payload.staticApiRequest("MockAPIResponse"));
	}
	
	@Test
	public void tests() {
		System.out.println(jsp.getInt("courses.size()"));
		System.out.println(jsp.getInt("dashboard.purchaseAmount"));
		System.out.println(jsp.getString("courses.get(0).title"));
		int sum = 0;
		for(int i=0; i<jsp.getInt("courses.size()");i++) {
			String courseName = jsp.getString("courses.get("+i+").title");
			System.out.println(courseName);
			if(courseName.equals("RPA"))
				System.out.println(jsp.getInt("courses.get("+i+").price"));
			sum+= (jsp.getInt("courses.get("+i+").price")*jsp.getInt("courses.get("+i+").copies"));
		}
		System.out.println(sum);
	}
}
