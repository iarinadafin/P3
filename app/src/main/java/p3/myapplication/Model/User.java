package p3.myapplication.Model;

/**
 * Created by Iarina Dafin.
 */

@SuppressWarnings("unused") // used for mandatory empty constructor and getters/setters
public class User {

	private String firstName;
	private String lastName;
	private String course;
	private String year;
	private String email;
	private String picURL;
	private String score;
	private String numberOfRatings;
	private String totalScore;

	public User () {}

	/**
	 * The constructor for the user class.
	 * @param firstName the first name of the user
	 * @param lastName the last mane of the user
	 * @param course the course that the user is enrolled in
	 * @param year the year of study that the user is in
	 * @param email the university email of the user
	 */
	public User (String firstName, String lastName, String course, String year, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.course = course;
		this.year = year;
		this.email = email;
		this.score = "0";
		this.totalScore = "0";
		this.numberOfRatings = "0";
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

	public String getEmail() {
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

	public String getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(String totalScore) {
		this.totalScore = totalScore;
	}

	public String getNumberOfRatings() {
		return numberOfRatings;
	}

	public void setNumberOfRatings(String numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}
}