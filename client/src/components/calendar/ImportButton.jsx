import React from "react";
import importCalendar from "../../helper/importCalendar";
import { constants } from "../../contants";
import { useContext } from "react";
import { CalendarContext } from "../../context/CalendarContext";
import ConfirmationButton from "../utils/ConfirmationButton";

export default function ImportButton() {
  const { eventService, genericEventService } = useContext(CalendarContext);

  return (
    <ConfirmationButton
      variant="text"
      buttonText="Importer"
      dialogTitle="Avertissement"
      dialogMessage="Importer un calendrier écrasera les données actuelles."
      confirmText="Poursuivre l'importation"
      onConfirm={() => {
        importCalendar(constants.CALENDAR.TYPES.AVAILABILITY)
          .then(({ events, genericEvents }) => {
            eventService.set(events);
            genericEventService.set(genericEvents);
          })
          .catch((error) => {
            console.error(error);
          });
      }}
    />
  );
}
