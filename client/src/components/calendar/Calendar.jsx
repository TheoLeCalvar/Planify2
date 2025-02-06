import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react";
import React from "react";
import { useContext } from "react";
import { CalendarContext } from "../../context/CalendarContext";

import { constants } from "../../contants";
import locale from "../../config/locale.json";

import "@schedule-x/theme-default/dist/index.css";
import "./calendar.css";

export default function Calendar() {
  const { eventService, initialEvents } = useContext(CalendarContext);

  const onEventClick = (event) => {
    const currentStatus = Object.values(constants.CALENDAR.SLOT_STATUS).indexOf(
      event.status,
    );
    const newStatusId =
      (currentStatus + 1) %
      Object.values(constants.CALENDAR.SLOT_STATUS).length;
    const newStatus = Object.values(constants.CALENDAR.SLOT_STATUS)[
      newStatusId
    ];
    eventService.update({
      ...event,
      status: newStatus,
      people: [locale.slotStatus[newStatus]],
      _options: {
        additionalClasses: [constants.CALENDAR.SLOT_COLOR[newStatus]],
      },
    });
  };

  const calendar = useCalendarApp(
    {
      ...constants.SCHEDULE_GENERAL_CONFIG,
      selectedDate: initialEvents[0].start.split(" ")[0],
      events: initialEvents,
      callbacks: {
        onEventClick,
      },
    },
    [eventService],
  );

  return (
    <div>
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  );
}
