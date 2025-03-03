package com.planify.server.controller.returnsClass;

import java.util.List;

import com.planify.server.models.Planning.Status;
import com.planify.server.models.ScheduledLesson;

public class GeneratedPlanning {
	private List<ScheduledLesson> scheduledLessons;
	
	private Status status;
	
	private boolean isSolutionOptimal;
	
	private String message;
	
	public GeneratedPlanning() {
	}

	public GeneratedPlanning(List<ScheduledLesson> scheduledLessons, Status status, boolean isSolutionoptimal, String message) {
		this.scheduledLessons = scheduledLessons;
		this.status = status;
		this.isSolutionOptimal = isSolutionoptimal;
		this.message = message;
	}

	public List<ScheduledLesson> getScheduledLessons() {
		return scheduledLessons;
	}

	public void setScheduledLessons(List<ScheduledLesson> scheduledLessons) {
		this.scheduledLessons = scheduledLessons;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isSolutionOptimal() {
		return isSolutionOptimal;
	}

	public void setSolutionOptimal(boolean isSolutionOptimal) {
		this.isSolutionOptimal = isSolutionOptimal;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
