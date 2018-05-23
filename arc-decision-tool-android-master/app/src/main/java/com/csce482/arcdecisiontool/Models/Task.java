package com.csce482.arcdecisiontool.Models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by michaelmckenna on 2/27/18.
 */


public class Task extends RealmObject {
    @PrimaryKey @Required
    private String id = "";
    @Required
    private String name = "";
    @Required
    private Date completionDate = new Date();
    @Required
    private String timeline = "";
    private boolean isCompleted = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getTimeline() {return timeline;}

    public void setTimeline(String timeline) {this.timeline = timeline;}

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}