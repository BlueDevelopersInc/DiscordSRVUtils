package tech.bedev.discordsrvutils.managers;

public class StopWatchImpl implements Stopwatch
{

	private long startTime = 0;
	private boolean running = false;


	public void start()
	{
		this.startTime = System.currentTimeMillis();
		this.running = true;
	}
	public long getElapsedTime()
	{
		long elapsed;
		if(running)
		{
			elapsed = (System.currentTimeMillis() - startTime);
		}
		else
		{
			long stopTime = 0;
			elapsed = (stopTime - startTime);
		}
		return elapsed;
	}
}
