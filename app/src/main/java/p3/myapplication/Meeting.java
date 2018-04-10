package p3.myapplication;

@SuppressWarnings("unused")
public class Meeting {

	private String name;
	private String startDate;
	private String endDate;
	private String module;

	Meeting (String name, String startDate, String endDate, String module) {
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
