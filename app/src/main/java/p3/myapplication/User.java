package p3.myapplication;

/**
 * Created by Iarina Dafin.
 */

@SuppressWarnings("unused") // used for mandatory empty constructor and getters/setters
class User {

	private String firstName;
	private String lastName;
	private String course; // todo: may change data structure for course name
	private String year;
	private String email;
	private String picURL;
	private String score;

	public User () {}

	// todo: update for all fields
	User (String firstName, String lastName, String course, String year, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.course = course;
		this.year = year;
		this.email = email;
		this.score = "0";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
}