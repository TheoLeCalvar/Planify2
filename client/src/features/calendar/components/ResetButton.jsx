// React imports
import React from "react";
import { useContext } from "react";
import { toast } from "react-toastify"; // Library for displaying toast notifications

// Local imports
import { CalendarContext } from "@/hooks/CalendarContext"; // Context for managing calendar state
import { convertCalendarToGeneric } from "../utils/calendarEvent"; // Utility function to convert events to a generic format
import ConfirmationButton from "@/components/ConfirmationButton"; // Reusable confirmation button component

/**
 * ResetButton Component
 * This component renders a button that allows users to reset the calendar to its initial state.
 * It displays a confirmation dialog before proceeding with the reset.
 * Once confirmed, it resets both the event service and the generic event service to their initial states.
 *
 * @returns {JSX.Element} - The rendered ResetButton component.
 */
export default function ResetButton() {
  // Access the calendar context to retrieve event services and initial events
  const { eventService, genericEventService, initialEvents } =
    useContext(CalendarContext);

  /**
   * Handles the reset action.
   * Resets the event service and generic event service to their initial states
   * and displays a toast notification to inform the user.
   */
  const handleClick = () => {
    eventService.set(initialEvents); // Reset the event service to the initial events
    genericEventService.set(convertCalendarToGeneric(initialEvents)); // Reset the generic event service
    toast.info("Calendrier réinitialisé"); // Display a toast notification
  };

  return (
    <ConfirmationButton
      buttonText="Réinitialiser" // Text displayed on the button
      dialogMessage="Êtes-vous sûr de vouloir réinitialiser le calendrier à son état initial ?" // Confirmation dialog message
      onConfirm={handleClick} // Function executed when the user confirms the reset
    />
  );
}
