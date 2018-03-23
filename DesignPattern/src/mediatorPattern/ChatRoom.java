/**
 * 
 */
package mediatorPattern;

import java.util.Date;

/**
 * @author Taoheng
 *
 */
public class ChatRoom {
	public static void showMessage(User user, String message) {
		System.out.println(new Date().toString() + "[" + user.getName() + "]" + message);
	}
}
