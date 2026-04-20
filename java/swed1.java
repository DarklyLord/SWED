public class swed1 {
	public static void main(String[] args) {
		Developer developer1 = new Developer("Student", "Ffm", "Darkly", "S", new String[]{"Java", "Python"});
		String jobTitle1 = developer1.getJobtitel();
		System.out.println(jobTitle1); // Output: Student
	}
}

class Developer {
	private String Jobtitel;
	private String Sitelocation;
	private String Firstname;
	private String surename;
	private String[] codinglangue;

	public Developer(String jobtitel, String sitelocation, String firstname, String surename, String[] codinglangue) {
		setJobtitel(jobtitel);
		setSitelocation(sitelocation);
		setFirstname(firstname);
		setSurename(surename);
		setCodinglangue(codinglangue);
	}

	public String getJobtitel() {
		return Jobtitel;
	}

	public void setJobtitel(String jobtitel) {
		validateRequiredString(jobtitel, "Jobtitel");
		this.Jobtitel = jobtitel;
	}

	public String getSitelocation() {
		return Sitelocation;
	}

	public void setSitelocation(String sitelocation) {
		validateRequiredString(sitelocation, "Sitelocation");
		this.Sitelocation = sitelocation;
	}

	public String getFirstname() {
		return Firstname;
	}

	public void setFirstname(String firstname) {
		validateRequiredString(firstname, "Firstname");
		this.Firstname = firstname;
	}

	public String getSurename() {
		return surename;
	}

	public void setSurename(String surename) {
		validateRequiredString(surename, "surename");
		this.surename = surename;
	}

	public String[] getCodinglangue() {
		return codinglangue.clone();
	}

	public void setCodinglangue(String[] codinglangue) {
		validateCodinglangue(codinglangue);
		this.codinglangue = codinglangue.clone();
	}

	public String devlopFeature(String featureName) {
		validateRequiredString(featureName, "featureName");
		return Firstname + " " + surename + " developed feature: " + featureName;
	}

	public String joinprojket(String projectName) {
		validateRequiredString(projectName, "projectName");
		return Firstname + " " + surename + " joined project: " + projectName;
	}

	public String fixbug(String bugId) {
		validateRequiredString(bugId, "bugId");
		return Firstname + " " + surename + " fixed bug: " + bugId;
	}

	private void validateRequiredString(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " cannot be null or blank.");
		}
	}

	private void validateCodinglangue(String[] codinglangue) {
		if (codinglangue == null || codinglangue.length == 0) {
			throw new IllegalArgumentException("codinglangue cannot be null or empty.");
		}

		for (String language : codinglangue) {
			if (language == null || language.isBlank()) {
				throw new IllegalArgumentException("codinglangue cannot contain null or blank values.");
			}
		}
	}
}