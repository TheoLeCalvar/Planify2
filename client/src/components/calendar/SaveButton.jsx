import React from "react";

import LoadingButton from "@mui/lab/LoadingButton";
import { useContext } from "react";
import { CalendarContext } from "../../context/CalendarContext";
import { useFetcher } from "react-router-dom";
import SaveIcon from "@mui/icons-material/Save";
import { CalendarEventToJSON } from "../../helper/calendarEvent";

export default function ExportButton() {
  const fetcher = useFetcher();

  const { eventService } = useContext(CalendarContext);

  const busy = fetcher.state !== "idle";

  return (
    <LoadingButton
      variant="contained"
      onClick={() =>
        fetcher.submit(eventService.getAll().map(CalendarEventToJSON), {
          encType: "application/json",
          method: "post",
        })
      }
      loading={busy}
      disabled={busy}
      loadingPosition="start"
      startIcon={<SaveIcon />}
    >
      Sauvegarder
    </LoadingButton>
  );
}
