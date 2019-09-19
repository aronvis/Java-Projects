import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatRoom 
{
	private Vector<ServerThread> serverThreads;
	private Vector<Lock> lockItems;
	private Vector<Condition> conditionItems;
	private int nextThread = 0;
	public ChatRoom(int port) 
	{
		try 
		{
			System.out.println("Binding to port " + port);
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Bound to port " + port);
			serverThreads = new Vector<ServerThread>();
			lockItems = new Vector<Lock>();
			conditionItems = new Vector <Condition>();
			while(true)
			{
				Socket s = ss.accept(); // blocking
				System.out.println("Connection from: " + s.getInetAddress());
				ServerThread st;
				Lock lockItem = new ReentrantLock();
				lockItems.add(lockItem);
				Condition conditionItem = lockItem.newCondition();
				conditionItems.add(conditionItem);
				if(serverThreads.size() == 0)
					st = new ServerThread(s, this, lockItem, conditionItem, true);
				else
					st = new ServerThread(s, this, lockItem, conditionItem, false);
				serverThreads.add(st);
			}
		} 
		catch (IOException ioe) 
		{
			System.out.println("ioe in ChatRoom constructor: " + ioe.getMessage());
		}
	}
	
	public void broadcast(String message, ServerThread st) 
	{
		if (message != null) 
		{
			System.out.println(message);
			for(ServerThread threads : serverThreads) 
			{
				if (st != threads) 
				{
					threads.sendMessage(message);
				}
			}
		}
	}
	
	public void signalCall()
	{
		if(nextThread == conditionItems.size()-1)
			nextThread = 0;
		else
			nextThread++;
		lockItems.get(nextThread).lock();
		conditionItems.get(nextThread).signal();
		lockItems.get(nextThread).unlock();
	}
	
	public static void main(String [] args) 
	{
		ChatRoom cr = new ChatRoom(6789);
	}
}
