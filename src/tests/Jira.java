package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import data.Payload;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;
import pojo.CreateIssueJira;
import pojo.GetIssueJira;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Jira {
	String baseURI = "https://aishwaryakandavel.atlassian.net/";
	String createIssueResource = "rest/api/3/issue/";
	String attachmentIssueResource = createIssueResource+"{issueId}/attachments";
	String getIssueResource = createIssueResource+"{issueId}";
	String id, key, self = "";
	String currDir = System.getProperty("user.dir");
	protected static Properties prop = new Properties();
	RequestSpecification spec;
	@BeforeClass
	public void setUp() {
		try {
			FileInputStream fis = new FileInputStream(
					new File(System.getProperty("user.dir") + "/src/data/runConfig.properties"));
			prop.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		spec = new RequestSpecBuilder().setBaseUri(baseURI)
				.addHeader("Authorization", prop.getProperty("token")).build();
	}
	@Test
	public void createIssue() {
		CreateIssueJira createIssueJira = given().log().all().header("Content-Type", "application/json")
		.spec(spec)
		.body(Payload.staticApiRequest("CreateIssue"))
		.when().log().all().post(createIssueResource)
		.then().log().all().assertThat()
		.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("CreateIssue.json"))
		.statusCode(201).extract().as(pojo.CreateIssueJira.class);
		
		this.id = createIssueJira.getId();
		this.key = createIssueJira.getKey();
		this.self = createIssueJira.getSelf();
	}
	
	@Test(dependsOnMethods = "createIssue")
	public void addAttachment() {
		given().log().all().header("X-Atlassian-Token", "no-check")
		.pathParam("issueId", this.id)
		.spec(spec)
		.multiPart("file", new File(currDir+"/src/test/resources/incorrectTotal.png"))
		.when().log().all().post(attachmentIssueResource)
		.then().log().all().assertThat()
		.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("AddAttachment.json"))
		.statusCode(200);
	}
	
	@Test(dependsOnMethods = "addAttachment")
	public void getIssue() {
		GetIssueJira getIssueJira = given().log().all()
		.spec(spec)
		.pathParam("issueId", this.id)
		.when().log().all().get(getIssueResource)
		.then().log().all().assertThat().statusCode(200).extract().as(pojo.GetIssueJira.class);
		
		Assert.assertEquals(getIssueJira.getId(), this.id);
		Assert.assertEquals(getIssueJira.getSelf(), this.self);
		Assert.assertEquals(getIssueJira.getKey(), this.key);
	}
	
	@Test(dependsOnMethods = "getIssue")
	public void deleteIssue() {
		given().log().all().spec(spec)
		.pathParam("issueId", this.id)
		.when().log().all().delete(getIssueResource)
		.then().log().all().assertThat().statusCode(204);
	}
}
