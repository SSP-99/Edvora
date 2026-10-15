package com.edvora.dto;

public class CoveragePredictionDto {
    private String subjectName;
    private double remainingHours;
    private double availableHours;
    private String status;
    private double shortfallHours;

    public CoveragePredictionDto() {}

    public CoveragePredictionDto(String subjectName, double remainingHours, double availableHours, String status, double shortfallHours) {
        this.subjectName = subjectName;
        this.remainingHours = remainingHours;
        this.availableHours = availableHours;
        this.status = status;
        this.shortfallHours = shortfallHours;
    }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public double getRemainingHours() { return remainingHours; }
    public void setRemainingHours(double remainingHours) { this.remainingHours = remainingHours; }
    public double getAvailableHours() { return availableHours; }
    public void setAvailableHours(double availableHours) { this.availableHours = availableHours; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getShortfallHours() { return shortfallHours; }
    public void setShortfallHours(double shortfallHours) { this.shortfallHours = shortfallHours; }
}
