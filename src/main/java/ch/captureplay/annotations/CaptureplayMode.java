package ch.captureplay.annotations;

/**
 * <p>The different kind of modes for captureplay.</p>
 * 
 * <p>
 * Depending on the mode, the interceptor can capture data or 
 * returning the captured data without need of calling the method again.
 * </p>
 * 
 * 
 * <p>The following modes can be used: 
 * <ul>
 * <li><code>OFF</code>: No capturing and no replay is done. Live-mode.</li>
 * <li><code>CAPTURE</code>: The data is captured and stored, but always 
 * the real method is invocated and its return value is returned.</li>
 * <li><code>REPLAY</code>: The method is never really invoked, because the 
 * captured values are returned. If a return value is asked, that 
 * was never captured yet, an exception is thrown.</li>
 * <li><code>SMOOTH</code>: The method is not really invoked, because the 
 * captured values are returned. If a return value is asked, that 
 * was never captured yet, the real method is called. In this mode, 
 * no data is captured! (Otherwise, it would work like a cache.)</li>
 * </ul>
 * </p>
 * 
 * @author Jonathan Weiss
 *
 */
public enum CaptureplayMode {

	/**
	 * No capturing and no replay is done. Live-mode.
	 */
	OFF, 
	/**
	 * The data is captured and stored, but always the 
	 * real method is invocated and its return value is returned.
	 */
	CAPTURE, 
	/**
	 * The method is never really invoked, because the captured 
	 * values are returned. If a return value is asked, that was 
	 * never captured yet, an exception is thrown.
	 */
	REPLAY, 

	/**
	 * The method is not really invoked, because the captured values 
	 * are returned. If a return value is asked, that was never 
	 * captured yet, the real method is called. In this mode, no data 
	 * is captured! (Otherwise, it would work like a cache.)
	 */
	SMOOTH;

}
