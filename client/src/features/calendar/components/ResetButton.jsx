// React imports
import React from "react";
import { useContext } from "react";
import { toast } from "react-toastify";

// Local imports
import { CalendarContext } from "@/hooks/CalendarContext";
import { convertCalendarToGeneric } from "../utils/calendarEvent";
import ConfirmationButton from "@/components/ConfirmationButton";

export default function ResetButton() {
  const { eventService, genericEventService, initialEvents } =
    useContext(CalendarContext);

  const handleClick = () => {
    eventService.set(initialEvents);
    genericEventService.set(convertCalendarToGeneric(initialEvents));
    toast.info("Calendrier réinitialisé");
  };

  return (
    <ConfirmationButton
      buttonText="Réinitialiser"
      dialogMessage="Êtes-vous sûr de vouloir réinitialiser le calendrier à son état initial ?"
      onConfirm={handleClick}
    />
  );
}
