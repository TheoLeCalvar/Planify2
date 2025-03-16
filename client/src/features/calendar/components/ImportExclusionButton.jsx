// React imports
import { useContext } from "react";
import React, { useState } from "react";

// Material-UI imports
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  MenuItem,
  Select,
  Stack,
  Typography,
  FormControl,
  TextField,
  InputAdornment,
  Tooltip,
  IconButton,
  Box,
} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

// Local imports
import { constants } from "@/config/constants"; // Application constants
import locale from "@/config/locale.json"; // Localization strings
import { CalendarContext } from "@/hooks/CalendarContext"; // Context for managing calendar state
import importExclusionCalendar from "../utils/importExclusionCalendar"; // Utility function for importing exclusion calendars

/**
 * ImportExclusionButton Component
 * This component renders a button that allows users to import an external exclusion calendar.
 * It displays a dialog where users can configure the import options, such as:
 * - The status to assign to imported slots.
 * - An additional delay before and after imported events.
 * Once confirmed, the exclusion calendar is imported, and the calendar state is updated.
 *
 * @returns {JSX.Element} - The rendered ImportExclusionButton component.
 */
export default function ImportExclusionButton() {
  // Access the calendar context to retrieve the event service
  const { eventService } = useContext(CalendarContext);

  // State variables for managing the dialog and import options
  const [open, setOpen] = useState(false); // Dialog open state
  const [status, setStatus] = useState(
    constants.CALENDAR.SLOT_STATUS.UNAVAILABLE, // Default status for imported slots
  );
  const [delay, setDelay] = useState(0); // Default additional delay in minutes

  /**
   * Handles the selection of a status for imported slots.
   *
   * @param {Object} event - The change event.
   */
  const handleStatusSelect = (event) => {
    setStatus(event.target.value); // Update the selected status
  };

  /**
   * Handles the confirmation of the import process.
   * Imports the exclusion calendar and updates the calendar state with the new events.
   */
  const handleConfirm = () => {
    importExclusionCalendar(eventService.getAll(), delay, status) // Import the exclusion calendar
      .then((events) => {
        eventService.set(events); // Update the event service with the imported events
      })
      .catch((error) => {
        console.error(error); // Log any errors during the import process
      });
    setOpen(false); // Close the dialog
  };

  return (
    <>
      {/* Dialog for configuring the import options */}
      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        aria-labelledby="confirmation-dialog-title"
        aria-describedby="confirmation-dialog-description"
      >
        <DialogTitle id="confirmation-dialog-title">
          Importer un calendrier externe
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="confirmation-dialog-description">
            Importer un calendrier d&apos;indisponibilité modifiera la
            disponibilité de certains créneaux.
          </DialogContentText>

          <Stack spacing={2}>
            {/* Status selection */}
            <Stack direction="row" spacing={2} alignItems="center">
              <Box flexGrow={1}>
                <Stack direction="row" alignItems="center" spacing={1}>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    Statut à affecter aux créneaux importés
                  </Typography>
                  <Tooltip title="Ce statut sera appliqué à tous les créneaux en collision avec les événements importés.">
                    <IconButton size="small">
                      <InfoIcon fontSize="inherit" />
                    </IconButton>
                  </Tooltip>
                </Stack>
              </Box>
              <FormControl>
                <Select
                  onChange={handleStatusSelect}
                  value={status}
                  sx={{ flexGrow: 1 }}
                >
                  <MenuItem value={constants.CALENDAR.SLOT_STATUS.UNAVAILABLE}>
                    {locale.slotStatus.UNAVAILABLE}
                  </MenuItem>
                  <MenuItem value={constants.CALENDAR.SLOT_STATUS.UNPREFERRED}>
                    {locale.slotStatus.UNPREFERRED}
                  </MenuItem>
                  <MenuItem value={constants.CALENDAR.SLOT_STATUS.AVAILABLE}>
                    {locale.slotStatus.AVAILABLE}
                  </MenuItem>
                </Select>
              </FormControl>
            </Stack>

            {/* Additional delay configuration */}
            <Stack direction="row" spacing={2} alignItems="center">
              <Box flexGrow={1}>
                <Stack direction="row" alignItems="center" spacing={1}>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    Délai additionnel
                  </Typography>
                  <Tooltip title="Ajoute un délai d'indisponibilité avant et après les événements importés.">
                    <IconButton size="small">
                      <InfoIcon fontSize="inherit" />
                    </IconButton>
                  </Tooltip>
                </Stack>
              </Box>
              <FormControl>
                <TextField
                  type="number"
                  value={delay}
                  onChange={(e) => setDelay(e.target.value)}
                  slotProps={{
                    input: {
                      endAdornment: (
                        <InputAdornment position="end">minutes</InputAdornment>
                      ),
                    },
                    htmlInput: {
                      step: 5,
                      min: -30,
                      max: 60,
                    },
                  }}
                  sx={{ flexGrow: 1 }}
                />
              </FormControl>
            </Stack>
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)} color="secondary">
            Annuler
          </Button>
          <Button
            onClick={handleConfirm}
            variant="contained"
            color="primary"
            autoFocus
          >
            Continuer
          </Button>
        </DialogActions>
      </Dialog>

      {/* Button to open the dialog */}
      <Button variant="contained" color="primary" onClick={() => setOpen(true)}>
        Importer un calendrier d&apos;indisponibilité
      </Button>
    </>
  );
}
