// React imports
import React, { useContext } from "react";

// ScheduleX imports
import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react"; // ScheduleX library for calendar functionality
import "@schedule-x/theme-default/dist/index.css"; // Default theme for ScheduleX

// Local imports
import { CalendarContext } from "@/hooks/CalendarContext"; // Context for managing calendar state
import { constants } from "@/config/constants"; // Application constants
import locale from "@/config/locale.json"; // Localization strings
import { convertCalendarToGeneric } from "../utils/calendarEvent"; // Utility function to convert events to a generic format
import "../assets/calendar.css"; // Custom CSS for the calendar
import "../assets/genericWeekCalendar.css"; // Custom CSS for the generic week calendar

/**
 * GenericWeekCalendar Component
 * This component renders a generic week calendar using the ScheduleX library.
 * It integrates with the CalendarContext to manage events and their statuses.
 *
 * @returns {JSX.Element} - The rendered GenericWeekCalendar component.
 */
export default function GenericWeekCalendar() {
  // Access the calendar context to retrieve event services and initial events
  const {
    onGenericChange, // Callback for handling changes to generic events
    genericEventService: eventService, // Service for managing generic events
    initialEvents, // Initial events to display in the calendar
  } = useContext(CalendarContext);

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

    // Create an updated event object with the new status
    const updatedEvent = {
      ...event,
      status: newStatus,
      people: [locale.slotStatus[newStatus]], // Update the "people" field with localized status
      _options: {
        additionalClasses: [constants.CALENDAR.SLOT_COLOR[newStatus]], // Add CSS class for the new status
      },
    };

    /**
     * Cancel function to revert the event to its original state.
     */
    const cancel = () => {
      eventService.update(event); // Revert the event to its original state
    };

    // Update the event with the new status
    eventService.update(updatedEvent);

    // Trigger the generic change callback with the updated event
    onGenericChange(updatedEvent, event.status, cancel);
  };

  // Initialize the calendar app with configuration, events, and callbacks
  const calendar = useCalendarApp(
    {
      ...constants.SCHEDULE_GENERAL_CONFIG, // General configuration for the calendar
      selectedDate: "2000-01-03", // Set the initial selected date
      events: convertCalendarToGeneric(initialEvents), // Convert initial events to a generic format
      callbacks: {
        onEventClick, // Register the event click handler
      },
    },
    [eventService], // Dependencies for the calendar app
  );

  return (
    <div className="generic-week-calendar">
      {/* Render the ScheduleX calendar */}
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  );
}
