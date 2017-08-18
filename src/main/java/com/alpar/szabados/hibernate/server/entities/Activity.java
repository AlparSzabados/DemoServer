package com.alpar.szabados.hibernate.server.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "activity")
public class Activity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id", nullable = false, unique = true)
    private long activityId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "activity_date")
    private String activityDate;

    @Column(name = "task_status")
    private TaskStatus taskStatus;

    public Activity() {
    }

    public Activity(String activityName) {
        this.activityName = activityName;
    }

    public Activity(Long userId, String activityName, String activityDate, TaskStatus taskStatus) {
        this.userId = userId;
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.taskStatus = taskStatus;
    }

    public Long getId() {
        return activityId;
    }

    public void setId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return String.format("Activity{activityId=%d, userId=%d, activityName='%s', activityDate=%s, taskStatus=%s}", activityId, userId, activityName, activityDate, taskStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return userId == activity.userId &&
                Objects.equals(activityName, activity.activityName) &&
                Objects.equals(activityDate, activity.activityDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, activityName);
    }
}
