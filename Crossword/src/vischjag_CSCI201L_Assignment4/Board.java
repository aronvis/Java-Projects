package vischjag_CSCI201L_Assignment4;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;

public class Board 
{
	Gamedata gamedata;
	String [][] gameBoard;
	int [][] stringCount;
	int boardHeight = 0;
	int boardWidth = 0;
	int numWords = 0;
	int wordsRemaining;
	int [] indexCount;
	HashSet<Integer> commonIndices;
 	ArrayList<String> availableAcrossWords;
	ArrayList<String> availableDownWords;
	Stack<String> usedAcrossWords;
	Stack<String> usedDownWords;
	boolean successful = true;
	Board(Gamedata gamedata)
	{
		this.gamedata = gamedata;
		computeHeight();
		computeWidth();
		createBoards();
		initializeIndexCounter();
		countIndices();
		storeCommonIndices();
		if(validIndexIntersection() == true)
		{
			initializeStorage();
			addAcrossWords();
			addDownWords();
			setWordsRemaining();
			placeFirstWord();
			sortByShortest(availableAcrossWords);
			sortByShortest(availableDownWords);
			createBoard(wordsRemaining,"d");
			removeSolution();
		}
	}
	void placeFirstWord()
	{
		String word = availableAcrossWords.get(0).substring(1);
		int index = Integer.parseInt(availableAcrossWords.get(0).substring(0,1));
		usedAcrossWords.push(availableAcrossWords.get(0));
		availableAcrossWords.remove(0);
		wordsRemaining--;
		placeWord(boardHeight/3, boardWidth/3, word, "a", index);
		if(commonIndices.contains(index))
		{
			for(int i=0; i<availableDownWords.size(); i++)
			{
				if(Integer.parseInt(availableDownWords.get(i).substring(0,1)) == index)
				{
					word = availableDownWords.get(i).substring(1);
					usedDownWords.push(index+word);
					availableDownWords.remove(i);
					wordsRemaining--;
					placeWord(boardHeight/3, boardWidth/3, word, "d", index);
					break;
				}
			}
		}
	}
	void createBoard(int wordsRemaining, String direction)
	{
		String word;
		int index;
		if(wordsRemaining == 0)
		{
			return;
		}
		if(availableAcrossWords.size() == 0)
		{
			direction = "d";
		}
		if(availableDownWords.size() == 0)
		{
			direction = "a";
		}
		if(direction == "a") 
		{
			word = availableAcrossWords.get(0).substring(1);
			index = Integer.parseInt(availableAcrossWords.get(0).substring(0,1));
			usedAcrossWords.push(availableAcrossWords.get(0));
			availableAcrossWords.remove(0);
			wordsRemaining--;
		}
		else
		{
			direction = "d";
			word = availableDownWords.get(0).substring(1);
			index = Integer.parseInt(availableDownWords.get(0).substring(0,1));
			usedDownWords.push(availableDownWords.get(0));
			availableDownWords.remove(0);
			wordsRemaining--;
		}
		for(int i=0; i<boardHeight; i++)
		{
			for(int j=0; j<boardWidth; j++)
			{
				if(validIntersection(i, j, word, direction)) 
				{
					if(placeWord(i, j, word, direction, index))
					{
						if(direction == "a")
						{
							createBoard(wordsRemaining, "d");
							if((availableDownWords.size() == 0) && (availableDownWords.size() == 0))
							{
								return;
							}
							removeWord(i, j, word, direction, index);
						}
						else	
						{
							createBoard(wordsRemaining, "a");
							if((availableDownWords.size() == 0) && (availableDownWords.size() == 0))
							{
								return;
							}
							removeWord(i, j, word, direction, index);
						}
					}
				}
			}
		}
		if((direction == "a") && (wordsRemaining > 0))
		{		
			String value = usedAcrossWords.pop();
			availableAcrossWords.add(value);
			wordsRemaining++;
			createBoard(wordsRemaining, "d");
		}
		else if((direction == "d") && (wordsRemaining > 0))
		{
			String value = usedDownWords.pop();
			availableDownWords.add(value);
			wordsRemaining++;
			createBoard(wordsRemaining, "a");
		}
	}
	boolean isValid()
	{
		return successful;
	}
	void createBoards()
	{
		gameBoard = new String [boardHeight][boardWidth];
		stringCount = new int [boardHeight][boardWidth];
		for(int i=0; i<boardHeight; i++)
		{
			Arrays.fill(gameBoard[i], "   ");
		}
	}
	void computeHeight()
	{
		for(Problem item : gamedata.data.get("a").values())
		{
			boardHeight += item.getAnswer().length();
			numWords++;
		}
	}
	void computeWidth()
	{
		for(Problem item : gamedata.data.get("d").values())
		{
			boardWidth += item.getAnswer().length();
			numWords++;
		}
	}
	void initializeStorage()
	{
	 	availableAcrossWords = new ArrayList<String>();
		availableDownWords = new ArrayList<String>();
		usedAcrossWords = new Stack<String>();
		usedDownWords = new Stack<String>();
	}
	void addAcrossWords()
	{
		for(Problem item : gamedata.data.get("a").values())
		{
			availableAcrossWords.add(item.getIndex() + item.getAnswer());
		}
	}
	void addDownWords()
	{
		for(Problem item : gamedata.data.get("d").values())
		{
			availableDownWords.add(item.getIndex() + item.getAnswer());
		}
	}
	void setWordsRemaining()
	{
		wordsRemaining = numWords;
	}
	void countIndices()
	{
		for(Problem item : gamedata.data.get("a").values())
		{
			indexCount[item.getIndex()-1]++;
		}
		for(Problem item : gamedata.data.get("d").values())
		{
			indexCount[item.getIndex()-1]++;
		}
	}
	void initializeIndexCounter()
	{
		indexCount = new int[numWords];
	}
	void storeCommonIndices()
	{
		commonIndices = new HashSet<Integer>();
		for(int i=0; i<indexCount.length; i++)
		{
			if(indexCount[i] > 1)
			{
				commonIndices.add(i+1);
			}
		}
		indexCount = null;	
	}
	boolean validIndexIntersection()
	{
		for(Integer value : commonIndices)
		{
			char firstLetterAcross = gamedata.data.get("a").get(value).getAnswer().charAt(0);
			char firstLetterDown = gamedata.data.get("d").get(value).getAnswer().charAt(0);
			if(firstLetterAcross != firstLetterDown)
			{
				successful = false;
				return false;
			}
		}
		return true;
	}
	void sortByLongest(ArrayList<String> list)
	{
		list.sort(Comparator.comparingInt(String::length).reversed());
	}
	void sortByShortest(ArrayList<String> list)
	{
		list.sort(Comparator.comparingInt(String::length));
	}
	boolean placeWord(int startRow, int startColumn, String word, String direction, int index)
	{
		int counter = 0;
		String value;
		if(direction == "a")
		{
			for(int j=startColumn; j<(startColumn+word.length()); j++)
			{
				if(j == startColumn)
				{
					value = " " + index + word.charAt(counter);
				}
				else
				{
					value = "  " + word.charAt(counter);
				}
				if(invalidInsertion(startRow,j,value))
				{
					if(counter > 0)
					{
						removeWord(startRow, startColumn, word.substring(0, counter), direction, index);
					}
					return false;
				}
				gameBoard[startRow][j] = value;
				stringCount[startRow][j]++;
				counter++;
			}
		}
		else
		{
			for(int i=startRow; i<(startRow+word.length()); i++)
			{
				if(i == startRow)
				{
					value = " " + index + word.charAt(counter);
				}
				else
				{
					value = "  " + word.charAt(counter);
				}
				if(invalidInsertion(i,startColumn,value))
				{
					if(counter > 0)
					{
						removeWord(startRow, startColumn, word.substring(0, counter), direction, index);
					}
					return false;
				}
				gameBoard[i][startColumn] = value;
				stringCount[i][startColumn]++;
				counter++;
			}
		}
		gamedata.data.get(direction).get(index).setLocation(startRow, startColumn);
		return true;
	}
	boolean invalidInsertion(int startRow, int startColumn, String value)
	{
		if((gameBoard[startRow][startColumn] != "   ") && (!value.contains(gameBoard[startRow][startColumn].trim())))
		{
			return true;
		}
		return false;
	}
	boolean validIntersection(int startRow, int startColumn, String word, String direction)
	{
		if(direction == "a")
		{
			if(startColumn+word.length()-1 > boardWidth-1)
			{
				return false;
			}
			for(int j=startColumn; j<(startColumn+word.length()); j++)
			{
				if(stringCount[startRow][j] >= 1)
					return true;
			}
		}
		else
		{
			if(startRow+word.length()-1 > boardHeight-1)
			{
				return false;
			}
			for(int i=startRow; i<(startRow+word.length()); i++)
			{
				if(stringCount[i][startColumn] >= 1)
					return true;
			}
		}
		return false;
	}
	void removeWord(int startRow, int startColumn, String word, String direction, int index)
	{
		if(direction == "a")
		{
			for(int j=startColumn; j<(startColumn+word.length()); j++)
			{
				if(stringCount[startRow][j] == 1)
				{
					gameBoard[startRow][j] = "   ";
				}
				else if((j == startColumn) && (gameBoard[startRow-1][j] != "   "))
				{
					gameBoard[startRow][j] = "  " + gameBoard[startRow][j].substring(2);
				}
				stringCount[startRow][j]--;
			}
		}
		else
		{
			for(int i=startRow; i<(startRow+word.length()); i++)
			{
				if(stringCount[i][startColumn] == 1)
				{
					gameBoard[i][startColumn] = "   ";
				}
				else if((i == startRow) && (gameBoard[i][startColumn-1] != "   "))
				{
					gameBoard[i][startColumn] = "  " + gameBoard[i][startColumn].substring(2);
				}
				stringCount[i][startColumn]--;
			}
		}
		gamedata.data.get(direction).get(index).removeLocation();
	}
	void printBoard()
	{
		for(int i=0; i<boardHeight; i++)
		{
			for(int j=0; j<boardWidth; j++)
			{
				gamedata.game.messageClientsEdit(gameBoard[i][j]);
				if(j == boardWidth - 1)
				{
					gamedata.game.messageClientsEdit("\n");
				}
			}
		}
	}
	void removeSolution()
	{
		for(int i=0; i<boardHeight; i++)
		{
			for(int j=0; j<boardWidth; j++)
			{
				if(gameBoard[i][j].matches("^\\s+\\w+$"))
				{
					gameBoard[i][j] = gameBoard[i][j].substring(0,2) + "-";
				}
			}
		}
	}
	void printStringCount()
	{
		for(int i=0; i<boardHeight; i++)
		{
			for(int j=0; j<boardWidth; j++)
			{
				System.out.print(stringCount[i][j]);
				if(j == boardWidth - 1)
				{
					System.out.println();
				}
			}
		}
	}
}

