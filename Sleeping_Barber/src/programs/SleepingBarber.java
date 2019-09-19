package programs;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SleepingBarber extends Thread {
	private static int maxSeats;
	private static int totalCustomers;
	boolean sleeping; 
	private static Vector<Customer> customersWaiting;
	private Lock barberLock;
	private Condition sleepingCondition;
	private static boolean moreCustomers;
	private String barberName;
	public SleepingBarber(String name) 
	{
		maxSeats = 3;
		totalCustomers = 10;
		moreCustomers = true;
		customersWaiting = new Vector<Customer>();
		barberLock = new ReentrantLock();
		sleepingCondition = barberLock.newCondition();
		barberName = name;
		sleeping = true;
		this.start();
	}
	public static synchronized boolean addCustomerToWaiting(Customer customer) 
	{
		if (customersWaiting.size() == maxSeats) {
			return false;
		}
		Util.printMessage("Customer " + customer.getCustomerName() + " is waiting");
		customersWaiting.add(customer);
		String customersString = "";
		for (int i=0; i < customersWaiting.size(); i++) {
			customersString += customersWaiting.get(i).getCustomerName();
			if (i < customersWaiting.size() - 1) {
				customersString += ",";
			}
		}
		Util.printMessage("Customers currently waiting: " + customersString);
		return true;
	}
	public void wakeUpBarber() {
		try {
			barberLock.lock();
			sleepingCondition.signal();
		} finally {
			barberLock.unlock();
		}
	}
	public void run() {
		while(moreCustomers) 
		{
			while(!customersWaiting.isEmpty()) {
				Customer customer = null;
				synchronized(this) {
					customer = customersWaiting.remove(0);
				}
				customer.startingHaircut();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					System.out.println(barberName + " ie cutting customer's hair" + ie.getMessage());
				}
				customer.finishingHaircut();
				Util.printMessage(barberName + " checking for more customers...");		
			}
			try {
				barberLock.lock();
				Util.printMessage(barberName + " no customers, so time to sleep...");
				sleeping = true;
				sleepingCondition.await();
				Util.printMessage(barberName + " someone woke me up!");
				sleeping = false;
			} catch (InterruptedException ie) {
				System.out.println(barberName + " ie while sleeping: " + ie.getMessage());
			} finally {
				barberLock.unlock();
			}
		}
		Util.printMessage(barberName + " all done for today!  Time to go home!");
		
	}
	public static void main(String [] args) {
		SleepingBarber sb1 = new SleepingBarber("Aron");
		SleepingBarber sb2 = new SleepingBarber("Brenda");
		ExecutorService executors = Executors.newCachedThreadPool();
		for (int i=0; i < SleepingBarber.totalCustomers; i++) 
		{
			Customer customer = new Customer(i,sb1,sb2);
			executors.execute(customer);
			try {
				Random rand = new Random();
				int timeBetweenCustomers = rand.nextInt(2000);
				Thread.sleep(timeBetweenCustomers);
			} catch (InterruptedException ie) {
				System.out.println("ie in customers entering: " + ie.getMessage());
			}
		}
		executors.shutdown();
		while(!executors.isTerminated()) {
			Thread.yield();
		}
		Util.printMessage("No more customers coming today...");
		SleepingBarber.moreCustomers = false;
		sb1.wakeUpBarber();
		sb2.wakeUpBarber();
	}
}
