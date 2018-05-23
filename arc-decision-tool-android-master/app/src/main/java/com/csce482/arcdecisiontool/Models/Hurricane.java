package com.csce482.arcdecisiontool.Models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by michaelmckenna on 2/27/18.
 */

public class Hurricane extends RealmObject {
    @PrimaryKey @Required
    private String id = "";
    @Required
    private String name = "";
    @Required
    private String location = "";
    @Required
    private String evacOrder = "";
    @Required
    private Date landfall = new Date();
    @Required
    private Date galeForceWindArrive = new Date();
    @Required
    private Date airportsCloseTime = new Date();
    @Required
    private Date leadTimeToOpenShelters = new Date();
    @Required
    private Date hunkerDownTime = new Date();
    @Required
    private Date expectedReEntryTime = new Date();
    @Required
    private RealmList<Date> conferenceCallTimeForRegion = new RealmList<>();
    @Required
    private RealmList<Date> conferenceCallTimeForNHQ = new RealmList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != "") return;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setEvac(String evac) { this.evacOrder = evac; }

	public String getEvac() {
		return evacOrder;
	}

	public void setLocation(String location) {
		this.location = location;
	}

    public Date getLandfall() {
        return landfall;
    }

    public void setLandfall(Date landfall) {
        this.landfall = landfall;
    }

    public Date getGaleForceWindArrive() {
        return galeForceWindArrive;
    }

    public void setGaleForceWindArrive(Date galeForceWindArrive) {
        this.galeForceWindArrive = galeForceWindArrive;
    }

    public Date getAirportsCloseTime() {
        return airportsCloseTime;
    }

    public void setAirportsCloseTime(Date airportsCloseTime) {
        this.airportsCloseTime = airportsCloseTime;
    }

    public Date getLeadTimeToOpenShelters() {
        return leadTimeToOpenShelters;
    }

    public void setLeadTimeToOpenShelters(Date leadTimeToOpenShelters) {
        this.leadTimeToOpenShelters = leadTimeToOpenShelters;
    }

    public Date getHunkerDownTime() {
        return hunkerDownTime;
    }

    public void setHunkerDownTime(Date hunkerDownTime) {
        this.hunkerDownTime = hunkerDownTime;
    }

    public Date getExpectedReEntryTime() {
        return expectedReEntryTime;
    }

    public void setExpectedReEntryTime(Date expectedReEntryTime) {
        this.expectedReEntryTime = expectedReEntryTime;
    }


    public RealmList<Date> getConferenceCallTimesForRegion() {
        return conferenceCallTimeForRegion;
    }

    public void setConferenceCallTimesForRegion(Date conferenceCallTimesForRegion) {
        this.conferenceCallTimeForRegion.add(conferenceCallTimesForRegion);
    }

    public RealmList<Date> getConferenceCallTimesForNHQ() {
        return conferenceCallTimeForNHQ;
    }

    public void setConferenceCallTimesForNHQ(Date conferenceCallTimesForNHQ) {
        this.conferenceCallTimeForNHQ.add(conferenceCallTimesForNHQ);
    }
}
