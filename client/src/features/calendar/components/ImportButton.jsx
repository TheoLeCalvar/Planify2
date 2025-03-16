// React imports
import React from "react";
import { useContext } from "react";

// Local imports
import importCalendar from "../utils/importCalendar"; // Utility function for importing calendar data
import { constants } from "@/config/constants"; // Application constants
import { CalendarContext } from "@/hooks/CalendarContext"; // Context for managing calendar state
import ConfirmationButton from "@/components/ConfirmationButton"; // Reusable confirmation button component

/**
 * ImportButton Component
 * This component renders a button that allows users to import calendar data.
 * It displays a confirmation dialog before proceeding with the import.
 * If confirmed, it imports calendar data and updates the event services with the new data.
 *
 * @returns {JSX.Element} - The rendered ImportButton component.
 */
export default function ImportButton() {
  // Access the calendar context to retrieve event services
  const { eventService, genericEventService } = useContext(CalendarContext);

  return (
    <ConfirmationButton
      variant="text" // Button style
      buttonText="Importer" // Text displayed on the button
      dialogTitle="Avertissement" // Title of the confirmation dialog
      dialogMessage="Importer un calendrier écrasera les données actuelles." // Warning message in the dialog
      confirmText="Poursuivre l'importation" // Text for the confirmation button in the dialog
      onConfirm={() => {
        // Function executed when the user confirms the import
        importCalendar(constants.CALENDAR.TYPES.AVAILABILITY) // Import calendar data for the specified type
          .then(({ events, genericEvents }) => {
            // On successful import, update the event services with the new data
            eventService.set(events); // Set the imported events in the event service
            genericEventService.set(genericEvents); // Set the imported generic events in the generic event service
          })
          .catch((error) => {
            // Handle errors during the import process
            console.error(error);
          });
      }}
    />
  );
}
