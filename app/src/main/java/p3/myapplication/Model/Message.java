package p3.myapplication.Model;

/**
 * Created by Iarina Dafin.
 */

public class Message {

	private String timestamp;
	private String userID;
	private String content;

	/**
	 * The constructor for the message object.
	 * @param timestamp the timestamp when the message was sent; format: yyyy-MM-dd HH:mm:ss
	 * @param userID the userID of the user that sent the message; alternatively, the string "system", that indicates a system message
	 * @param content the content string of the message
	 */
	public Message (String timestamp, String userID, String content) {
		this.timestamp = timestamp;
		this.userID = userID;
		this.content = content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
