public class ExamSchedule {
    private String assessmentId;
    private String date;
    private String time;
    private String venue;

    public ExamSchedule(String assessmentId, String date, String time, String venue) {
        this.assessmentId = assessmentId;
        this.date = date;
        this.time = time;
        this.venue = venue;
    }

    public String getAssessmentId() { return assessmentId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getVenue() { return venue; }

    @Override
    public String toString() {
        return date + " " + time + " @ " + venue;
    }
}
