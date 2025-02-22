// React imports
import React, { useContext } from "react";

// ScheduleX imports
import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react";
import "@schedule-x/theme-default/dist/index.css";

// Local imports
import { CalendarContext } from "@/hooks/CalendarContext";
import { constants } from "@/config/constants";
import locale from "@/config/locale.json";
import { convertCalendarToGeneric } from "../utils/calendarEvent";
import "../assets/calendar.css";
import "../assets/genericWeekCalendar.css";

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
