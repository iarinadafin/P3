package p3.myapplication.Model;

@SuppressWarnings("unused")
public class Meeting {

	private String name;
	private String startDate;
	private String endDate;
	private String module;

	/**
	 * Constructor for the meeting object.
	 * @param name the name of the meeting
	 * @param startDate start date and time string of the meeting; format: yyyy-MM-dd HH:mm
	 * @param endDate end date and time of the meeting; format: yyyy-MM-dd HH:mm
	 * @param module the module that this meeting has been listed for
	 */
	public Meeting (String name, String startDate, String endDate, String module) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
}