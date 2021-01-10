package tech.bedev.discordsrvutils.managers;

public class TimerManager
{

	public Stopwatch getStopwatch()
	{
		return new StopWatchImpl();
	}

	public long getCurrentTime()
	{
		return System.currentTimeMillis();
	}
}
