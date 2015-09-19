import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;


public class TextBuddyTest {
	
	static TextBuddy textBuddy = new TextBuddy();
	static String fileName = "myTestFile.txt";
	String output = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// set up TextBuddy
		textBuddy.createFile(fileName);
	}

	@Test
	public void testAddText() {
		
		// Test null case
		textBuddy.executeCommand("clear");
		textBuddy.executeCommand("add ");
		textBuddy.executeCommand("add ");
		textBuddy.executeCommand("add ");
		textBuddy.executeCommand("sort");
		output = textBuddy.executeCommand("display");
		assertEquals("myTestFile.txt is empty", output);
		
		textBuddy.executeCommand("clear");
		textBuddy.executeCommand("add a");
		textBuddy.executeCommand("add b");
		textBuddy.executeCommand("add c");
		output = textBuddy.executeCommand("display"); 
		assertEquals("1. a\n2. b\n3. c", output);
	}

	@Test
	public void testSort() {
		String output = null;
		
		textBuddy.executeCommand("clear");
		textBuddy.executeCommand("add b");
		textBuddy.executeCommand("add a");
		textBuddy.executeCommand("add c");
		textBuddy.executeCommand("sort");
		output = textBuddy.executeCommand("display");
		assertEquals("1. a\n2. b\n3. c", output);
	}

	@Test
	public void testSearchText() {
		TextBuddy.executeCommand("clear");
		TextBuddy.executeCommand("add c");
		TextBuddy.executeCommand("add b");
		TextBuddy.executeCommand("add a");
		output = TextBuddy.executeCommand("search a");
		assertEquals("1. a", output);
	}

}
