import { Button } from "@mui/material";
import { useContext } from "react";
import { CalendarContext } from "../../context/CalendarContext";

import { convertCalendarToGeneric } from "../../helper/calendarEvent";
import ConfirmationButton from "../utils/ConfirmationButton";

import { toast } from "react-toastify";

export default function ResetButton() {
    const { eventService, genericEventService, initialEvents } =
        useContext(CalendarContext);

    const handleClick = () => {
        eventService.set(initialEvents);
        genericEventService.set(convertCalendarToGeneric(initialEvents));
        toast.info('Calendrier réinitialisé')
    };

    return (
        <ConfirmationButton
            buttonText="Réinitialiser"
            dialogMessage="Êtes-vous sûr de vouloir réinitialiser le calendrier à son état initial ?"
            onConfirm={handleClick}
        />
    );
}
