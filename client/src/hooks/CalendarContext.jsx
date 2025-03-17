// React imports
import { React, createContext, useMemo } from "react";
import { useState } from "react";
import PropTypes from "prop-types";

// Schedule-X imports
import { createEventsServicePlugin } from "@schedule-x/events-service";

// Local imports
import ConfirmationDialog from "@/components/ConfirmationDialog";
import { JSONToCalendarEvent } from "@/features/calendar/utils/calendarEvent";

// Create the CalendarContext
export const CalendarContext = createContext();

/**
 * CalendarContextProvider Component
 * This component provides a context for managing calendar events and their statuses.
 * It includes functionality for updating events, handling generic changes, and showing confirmation dialogs.
 *
 * @param {Object} props - The component props.
 * @param {React.ReactNode} props.children - The child components that will have access to the CalendarContext.
 * @param {Array} [props.initialEvents] - The initial list of events to populate the calendar.
 * @returns {JSX.Element} - The CalendarContextProvider component.
 */
export default function CalendarContextProvider({ children, initialEvents }) {
  // Create event services using useMemo for memoization
  const eventService = useMemo(() => createEventsServicePlugin(), []);
  const genericEventService = useMemo(() => createEventsServicePlugin(), []);

  // State for managing the confirmation dialog
  const [open, setOpen] = useState(false); // Whether the confirmation dialog is open
  const [confirm, setConfirm] = useState(null); // Function to execute on confirmation
  const [cancel, setCancel] = useState(null); // Function to execute on cancellation

  /**
   * Handles generic changes to events.
   * Updates the status of all events with the same `inWeekId` as the provided event.
   * If any of these events have a status different from the old status, a confirmation dialog is shown.
   *
   * @param {Object} event - The event being updated.
   * @param {string} oldStatus - The previous status of the event.
   * @param {Function} cancel - The function to execute if the action is canceled.
   */
  const onGenericChange = (event, oldStatus, cancel) => {
    // Action to update the status of all related events
    const action = () => {
      eventService
        .getAll()
        .filter((e) => e.inWeekId === event.inWeekId) // Filter events with the same inWeekId
        .forEach((e) => {
          eventService.update(
            JSONToCalendarEvent({
              ...e,
              status: event.status, // Update the status
            }),
          );
        });
    };

    // Check if any related events have a status different from the old status
    if (
      eventService
        .getAll()
        .filter((e) => e.inWeekId === event.inWeekId)
        .find((e) => e.status !== oldStatus)
    ) {
      // Show the confirmation dialog
      setConfirm(() => action); // Set the confirmation action
      setCancel(() => cancel); // Set the cancellation action
      setOpen(true); // Open the dialog
    } else {
      // Directly execute the action if no conflicts are found
      action();
    }
  };

  return (
    <div>
      {/* Provide the context values to child components */}
      <CalendarContext.Provider
        value={{
          onGenericChange,
          eventService,
          genericEventService,
          initialEvents,
        }}
      >
        {children}
      </CalendarContext.Provider>

      {/* Render the confirmation dialog if open */}
      {open && (
        <ConfirmationDialog
          dialogTitle="Avertissement"
          dialogMessage="Certains créneaux sur cet horaire ont été modifiés manuellement. Cette action va écraser ces modifications."
          open={open}
          handleClose={() => setOpen(false)} // Close the dialog
          onConfirm={confirm} // Execute the confirmation action
          onCancel={cancel} // Execute the cancellation action
        />
      )}
    </div>
  );
}

// Define the expected prop types for the CalendarContextProvider component
CalendarContextProvider.propTypes = {
  /**
   * The child components that will have access to the CalendarContext.
   */
  children: PropTypes.node.isRequired,

  /**
   * The initial list of events to populate the calendar.
   * This is an optional array of event objects.
   */
  initialEvents: PropTypes.array,
};
