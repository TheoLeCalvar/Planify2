// React imports
import React from "react";
import { useContext } from "react";

// ScheduleX imports
import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react"; // ScheduleX library for calendar functionality
import "@schedule-x/theme-default/dist/index.css"; // Default theme for ScheduleX

// Local imports
import { CalendarContext } from "@/hooks/CalendarContext"; // Context for managing calendar state
import { constants } from "@/config/constants"; // Application constants
import locale from "@/config/locale.json"; // Localization strings
import "../assets/calendar.css"; // Custom CSS for the calendar

/**
 * Calendar Component
 * This component renders a calendar using the ScheduleX library.
 * It integrates with the CalendarContext to manage events and their statuses.
 *
 * @returns {JSX.Element} - The rendered Calendar component.
 */
export default function Calendar() {
  // Access the calendar context to retrieve the event service and initial events
  const { eventService, initialEvents } = useContext(CalendarContext);

  /**
   * Handles the click event on a calendar event.
   * Cycles through the statuses of the event and updates its status and appearance.
   *
   * @param {Object} event - The clicked event object.
   */
  const onEventClick = (event) => {
    // Get the current status of the event
    const currentStatus = Object.values(constants.CALENDAR.SLOT_STATUS).indexOf(
      event.status,
    );

    // Calculate the next status in the cycle
    const newStatusId =
      (currentStatus + 1) %
      Object.values(constants.CALENDAR.SLOT_STATUS).length;
    const newStatus = Object.values(constants.CALENDAR.SLOT_STATUS)[
      newStatusId
    ];

    // Update the event with the new status and additional properties
    eventService.update({
      ...event,
      status: newStatus,
      people: [locale.slotStatus[newStatus]], // Update the "people" field with localized status
      _options: {
        additionalClasses: [constants.CALENDAR.SLOT_COLOR[newStatus]], // Add CSS class for the new status
      },
    });
  };

  // Initialize the calendar app with configuration, events, and callbacks
  const calendar = useCalendarApp(
    {
      ...constants.SCHEDULE_GENERAL_CONFIG, // General configuration for the calendar
      selectedDate: initialEvents[0].start.split(" ")[0], // Set the initial selected date based on the first event
      events: initialEvents, // Load the initial events into the calendar
      callbacks: {
        onEventClick, // Register the event click handler
      },
    },
    [eventService], // Dependencies for the calendar app
  );

  return (
    <div>
      {/* Render the ScheduleX calendar */}
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  );
}
