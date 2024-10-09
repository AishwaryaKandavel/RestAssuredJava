package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Payload {
	static String currDir = System.getProperty("user.dir");
	public static String addBook(String book_name, String isbn, String aisle, String author) {
		String res = "";
		try {
			res = new String(Files.readAllBytes(Path.of(currDir+"/src/data/payloadJSON/AddBook.json")));
			res = res.replaceAll("<book_name>", book_name);
			res = res.replaceAll("<isbn>", isbn);
			res = res.replaceAll("<aisle>", aisle);
			res = res.replaceAll("<author>", author);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static String deleteBook(String ID) {
		String res = "";
		try {
			res = new String(Files.readAllBytes(Path.of(currDir+"/src/data/payloadJSON/deleteBook.json")));
			res = res.replaceAll("<ID>", ID);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public static String staticApiRequest(String jsonName) {
		String res = "";
		try {
			res = new String(Files.readAllBytes(Path.of(currDir+"/src/data/payloadJSON/"+jsonName+".json")));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
