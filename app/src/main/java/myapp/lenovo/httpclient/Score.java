package myapp.lenovo.httpclient;

/**
 * Created by wn on 2017/7/16.
 */

public class Score {
    private String courseName;
    private String courseType;
    private String dailyScore;
    private String finalScore;
    private String score;

    public String getCourseName() {
        return courseName;
    }

    public String getCourseType() {
        return courseType;
    }

    public String getDailyScore() {
        return dailyScore;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public String getScore() {
        return score;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setDailyScore(String dailyScore) {
        this.dailyScore = dailyScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return courseName+"---"+courseType+"---"+dailyScore+"---"+finalScore+"---"+score;
    }
}
