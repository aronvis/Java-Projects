package vischjag_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CrosswordServer extends Thread 
{
	private PrintWriter pw;
	private BufferedReader br;
	private Crossword cr;
	private Lock lockItem;
	private Condition conditionItem;
	private Boolean firstThread;
	public CrosswordServer(Socket s, Crossword cr, Lock lockItem, Condition conditionItem, Boolean firstThread) 
	{
		try 
		{
			this.cr = cr;
			this.lockItem = lockItem;
			this.conditionItem = conditionItem;
			this.firstThread = firstThread;
			pw = new PrintWriter(s.getOutputStream());
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.start();
		} 
		catch (IOException ioe) 
		{
			System.out.println("ioe in CrosswordServer constructor: " + ioe.getMessage());
		}
	}
	public void sendMessage(String message) 
	{
		pw.println(message);
		pw.flush();
	}
	public void sendString(String message)
	{
		pw.print(message);
		pw.flush();
	}
	public void run() 
	{
		try 
		{
			
			int numPlayers = 4;
			if(firstThread == true)
			{
				while((numPlayers > 3) || (numPlayers < 1))
				{
					pw.println("How many players will there be? ");
					pw.flush();	
					String value = br.readLine();
					numPlayers = Integer.parseInt(value);
					if((numPlayers > 3) || (numPlayers < 1))
					{
						pw.println("Please enter a value between 1 and 3.");
						pw.flush();
					}
				}
				cr.setPlayersNeeded(numPlayers);
			}
			else
			{
				pw.println("There is a game waiting for you.");
				pw.flush();
				for(int i = 1; i<cr.getCurrentNum(); i++)
				{
					pw.println("Player " + i + " has already joined.");
					pw.flush();
				}
			}
			lockItem.lock();
			cr.signalMain();
			if(cr.getCurrentNum() < cr.getPlayersNeeded())
			{
				conditionItem.await();
			}
		} 
		catch (IOException ioe) 
		{
			System.out.println("ioe in CrosswordServer: " + ioe.getMessage());
		}
		catch (InterruptedException ie)
		{
			System.out.println("InterruptedException in CrosswordServer" + ie.getMessage());
		}
		finally
		{
			lockItem.unlock();
			cr.signalAllClients();
		}
		while(true)
		{
			try 
			{
				lockItem.lock();
				if(firstThread == false)
				{
					conditionItem.await();
				}
				else
				{
					cr.signalMain();
					firstThread = false;
					lockItem.unlock();
				}
				String[] items = {"content"};
				while(!items[0].trim().equals("END_OF_MESSAGE")) 
				{
					String line = br.readLine();
					items = line.split(":");
					if(!items[0].trim().equals("END_OF_MESSAGE"))
					{
						cr.broadcast(line, this);
					}
				}
			} 
			catch (IOException ioe) 
			{
				System.out.println("ioe in ServerThread.run(): " + ioe.getMessage());
			} 
			catch (InterruptedException e) 
			{
				System.out.println("Interrupted exception in ServerThread.run(): " + e.getMessage());
			}
			finally
			{
				cr.signalCall();
				lockItem.unlock();
			}
		}
	}
}