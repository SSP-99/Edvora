package com.edvora.dto;

public class SubjectProgressDto {
    private Long subjectId;
    private String subjectName;
    private int totalTopics;
    private int completedTopics;
    private int percent;

    public SubjectProgressDto() {}

    public SubjectProgressDto(Long subjectId, String subjectName, int totalTopics, int completedTopics, int percent) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.totalTopics = totalTopics;
        this.completedTopics = completedTopics;
        this.percent = percent;
    }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public int getTotalTopics() { return totalTopics; }
    public void setTotalTopics(int totalTopics) { this.totalTopics = totalTopics; }
    public int getCompletedTopics() { return completedTopics; }
    public void setCompletedTopics(int completedTopics) { this.completedTopics = completedTopics; }
    public int getPercent() { return percent; }
    public void setPercent(int percent) { this.percent = percent; }
}
