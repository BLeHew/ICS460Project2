package helpers;

//we call the stopwatch helper for deciding how long to wait for a response, and so on.
public class StopwatchHelper {
	private final long start;

    /**
     * Initializes a new stopwatch.
     */
    public StopwatchHelper() {
        start = System.currentTimeMillis();
    } 
    
    /**
     * Returns the elapsed CPU time (in seconds) since the stopwatch was created.
     *
     * @return elapsed CPU time (in seconds) since the stopwatch was created
     */
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }
}
