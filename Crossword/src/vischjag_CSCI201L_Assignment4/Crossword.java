package vischjag_CSCI201L_Assignment4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Crossword 
{
	private Vector<CrosswordServer> CrosswordServers;
	private Lock clientLock;
	private Vector<Condition> conditionItems;
	private int playersNeeded = 1000;
	private int currentNum = 0;
	private int nextThread = 0;
	private boolean fileRead = false;
	private Lock mainLock;
	private Condition mainCondition;
	private RenderGame game;
	public Crossword(int port) 
	{
		try 
		{
			while(true)
			{
				ServerSocket ss = new ServerSocket(port);
				System.out.println("Listening on port " + port + ".");
				CrosswordServers = new Vector<CrosswordServer>();
				clientLock = new ReentrantLock();
				conditionItems = new Vector <Condition>();
				mainLock = new ReentrantLock();
				mainCondition = mainLock.newCondition();			
				System.out.println("Waiting for players...");
				while(playersNeeded > currentNum)
				{
					Socket s = ss.accept();
					this.incrementCurrentNum();
					InetAddress address = s.getInetAddress();
					System.out.println("Connection from " + address.getHostAddress());
					if(currentNum > 1)
					{
						this.messageClients("Player " + currentNum + " has joined from " + address.getHostAddress() + ".");
					}
					CrosswordServer st;
					Condition conditionItem = clientLock.newCondition();
					conditionItems.add(conditionItem);
					if(CrosswordServers.size() == 0)
						st = new CrosswordServer(s, this, clientLock, conditionItem, true);
					else
						st = new CrosswordServer(s, this, clientLock, conditionItem, false);
					CrosswordServers.add(st);
					waitForClient();
					if(currentNum == 1)
					{
						System.out.println("Number of players: " + playersNeeded);
					}
					if((currentNum >= 1) && (currentNum < playersNeeded))
					{
						String line = "Waiting for player " + (currentNum+1) + ".";
						this.broadcast(line, null);
					}
					if(fileRead == false)
					{
						game = new RenderGame(this);
						fileRead = game.validParse();
					}
				}
				waitForClient();
				System.out.println("Game can now begin");
				this.messageClients("The game is beginning.");
				System.out.println("Sending game board.");
				game.data.board.printBoard();
				game.data.printQuestions();
				
			}
		} 
		catch (IOException ioe) 
		{
			System.out.println("ioe in Crossword constructor: " + ioe.getMessage());
		}
	}
	
	public void broadcast(String message, CrosswordServer st) 
	{
		if (message != null) 
		{
			System.out.println(message);
			for(CrosswordServer threads : CrosswordServers) 
			{
				if (st != threads) 
				{
					threads.sendMessage(message);
				}
			}
		}
	}
	
	public void waitForClient()
	{
		mainLock.lock();
		try 
		{
			mainCondition.await();
		} 
		catch (InterruptedException e) 
		{
			System.out.println("InterruptedException in main thread " + e.getMessage());
		}
		finally
		{
			mainLock.unlock();
		}
	}
	
	public void messageClients(String message)
	{
		for(CrosswordServer threads : CrosswordServers) 
		{
			threads.sendMessage(message);
		}	
	}
	
	public void messageClientsEdit(String message)
	{
		for(CrosswordServer threads : CrosswordServers) 
		{
			threads.sendString(message);
		}	
	}
	public void signalCall()
	{
		if(nextThread == conditionItems.size()-1)
		{
			nextThread = 0;
		}
		else
		{
			nextThread++;
		}
		clientLock.lock();
		conditionItems.get(nextThread).signal();
		clientLock.unlock();
	}
	
	public void signalAllClients()
	{
		clientLock.lock();
		for(int i=0; i<conditionItems.size(); i++) 
		{
			conditionItems.get(i).signal();
		}
		clientLock.unlock();	
	}
	
	public void signalMain()
	{
		mainLock.lock();
		mainCondition.signal();
		mainLock.unlock();
	}
	public void setPlayersNeeded(int num)
	{
		playersNeeded = num;
	}
	
	public void incrementCurrentNum()
	{
		currentNum++;
	}
	
	public int getCurrentNum()
	{
		return currentNum;
	}
	
	public int getPlayersNeeded()
	{
		return playersNeeded;
	}
	
	public static void main(String [] args) 
	{
		Crossword cr = new Crossword(3456);
	}
}
