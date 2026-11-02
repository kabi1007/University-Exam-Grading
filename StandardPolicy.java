/** Rule 3: A >= 90, B >= 80, C >= 70, D >= 50, otherwise F (4.0 scale). */
public class StandardPolicy implements GradingPolicy {

    @Override
    public String getLetter(double total) {
        if (total >= 90) return "A";
        if (total >= 80) return "B";
        if (total >= 70) return "C";
        if (total >= 50) return "D";
        return "F";
    }

    @Override
    public double getPoints(String letter) {
        switch (letter) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            default:  return 0.0;
        }
    }
}
