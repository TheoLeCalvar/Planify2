import importExclusionCalendar from "../utils/importExclusionCalendar";
import { useContext } from "react";
import { CalendarContext } from "../../../hooks/CalendarContext";
import React, { useState } from "react";
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
import { constants } from "../../../config/constants";
import locale from "../../../config/locale.json";

export default function ImportExclusionButton() {
  const { eventService } = useContext(CalendarContext);

  const [open, setOpen] = useState(false);
  const [status, setStatus] = useState(
    constants.CALENDAR.SLOT_STATUS.UNAVAILABLE,
  );
  const [delay, setDelay] = useState(0);

  const handleStatusSelect = (event) => {
    setStatus(event.target.value);
  };

  const handleConfirm = () => {
    importExclusionCalendar(eventService.getAll(), delay, status)
      .then((events) => {
        eventService.set(events);
      })
      .catch((error) => {
        console.error(error);
      });
    setOpen(false);
  };

  return (
    <>
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
            {/* Sélection du statut */}
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

            {/* Délai additionnel */}
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
      <Button variant="contained" color="primary" onClick={() => setOpen(true)}>
        Importer un calendrier d&apos;indisponibilité
      </Button>
    </>
  );
}
