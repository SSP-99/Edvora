package com.edvora.dto;

public class ExamCountdownDto {
    private Long examId;
    private String examName;
    private String subjectName;
    private long daysLeft;
    private long hoursLeft;
    private boolean expired;

    public ExamCountdownDto() {}

    public ExamCountdownDto(Long examId, String examName, String subjectName, long daysLeft, long hoursLeft, boolean expired) {
        this.examId = examId;
        this.examName = examName;
        this.subjectName = subjectName;
        this.daysLeft = daysLeft;
        this.hoursLeft = hoursLeft;
        this.expired = expired;
    }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public long getDaysLeft() { return daysLeft; }
    public void setDaysLeft(long daysLeft) { this.daysLeft = daysLeft; }
    public long getHoursLeft() { return hoursLeft; }
    public void setHoursLeft(long hoursLeft) { this.hoursLeft = hoursLeft; }
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
}
