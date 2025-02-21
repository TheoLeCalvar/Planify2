// React imports
import { React, createContext, useMemo } from "react";
import { useState } from "react";
import PropTypes from "prop-types";

// Schedule-X imports
import { createEventsServicePlugin } from "@schedule-x/events-service";

// Local imports
import ConfirmationDialog from "@/components/ConfirmationDialog";
import { JSONToCalendarEvent } from "@/features/calendar/utils/calendarEvent";

export const CalendarContext = createContext();

export default function CalendarContextProvider({ children, initialEvents }) {
  const eventService = useMemo(() => createEventsServicePlugin(), []);
  const genericEventService = useMemo(() => createEventsServicePlugin(), []);

  const [open, setOpen] = useState(false);
  const [confirm, setConfirm] = useState(null);
  const [cancel, setCancel] = useState(null);

  const onGenericChange = (event, oldStatus, cancel) => {
    const action = () => {
      eventService
        .getAll()
        .filter((e) => e.inWeekId === event.inWeekId)
        .forEach((e) => {
          eventService.update(
            JSONToCalendarEvent({
              ...e,
              status: event.status,
            }),
          );
        });
    };

    if (
      eventService
        .getAll()
        .filter((e) => e.inWeekId === event.inWeekId)
        .find((e) => e.status !== oldStatus)
    ) {
      setConfirm(() => action);
      setCancel(() => cancel);
      setOpen(true);
    } else {
      action();
    }
  };

  return (
    <div>
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
      {open && (
        <ConfirmationDialog
          dialogTitle="Avertissement"
          dialogMessage="Certains créneaux sur cet horaire ont été modifiés manuellement. Cette action va écraser ces modifications."
          open={open}
          handleClose={() => setOpen(false)}
          onConfirm={confirm}
          onCancel={cancel}
        />
      )}
    </div>
  );
}

CalendarContextProvider.propTypes = {
  children: PropTypes.node.isRequired,
  initialEvents: PropTypes.array,
};
