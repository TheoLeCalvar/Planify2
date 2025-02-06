import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react";
import React, { useContext } from "react";

import { CalendarContext } from "../../context/CalendarContext";

import { constants } from "../../contants";
import locale from "../../config/locale.json";

import { convertCalendarToGeneric } from "../../helper/calendarEvent";

import "@schedule-x/theme-default/dist/index.css";
import "./calendar.css";
import "./genericWeekCalendar.css";

export default function GenericWeekCalendar() {
  const {
    onGenericChange,
    genericEventService: eventService,
    initialEvents,
  } = useContext(CalendarContext);

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
    const updatedEvent = {
      ...event,
      status: newStatus,
      people: [locale.slotStatus[newStatus]],
      _options: {
        additionalClasses: [constants.CALENDAR.SLOT_COLOR[newStatus]],
      },
    };

    const cancel = () => {
      eventService.update(event);
    };
    eventService.update(updatedEvent);
    onGenericChange(updatedEvent, event.status, cancel);
  };

  const calendar = useCalendarApp(
    {
      ...constants.SCHEDULE_GENERAL_CONFIG,
      selectedDate: "2000-01-03",
      events: convertCalendarToGeneric(initialEvents),
      callbacks: {
        onEventClick,
      },
    },
    [eventService],
  );

  return (
    <div className="generic-week-calendar">
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  );
}
