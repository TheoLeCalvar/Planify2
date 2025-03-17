// React imports
import React from "react";
import { useContext } from "react";
import { useFetcher } from "react-router-dom"; // React Router utility for handling form submissions

// Material-UI imports
import LoadingButton from "@mui/lab/LoadingButton"; // Button with a loading state
import SaveIcon from "@mui/icons-material/Save"; // Save icon for the button

// Local imports
import { CalendarContext } from "@/hooks/CalendarContext"; // Context for managing calendar state
import { CalendarEventToJSON } from "../utils/calendarEvent"; // Utility function to convert calendar events to JSON format

/**
 * SaveButton Component
 * This component renders a button that allows users to save calendar data.
 * It uses the `useFetcher` hook from React Router to submit the calendar events to the server.
 * The button displays a loading state while the save operation is in progress.
 *
 * @returns {JSX.Element} - The rendered SaveButton component.
 */
export default function SaveButton() {
  // Fetcher instance for handling form submissions
  const fetcher = useFetcher();

  // Access the calendar context to retrieve the event service
  const { eventService } = useContext(CalendarContext);

  // Determine if the fetcher is busy (i.e., a save operation is in progress)
  const busy = fetcher.state !== "idle";

  return (
    <LoadingButton
      variant="contained" // Button style
      onClick={() =>
        // Submit the calendar events to the server
        fetcher.submit(
          eventService.getAll().map(CalendarEventToJSON), // Convert events to JSON format
          {
            encType: "application/json", // Specify the content type
            method: "post", // Use the POST method for submission
          },
        )
      }
      loading={busy} // Display a loading spinner if the fetcher is busy
      disabled={busy} // Disable the button while the fetcher is busy
      loadingPosition="start" // Position the loading spinner at the start of the button
      startIcon={<SaveIcon />} // Display a save icon on the button
    >
      Sauvegarder
    </LoadingButton>
  );
}
