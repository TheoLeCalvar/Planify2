// React imports
import React, { useMemo } from "react";

// ScheduleX imports
import { useCalendarApp, ScheduleXCalendar } from "@schedule-x/react"; // ScheduleX components for calendar rendering
import { createEventModalPlugin } from "@schedule-x/event-modal"; // Plugin for event modal functionality
import { createEventsServicePlugin } from "@schedule-x/events-service"; // Plugin for managing events
import { createCalendarControlsPlugin } from "@schedule-x/calendar-controls"; // Plugin for calendar controls
import "@schedule-x/theme-default/dist/index.css"; // Default theme for ScheduleX

// Local imports
import { constants } from "@/config/constants"; // Application constants
import {
  JSONToPlanningEvent,
  assignUniqueUDId,
} from "@/features/calendar/utils/calendarEvent"; // Utility functions for event processing
import PropTypes from "prop-types"; // PropTypes for type checking

/**
 * Planning component.
 * This component renders a calendar using the ScheduleX library.
 * It initializes the calendar with plugins and events, and provides a UI for managing schedules.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.initialEvents - The initial list of events to display in the calendar.
 *
 * @returns {JSX.Element} - The rendered Planning component.
 */
const Planning = ({ initialEvents }) => {
  // Initialize the event modal plugin
  const eventModal = createEventModalPlugin();

  // Memoize the events service plugin to avoid unnecessary re-creation
  const eventsService = useMemo(() => createEventsServicePlugin(), []);

  // Memoize the calendar controls plugin to avoid unnecessary re-creation
  const calendarControls = useMemo(() => createCalendarControlsPlugin(), []);

  // Initialize the calendar app with configuration, plugins, and events
  const calendar = useCalendarApp(
    {
      ...constants.SCHEDULE_GENERAL_CONFIG, // General configuration for the calendar
      selectedDate: initialEvents[0]?.start.split(" ")[0], // Set the initial selected date based on the first event
      events: assignUniqueUDId(initialEvents).map(JSONToPlanningEvent), // Process and assign unique IDs to events
    },
    [eventModal, eventsService, calendarControls], // Plugins to include in the calendar
  );

  // Update the events and selected date in the calendar controls if the events service is available
  if (eventsService?.set) {
    eventsService.set(assignUniqueUDId(initialEvents).map(JSONToPlanningEvent)); // Set the events in the events service
    if (initialEvents[0]) {
      calendarControls.setDate(initialEvents[0].start.split(" ")[0]); // Set the selected date in the calendar controls
    }
  }

  return (
    <div>
      {/* Render the ScheduleX calendar */}
      <ScheduleXCalendar calendarApp={calendar} />
    </div>
  );
};

// Define the prop types for the component
Planning.propTypes = {
  initialEvents: PropTypes.array.isRequired, // Array of initial events to display in the calendar
};

// Export the component as the default export
export default Planning;
