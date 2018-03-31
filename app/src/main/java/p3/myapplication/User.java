package p3.myapplication;

/**
 * Created by Iarina Dafin.
 */

@SuppressWarnings("unused") // used for mandatory empty constructor and getters/setters
class User {

	private String firstName;
	private String lastName;
	private String course; // todo: may change data structure for course name
	private int year;
	private String email;
	private String picURL;

	public User () {}

	// todo: update for all fields
	User (String firstName, String lastName, String course, int year, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.course = course;
		this.year = year;
		this.email = email;
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

	String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}