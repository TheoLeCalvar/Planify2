// React imports
import React from "react";
import { useContext } from "react";
import { useFetcher } from "react-router-dom";

// Material-UI imports
import LoadingButton from "@mui/lab/LoadingButton";
import SaveIcon from "@mui/icons-material/Save";

// Local imports
import { CalendarContext } from "@/hooks/CalendarContext";
import { CalendarEventToJSON } from "../utils/calendarEvent";

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
