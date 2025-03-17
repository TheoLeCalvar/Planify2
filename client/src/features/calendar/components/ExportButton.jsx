// React imports
import React from "react";
import { useContext } from "react";

// Material-UI imports
import { Button } from "@mui/material";

// Local imports
import exportCalendar from "../utils/exportCalendar"; // Utility function for exporting calendar data
import { constants } from "@/config/constants"; // Application constants
import { CalendarContext } from "@/hooks/CalendarContext"; // Context for managing calendar state

/**
 * ExportButton Component
 * This component renders a button that allows users to export calendar data.
 * It retrieves events from the calendar context and passes them to the export utility.
 *
 * @returns {JSX.Element} - The rendered ExportButton component.
 */
export default function ExportButton() {
  // Access the calendar context to retrieve event services
  const { eventService, genericEventService } = useContext(CalendarContext);

  return (
    <Button
      variant="text" // Button style
      onClick={() => {
        // Trigger the export function when the button is clicked
        exportCalendar(
          eventService.getAll(), // Retrieve all events from the event service
          genericEventService.getAll(), // Retrieve all generic events
          constants.CALENDAR.TYPES.AVAILABILITY, // Specify the calendar type for export
        );
      }}
    >
      Exporter
    </Button>
  );
}
