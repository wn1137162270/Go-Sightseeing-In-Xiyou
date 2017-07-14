package myapp.lenovo.httpclient;

import java.io.Serializable;

/**
 * Created by Lenovo on 2017/1/27.
 */

public class Course implements Serializable{
    private int section;
    private int day;
    private String courseName;
    private int courseLong=2;
    private String courseDetail;
    private int color;

    public int getSection() {
        return section;
    }

    public int getDay() {
        return day;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCourseLong() {
        return courseLong;
    }

    public int getColor() {
        return color;
    }

    public String getCourseDetail() {
        return courseDetail;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseLong(int courseLong) {
        this.courseLong = courseLong;
    }

    public void setCourseDetail(String courseDetail) {
        this.courseDetail = courseDetail;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
