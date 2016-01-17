package filozofowie;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher implements Runnable {

	private ReentrantLock leftChopStick;
	private ReentrantLock rightChopStick;
	private int Id;

	public AtomicBoolean isTummyFull = new AtomicBoolean(false);

	// To randomize eat/Think time
	private Random randomGenerator = new Random();

	private int noOfTurnsToEat = 0;

	public int getId() {
		return this.Id;
	}

	public int getNoOfTurnsToEat() {
		return noOfTurnsToEat;
	}

	/****
	 * 
	 * @param id
	 *            Philosopher number
	 * 
	 * @param leftChopStick
	 * @param rightChopStick
	 */
	public Philosopher(int id, ReentrantLock leftChopStick, ReentrantLock rightChopStick) {
		this.Id = id;
		this.leftChopStick = leftChopStick;
		this.rightChopStick = rightChopStick;
	}

	@Override
	public void run() {

		while (!isTummyFull.get()) {
			try {
				think();
				if (pickupLeftChopStick() && pickupRightChopStick()) {
					eat();
				}
				putDownChopSticks();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void think() throws InterruptedException {
		System.out.println(String.format("Philosopher %s is thinking", this.Id));
		System.out.flush();
		Thread.sleep(randomGenerator.nextInt(1000));
	}

	private void eat() throws InterruptedException {
		System.out.println(String.format("Philosopher %s is eating", this.Id));
		System.out.flush();
		noOfTurnsToEat++;
		Thread.sleep(randomGenerator.nextInt(1000));
	}

	private boolean pickupLeftChopStick() throws InterruptedException {
		if (leftChopStick.tryLock(10, TimeUnit.MILLISECONDS)) {
			System.out.println(String.format("Philosopher %s pickedup Left ChopStick", this.Id));
			System.out.flush();
			return true;
		}
		return false;
	}

	private boolean pickupRightChopStick() throws InterruptedException {
		if (rightChopStick.tryLock(10, TimeUnit.MILLISECONDS)) {
			System.out.println(String.format("Philosopher %s pickedup Right ChopStick", this.Id));
			System.out.flush();
			return true;
		}
		return false;
	}

	private void putDownChopSticks() {
		if (leftChopStick.isHeldByCurrentThread()) {
			leftChopStick.unlock();
			System.out.println(String.format("Philosopher %s putdown Left ChopStick", this.Id));
			System.out.flush();
		}
		if (rightChopStick.isHeldByCurrentThread()) {
			rightChopStick.unlock();
			System.out.println(String.format("Philosopher %s putdown Right ChopStick", this.Id));
			System.out.flush();
		}
	}
}
