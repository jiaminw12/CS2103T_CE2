import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * This class is used to store and retrieve the text from storage. The command
 * format is given by the example interaction below:
 * 
 * c:>java TextBuddy mytextfile.txt Welcome to TextBuddy. mytextfile.txt is
 * ready for use command: add little brown fox added to mytextfile.txt: ¡°little
 * brown fox¡± command: display 1. little brown fox command: add jumped over the
 * moon added to mytextfile.txt: ¡°jumped over the moon¡± command: display 1.
 * little brown fox 2. jumped over the moon command: delete 2 deleted from
 * mytextfile.txt: ¡°jumped over the moon¡± command: display 1. little brown fox
 * command: clear all content deleted from mytextfile.txt command: display
 * mytextfile.txt is empty command: exit
 * 
 * @author Wong Jia Min
 */

public class TextBuddy {

	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. %1$s is ready for use";
	private static final String MESSAGE_ADDED = "added to %1$s : \"%2$s\"";
	private static final String MESSAGE_DELETED = "deleted from %1$s : \"%2$s\"";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final String MESSAGE_DISPLAY_EMPTY = "%1$s is empty";
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format : %1$s";
	private static final String MESSAGE_INVALIDNUM_FORMAT = "invalid num : %1$s";

	static File fileInput;
	static int totalLineNum = 0;

	// These are the possible command types
	enum COMMAND_TYPE {
		ADD, DISPLAY, DELETE, CLEAR, SORT, SEARCH, EXIT, INVALID
	};

	private static Scanner _scanner = new Scanner(System.in);

	public static void main(String[] args) {
		createFile(args[0]);
		while (true) {
			System.out.print("command: ");
			String userCommand = _scanner.nextLine();
			showToUser(executeCommand(userCommand));
		}
	}

	private static void showToUser(String text) {
		System.out.println(text);
	}

	public static String executeCommand(String userCommand) {

		String commandTypeString = getFirstWord(userCommand);
		String result = null;
		
		if (commandTypeString == null)
			throw new Error("command type string cannot be null!");

		if (commandTypeString.equalsIgnoreCase("add")) {
			return addText(userCommand);
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return display();
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return deleteText(userCommand);
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return clearText();
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
			sort();
			return display();
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return searchText(userCommand);
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			System.exit(0);
			result = null;
		} else {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}
		return result;
	}

	public static void createFile(String file) {
		fileInput = new File(file);
		try {
			fileInput.createNewFile();
			showToUser(String.format(WELCOME_MESSAGE, file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This operation is used to add text into storage
	 * 
	 * @param userCommand
	 *            is the full string user has entered as the command
	 * @return the message
	 */
	public static String addText(String userCommand) {
		try {
			if (removeFirstWord(userCommand).equals("")) {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			} else {
				FileWriter fw = new FileWriter(fileInput, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(removeFirstWord(userCommand) + "\r\n");
				bw.close();
				fw.close();
				totalLineNum++;
			}
		} catch (IOException e) {
			showToUser(e.getMessage());
		}
		return String.format(MESSAGE_ADDED, fileInput.getName(),
				removeFirstWord(userCommand));
	}

	/**
	 * This operation is used to delete text from storage
	 * 
	 * @param userCommand
	 *            is the a digit that user has entered as the command
	 * @return the message
	 */
	public static String deleteText(String userCommand) {
		String deletedLine = "";
		if (removeFirstWord(userCommand).equals("")) {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		} else if (Integer.valueOf(removeFirstWord(userCommand)) > totalLineNum) {
			return String.format(MESSAGE_INVALIDNUM_FORMAT, userCommand);
		} else {
			int lineNum = Integer.valueOf(removeFirstWord(userCommand));
			try {
				File tmp = File.createTempFile("tmp", "");  // create a temp file
				BufferedReader br = new BufferedReader(new FileReader(
						fileInput.getName()));
				BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

				for (int i = 1; i < lineNum; i++) {
					bw.write(String.format("%s%n", br.readLine()));
				}

				deletedLine = br.readLine();

				String l;
				// skip the line == deletedLine
				while (null != (l = br.readLine())) {
					bw.write(String.format("%s%n", l));
				}

				br.close();
				bw.close();
				
				//Replace the temp to fileInput
				File oldFile = new File(fileInput.getName());
				oldFile.setWritable(true);
				if (oldFile.delete()) {
					tmp.renameTo(oldFile);
				}
				
				totalLineNum--;

			} catch (IOException e) {
				showToUser(e.getMessage());
			}
		}
		return String.format(MESSAGE_DELETED, fileInput.getName(),
				deletedLine);
	}

	/**
	 * This operation is used to display all text from storage
	 * 
	 * @return the content
	 */
	public static String display() {
		StringBuilder sb = new StringBuilder();
		try {
			FileReader fr = new FileReader(fileInput);
			BufferedReader br = new BufferedReader(fr);
			String sCurrentLine = null;
			int i = 1;
			if ((sCurrentLine = br.readLine()) == null) {
				return String.format(MESSAGE_DISPLAY_EMPTY,
						fileInput.getName());
			} else {
				while (sCurrentLine != null) {
					sb.append(i + ". " + sCurrentLine + "\n");
					i++;
					sCurrentLine = br.readLine();
				}
			}
			fr.close();
			br.close();
			
			if (sb.length() > 0){
				sb.setLength(sb.length() - 1);
			}
			
		} catch (IOException exception) {
			showToUser(exception.getMessage());
		}
		return sb.toString();
	}

	/**
	 * This operation is used to clear all text in storage
	 * 
	 * @return the message
	 */
	public static String clearText() {
		try {
			FileWriter fw = new FileWriter(fileInput);
			fw.flush();
			fw.close();
			totalLineNum = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.format(MESSAGE_CLEAR, fileInput.getName());
	}

	public static void sort() {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		String inputLine;
		int i = 1;
		ArrayList<String> lineList = new ArrayList<String>();
		try {
			fileReader = new FileReader(fileInput);
			bufferedReader = new BufferedReader(fileReader);
			while ((inputLine = bufferedReader.readLine()) != null) {
				lineList.add(inputLine);
			}
			Collections.sort(lineList);
			fileReader.close();

			FileWriter fileWriter = new FileWriter(fileInput);
			fileWriter.flush();
			PrintWriter out = new PrintWriter(fileWriter);
			for (String outputLine : lineList) {
				out.println(outputLine);
			}
			out.flush();
			out.close();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			showToUser(e.getMessage());
		} catch (IOException e) {
			showToUser(e.getMessage());
		}
	}

	public static String searchText(String userCommand) {
		StringBuilder sb = new StringBuilder();
		if (removeFirstWord(userCommand).equals("")) {
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		} else {
			int i = 1;
			String word = removeFirstWord(userCommand);
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			String inputLine;
			ArrayList<String> lineList = new ArrayList<String>();
			try {
				fileReader = new FileReader(fileInput);
				bufferedReader = new BufferedReader(fileReader);
				while ((inputLine = bufferedReader.readLine()) != null) {
					lineList.add(inputLine);
				}
				fileReader.close();
				for (String value : lineList) {
					if (value.contains(word)) {
							sb.append(i + ". " + value + "\n");
							i++;
						}
				}
				if (i == 1){
					System.out.println("No results!");
				} else {
					sb.setLength(sb.length() - 1);
				}
			} catch (FileNotFoundException e) {
				showToUser(e.getMessage());
			} catch (IOException e) {
				showToUser(e.getMessage());
			}
		}
		return sb.toString();
	}

	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}

	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}

}
