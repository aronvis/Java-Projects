package src_files;

public class Problem 
{
	Problem(String question, String answer, Integer questionIndex)
	{
		this.question = question;
		this.answer = answer;
		this.questionIndex = questionIndex;
		this.questionString = questionIndex + " " + question;
	}
	String question;
	String answer;
	Integer questionIndex;
	String questionString;
	Integer boardColumn = -1;
	Integer boardRow = -1;
	String getQuestion()
	{
		return questionString;
	}
	String getAnswer()
	{
		return answer;
	}
	Integer getIndex()
	{
		return questionIndex;
	}
	void setLocation(Integer row, Integer column)
	{
		boardRow = row;
		boardColumn = column;
	}
	void removeLocation()
	{
		boardRow = -1;
		boardColumn = -1;
	}
}
