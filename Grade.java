/** The final grade of one student in one course. */
public class Grade {
    private String studentId;
    private String courseCode;
    private double total;      // out of 100
    private String letter;
    private double points;

    public Grade(String studentId, String courseCode, double total, GradingPolicy policy) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.total = total;
        this.letter = policy.getLetter(total);       // grade comes from the policy
        this.points = policy.getPoints(letter);
    }

    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public double getTotal() { return total; }
    public String getLetter() { return letter; }
    public double getPoints() { return points; }
}
