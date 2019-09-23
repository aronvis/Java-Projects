package src_files;
import java.util.HashMap;
public class Gamedata 
{
	Gamedata(Crossword game)
	{
		data = new HashMap<String, HashMap<Integer, Problem>>();
		HashMap<Integer, Problem> across = new  HashMap<Integer, Problem>();
		HashMap<Integer, Problem> down = new  HashMap<Integer, Problem>();
		data.put("a", across);
		data.put("d", down);
		this.game = game;
	}
	Crossword game;
	Board board;
	HashMap<String, HashMap<Integer, Problem>> data;
	void insertItem(String key, Integer index, String question, String answer)
	{
		data.get(key).put(index, new Problem(question, answer.toLowerCase(), index));
	}
	void printQuestions()
	{
		if(data.get("a").size() > 0)
		{
			game.messageClients("Across");
			for(Problem item : data.get("a").values())
			{
				game.messageClients(item.getQuestion());
			}
		}
		if(data.get("d").size() > 0)
		{
			game.messageClients("Down");
			for(Problem item : data.get("d").values())
			{
				game.messageClients(item.getQuestion());
			}
		}
	}
	boolean renderBoard()
	{
		board = new Board(this);
		return board.isValid();
	}
	boolean validateKey(String key)
	{
		if((data.containsKey(key)) && (data.get(key).size() > 0))
		{
			return true;
		}
		return false;
	}
	
	boolean validateIndex(String key, Integer index)
	{
		if(data.get(key).containsKey(index))
		{
			return true;
		}
		return false;
	}
	boolean validateAnswer(String key, Integer index, String question, String answer)
	{
		if(data.get(key).get(index).getAnswer() == answer.toLowerCase())
		{
			return true;
		}
		return false;
	}
}
