package src_files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class RenderGame 
{
	boolean successful = true;
	Crossword game;
	Gamedata data;
	RenderGame(Crossword game)
	{
		System.out.println("Reading random game file.");
		this.game = game;
		selectFile("../Crossword/gamedata");
	}
	private void selectFile(String directory)
	{
		File file = new File(directory);
		File[] files = file.listFiles();
		if(files != null)
		{
			Random rand = new Random();
			int index = rand.nextInt(files.length);
			parseFile(files[index]);
		}
		else
		{
			System.out.println("No files in gamedata directory.");
			successful = false;
		}
	}
	
	void parseFile(File directory)
	{
		try 
		{
			@SuppressWarnings("resource")
			Scanner fileReader = new Scanner(directory);
			data = new Gamedata(game);
			BooleanObj foundAcross = new BooleanObj(false);
			BooleanObj foundDown = new BooleanObj(false);
			StringObj currentKey = new StringObj();
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				if(validFormatAndInsert(line, foundAcross, foundDown, currentKey, data) == false)
				{
						System.out.println("File: was not formatted correctly");
						successful = false;
						return;
				}
			}
			successful = data.renderBoard();
			if(successful == true)
			{
				System.out.println("File read successfully.");
			}
			else
			{
				System.out.println("Board did not render correctly");
			}
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("File not found exception " + e.getMessage());
		}
	}
	
	boolean validateHeader(String line, String headerString, BooleanObj viewedHeader, StringObj currentKey)
	{
		if(line.equals(headerString))
		{
			if(viewedHeader.value == false)
			{
				if(headerString.equals("ACROSS"))
					currentKey.value = "a";
				else
					currentKey.value = "d";
				viewedHeader.value = true;
				return true;
			}
			else
			{
				return false;
			}		
		}   
		return false;
	}
	
	boolean validFormatAndInsert(String line, BooleanObj foundAcross, BooleanObj foundDown, StringObj currentKey, Gamedata data)
	{
		if(line.contains("ACROSS"))
		{
			return validateHeader(line, "ACROSS", foundAcross, currentKey);
		}
		else if(line.contains("DOWN"))
		{
			return validateHeader(line, "DOWN", foundDown, currentKey);
		}
		String[] items = line.split("\\|");	
		try
		{
			if(line.matches("^\\d+\\|\\D+\\|\\D+$") == false)
				return false;
			Integer index = Integer.parseInt(items[0]);
			String answer = items[1];
			if(answer.contains(" ") == true)
				return false;
			String question = items[2];
			data.insertItem(currentKey.value, index, question, answer);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
	
	boolean validParse()
	{
		return successful;
	}
}
