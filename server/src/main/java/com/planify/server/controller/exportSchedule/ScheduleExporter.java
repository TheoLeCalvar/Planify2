package com.planify.server.controller.exportSchedule;

import com.planify.server.models.Planning;

public abstract class ScheduleExporter {

    public abstract String export(Planning planning);


}
