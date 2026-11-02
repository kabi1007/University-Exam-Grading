/** Marks scored by one student in one assessment. */
public class MarkEntry {
    private String studentId;
    private String assessmentId;
    private double marks;
    private int attempts = 1;     // 1 = first attempt, 2 = after a resit / improvement

    public MarkEntry(String studentId, String assessmentId, double marks) {
        this.studentId = studentId;
        this.assessmentId = assessmentId;
        this.marks = marks;
    }

    public String getStudentId() { return studentId; }
    public String getAssessmentId() { return assessmentId; }
    public double getMarks() { return marks; }
    public int getAttempts() { return attempts; }

    public void setMarks(double marks) { this.marks = marks; }

    public void addAttempt() { attempts++; }
}
