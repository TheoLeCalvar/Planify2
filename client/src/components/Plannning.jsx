// React imports
import React, { useMemo } from "react";

// ScheduleX imports
import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react";
import { createEventModalPlugin } from "@schedule-x/event-modal";
import { createEventsServicePlugin } from "@schedule-x/events-service";
import { createCalendarControlsPlugin } from "@schedule-x/calendar-controls";
import "@schedule-x/theme-default/dist/index.css";

// Local imports
import { constants } from "@/config/constants";
import {
  JSONToPlanningEvent,
  assignUniqueUDId,
} from "@/features/calendar/utils/calendarEvent";
import PropTypes from "prop-types";

const Planning = ({ initialEvents }) => {
  const eventModal = createEventModalPlugin();
  const eventsService = useMemo(() => createEventsServicePlugin(), []);
  const calendarControls = useMemo(() => createCalendarControlsPlugin(), []);

  const calendar = useCalendarApp(
    {
      ...constants.SCHEDULE_GENERAL_CONFIG,
      selectedDate: initialEvents[0].start.split(" ")[0],
      events: assignUniqueUDId(initialEvents).map(JSONToPlanningEvent),
    },
    [eventModal, eventsService, calendarControls],
  );

  if (eventsService?.set) {
    eventsService.set(assignUniqueUDId(initialEvents).map(JSONToPlanningEvent));
    initialEvents[0] &&
      calendarControls.setDate(initialEvents[0].start.split(" ")[0]);
  }

  return (
    <div>
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  );
};

Planning.propTypes = {
  initialEvents: PropTypes.array.isRequired,
};

export default Planning;
