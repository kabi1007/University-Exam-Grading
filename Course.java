import java.util.ArrayList;

public class Course {
    private String code;
    private String name;
    private int credits;
    private String term;
    private ArrayList<Assessment> assessments = new ArrayList<>();

    public Course(String code, String name, int credits, String term) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.term = term;
    }

    
    public Course(String code, String name, int credits) {
        this(code, name, credits, "TBD");
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public String getTerm() { return term; }
    public ArrayList<Assessment> getAssessments() { return assessments; }

    public double getTotalWeightage() {
        double sum = 0;
        for (Assessment a : assessments) {
            sum += a.getWeightage();
        }
        return sum;
    }


    public void addAssessment(Assessment a) throws InvalidDataException {
        if (getTotalWeightage() + a.getWeightage() > 100) {
            throw new InvalidDataException("Total weightage would exceed 100% (already used: "
                    + getTotalWeightage() + "%)");
        }
        assessments.add(a);
    }

    @Override
    public String toString() {
        return code + " - " + name + " (" + credits + " credits, " + term + ")";
    }
}
