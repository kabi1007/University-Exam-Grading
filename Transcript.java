import java.util.ArrayList;
import java.util.Collections;

/** Rule 4: course list, grades, term GPA and CGPA. */
public class Transcript {
    private Student student;
    private ArrayList<Grade> grades;       // grades of this student only
    private ArrayList<Course> courses;     // all courses (to look up name, credits, term)

    public Transcript(Student student, ArrayList<Grade> grades, ArrayList<Course> courses) {
        this.student = student;
        this.grades = grades;
        this.courses = courses;
    }

    private Course findCourse(String code) {
        for (Course c : courses) {
            if (c.getCode().equals(code)) return c;
        }
        return null;
    }

    public void print() {
        System.out.println("\n================ TRANSCRIPT ================");
        System.out.println(student);
        if (grades.isEmpty()) {
            System.out.println("No published grades yet.");
            return;
        }

        // collect the different terms and sort them
        ArrayList<String> terms = new ArrayList<>();
        for (Grade g : grades) {
            String term = findCourse(g.getCourseCode()).getTerm();
            if (!terms.contains(term)) terms.add(term);
        }
        Collections.sort(terms);

        double cumPoints = 0;
        int cumCredits = 0;
        for (String term : terms) {
            System.out.println("\nTerm: " + term);
            System.out.printf("%-8s %-22s %-7s %-7s %-6s %-6s%n",
                    "Code", "Course", "Credits", "Marks", "Grade", "Points");
            double termPoints = 0;
            int termCredits = 0;
            for (Grade g : grades) {
                Course c = findCourse(g.getCourseCode());
                if (!c.getTerm().equals(term)) continue;
                System.out.printf("%-8s %-22s %-7d %-7.2f %-6s %-6.1f%n",
                        c.getCode(), c.getName(), c.getCredits(), g.getTotal(), g.getLetter(), g.getPoints());
                termPoints += g.getPoints() * c.getCredits();
                termCredits += c.getCredits();
            }
            cumPoints += termPoints;
            cumCredits += termCredits;
            System.out.printf("Term Summary -> Credits: %d | GPA: %.2f | CGPA so far: %.2f%n",
                    termCredits, termPoints / termCredits, cumPoints / cumCredits);
        }
        System.out.printf("%nFINAL CGPA: %.2f (total credits: %d)%n", cumPoints / cumCredits, cumCredits);
        System.out.println("============================================");
    }
}
