package p3.myapplication;

/**
 * Created by Iarina Dafin.
 */

public class Message {

	private String timestamp;
	private String userID;
	private String content;

	Message (String timestamp, String userID, String content) {
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
