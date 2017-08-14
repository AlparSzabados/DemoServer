package com.alpar.szabados.hibernate.server.entities;

import javax.persistence.*;

@Entity
@Table(name = "activity")
public class Activity {
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

    public Activity(Long userId, String activityName, String activityDate, TaskStatus taskStatus) {
        this.userId = userId;
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.taskStatus = taskStatus;
    }

    public Activity() {
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
}
