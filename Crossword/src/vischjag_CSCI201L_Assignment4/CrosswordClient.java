package vischjag_CSCI201L_Assignment4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CrosswordClient extends Thread 
{
	private BufferedReader br;
	private PrintWriter pw;
	boolean validConnection = false;
	String hostname;
	int port;
	Scanner sc = new Scanner(System.in);
	Socket s;
	public CrosswordClient() 
	{	
		System.out.println("Welcome to 201 Crossword!");
		while(!validConnection)
		{
			System.out.print("Enter the server hostname: ");
			hostname = sc.next();
			port = validateIntInput("Enter the server port: ");
			connectSocket();
		}
		try
		{
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
		} 
		catch (IOException e) 
		{
			System.out.println("IOException in CrosswordClient: " + e.getMessage());
		}
		this.start();
		while(true) 
		{
			String line = "";
			while(line.equals(""))
			{
				line = sc.nextLine();
			}	
			pw.println(line);
			pw.flush();
		}	
	}
	// The method will run an infinite loop 
	// until the user gives an integer as input
	int validateIntInput(String Question)
	{
		int storageVar = 0;
		boolean isValid = false;
		while(!isValid)
		{
			System.out.print(Question);
			try
			{
				storageVar = sc.nextInt();
				isValid = true;
			}
			catch(InputMismatchException e)
			{
				System.out.println("The value entered was not an integer.");
				System.out.println("Please give a valid input.");
				sc.nextLine();
			}
		}
		return storageVar;
	}
	
	// Attempts to connect the player socket to the crossword server socket
	void connectSocket()
	{
		try 
		{
			s = new Socket(hostname, port);
			validConnection = true;
		} 
		catch (UnknownHostException e) 
		{
			System.out.println("UnknownHostException in CrosswordClient constructor: " + e.getMessage());
			System.out.println("Please try to reconnect.");
		} 
		catch (IOException e) 
		{
			System.out.println("IOException in CrosswordClient constructor: " + e.getMessage());
			System.out.println("Please try to reconnect.");
		}
	}
	
	// Runs the thread
	public void run() 
	{
		try 
		{
			while(true) 
			{
				String line = br.readLine();
				System.out.println(line);
			}
		} 
		catch (IOException ioe) 
		{
			System.out.println("ioe in CrosswordClient.run(): " + ioe.getMessage());
		}
	}
	public static void main(String [] args) 
	{
		CrosswordClient cc = new CrosswordClient();
	}
}
