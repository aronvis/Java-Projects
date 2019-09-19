import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ServerThread extends Thread 
{
	private PrintWriter pw;
	private BufferedReader br;
	private ChatRoom cr;
	private Lock lockItem;
	private Condition conditionItem;
	private Boolean firstThread;
	public ServerThread(Socket s, ChatRoom cr, Lock lockItem, Condition conditionItem, Boolean firstThread) 
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
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}

	public void sendMessage(String message) 
	{
		pw.println(message);
		pw.flush();
	}
	
	public void run() 
	{
		while(true)
		{
			try 
			{
				lockItem.lock();
				if(firstThread == false)
				{
					System.out.println("waiting..");
					conditionItem.await();
				}
				else
					firstThread = false;
				String[] items = {"name: ","content"};
				while(!items[1].trim().equals("END_OF_MESSAGE")) 
				{
					String line = br.readLine();
					items = line.split(":");
					if(!items[1].trim().equals("END_OF_MESSAGE"))
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
