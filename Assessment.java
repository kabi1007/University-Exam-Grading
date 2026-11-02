/** One graded component of a course, e.g. Midterm (max 50 marks, 30% weightage). */
public class Assessment {
    private String id;
    private String courseCode;
    private String name;
    private double maxMarks;
    private double weightage;     // percentage of the course total

    public Assessment(String id, String courseCode, String name, double maxMarks, double weightage) {
        this.id = id;
        this.courseCode = courseCode;
        this.name = name;
        this.maxMarks = maxMarks;
        this.weightage = weightage;
    }

    public String getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getName() { return name; }
    public double getMaxMarks() { return maxMarks; }
    public double getWeightage() { return weightage; }

    /** Rule 2: scale the marks to the configured weightage. */
    public double getWeightedScore(double marks) {
        return marks / maxMarks * weightage;
    }
}
