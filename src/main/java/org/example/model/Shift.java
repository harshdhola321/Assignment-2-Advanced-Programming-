package org.example.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Class representing a staff shift
 */
public class Shift implements Serializable {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public Shift(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Check if this shift covers the given date and time
     * @param dateTime The date and time to check
     * @return true if the shift covers the given date and time, false otherwise
     */
    public boolean covers(LocalDateTime dateTime) {
        // Handle overnight shifts (where endTime is before startTime)
        if (endTime.isBefore(startTime) || endTime.equals(LocalTime.MIDNIGHT)) {
            // For the day of the shift, check if time is after or equal to start time
            if (dateTime.getDayOfWeek() == dayOfWeek && !dateTime.toLocalTime().isBefore(startTime)) {
                return true;
            }
            
            // For the next day, check if time is before or equal to end time
            DayOfWeek nextDay = dayOfWeek.plus(1);
            if (dateTime.getDayOfWeek() == nextDay && 
                (dateTime.toLocalTime().isBefore(endTime) || dateTime.toLocalTime().equals(endTime))) {
                return true;
            }
            
            return false;
        } else {
            // Regular shift within the same day
            return dateTime.getDayOfWeek() == dayOfWeek &&
                   !dateTime.toLocalTime().isBefore(startTime) &&
                   !dateTime.toLocalTime().isAfter(endTime);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return dayOfWeek == shift.dayOfWeek &&
                Objects.equals(startTime, shift.startTime) &&
                Objects.equals(endTime, shift.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + " - " + endTime;
    }
}
