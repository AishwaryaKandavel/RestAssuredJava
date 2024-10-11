package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;

public class GraphQLScript {
	public static void main(String args[]) {
		//Query
		RestAssured.baseURI = "https://rahulshettyacademy.com/gq/graphql";
		String res = given().log().all().header("Content-Type", "application/json")
		.body("{\"query\":\"query($characterId: Int!, $locationId: Int!, $episodeId: Int!, $country: String!, $region: String!){\\n  character(characterId: $characterId){\\n    name\\n    status\\n    species\\n  }\\n  location(locationId: $locationId){\\n    name\\n    type\\n    id\\n    residents{\\n      id\\n      name\\n    }\\n  }\\n  episode(episodeId: $episodeId){\\n    name\\n    air_date\\n  }\\n  locations(filters: {name: $country, type: $region}){\\n    info{\\n      count\\n    }\\n    result{\\n      name\\n      type\\n      id\\n      residents{\\n        id\\n        name\\n      }\\n    }\\n  }\\n}\",\"variables\":{\"characterId\":47,\"locationId\":24,\"episodeId\":9,\"country\":\"India\",\"region\":\"Eastern\"}}")
		.when().log().all().post()
		.then().log().all().extract().asString();
		
		System.out.println(res);
		JsonPath js = new JsonPath(res);
		System.out.println(js.getString("data.character.name"));
		
		//Mutation
		RestAssured.baseURI = "https://rahulshettyacademy.com/gq/graphql?";
		res = given().log().all().header("Content-Type", "application/json")
		.body("{\"query\":\"mutation{\\n  createLocation(location: {name:\\\"Dublin\\\", type:\\\"Country\\\", dimension:\\\"5000\\\"}){\\n    id\\n  }\\n  \\n}\",\"variables\":{\"characterId\":47,\"locationId\":24,\"episodeId\":9,\"country\":\"India\",\"region\":\"Eastern\"}}")
		.when().log().all().post()
		.then().log().all().extract().asString();
		
		System.out.println(res);
		js = new JsonPath(res);
		System.out.println(js.getString("data.createLocation.id"));
		
	}

}
