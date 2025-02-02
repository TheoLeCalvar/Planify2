import React, { useState, useContext } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Button,
  Box,
  FormControl,
  Autocomplete,
  Checkbox,
  Chip,
  Typography,
  Stack,
} from "@mui/material";
import { LessonsContext } from "../../context/LessonsContext";
import CreateUser from "../createUser";

const LessonDialog = ({ open, onClose, onSubmit, initialData }) => {
  // Local state for form fields
  const [title, setTitle] = useState(initialData?.title || "");
  const [description, setDescription] = useState(initialData?.description || "");
  const [lecturers, setLecturers] = useState(initialData?.lecturers || []);
  const [openNewUser, setOpenNewUser] = useState(false);

  const { lecturersList: options, setLecturersList } = useContext(LessonsContext);

  // Handlers for input changes
  const handleTitleChange = (e) => setTitle(e.target.value);
  const handleDescriptionChange = (e) => setDescription(e.target.value);
  const handleLecturersChange = (e, value) => {
    // Map lecturer objects to their ids (or keep id if already a primitive)
    setLecturers(value.map((lecturer) => lecturer.id || lecturer));
  };

  // Determine the dialog title based on initialData
  const getDialogTitle = () => {
    if (initialData?.id) return "Modifier le cours";
    if (initialData) return "Dupliquer le cours";
    return "Ajouter un cours";
  };

  // Update the lecturersList to mark selected lecturers
  const updateSelectedLecturers = () => {
    setLecturersList((prev) =>
      prev.map((lecturer) =>
        lecturers.includes(lecturer.id)
          ? { ...lecturer, alreadySelected: true }
          : lecturer
      )
    );
  };

  const handleSave = () => {
    if (!title.trim()) {
      alert("Le titre est obligatoire.");
      return;
    }
    updateSelectedLecturers();
    onSubmit({
      id: initialData?.id,
      title,
      description,
      lecturers,
    });
    onClose();
  };

  // Sort options by "alreadySelected" status (selected ones first)
  const sortedOptions = [...options].sort(
    (a, b) => Number(b.alreadySelected) - Number(a.alreadySelected)
  );

  // Helper to render a tag (Chip) from a lecturer id
  const renderTag = (optionId, index, getTagProps) => {
    const lecturer = options.find((opt) => opt.id === optionId);
    if (!lecturer) return null;
    const { key, ...chipProps } = getTagProps({ index });
    return <Chip key={lecturer.id} label={lecturer.name} {...chipProps} />;
  };

  return (
    <>
      <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
        <DialogTitle>{getDialogTitle()}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 1 }}>
            <TextField
              label="Titre"
              value={title}
              onChange={handleTitleChange}
              fullWidth
              required
            />
            <TextField
              label="Description"
              value={description}
              onChange={handleDescriptionChange}
              fullWidth
              multiline
              rows={3}
            />
            <FormControl fullWidth>
              <Autocomplete
                multiple
                options={sortedOptions}
                groupBy={(option) =>
                  option.alreadySelected
                    ? "Intervenants de cette TAF"
                    : "Toutes les personnes"
                }
                disableCloseOnSelect
                value={lecturers}
                getOptionLabel={(option) => option.name}
                onChange={handleLecturersChange}
                isOptionEqualToValue={(option, value) => option.id === value}
                renderOption={(props, option, { selected }) => {
                    const { key, ...otherProps } = props;
                    return (
                      <li key={key} {...otherProps}>
                        <Checkbox checked={selected} sx={{ mr: 1 }} />
                        {option.name}
                      </li>
                    );
                  }}
                renderTags={(value, getTagProps) =>
                  value.map((option, index) => renderTag(option, index, getTagProps))
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Intervenants"
                    placeholder="Sélectionnez des personnes..."
                  />
                )}
                noOptionsText={
                  <Stack gap={1} alignItems="center">
                    <Typography variant="body2">
                      Aucun intervenant trouvé.
                    </Typography>
                    <Button
                      color="primary"
                      variant="contained"
                      onClick={() => setOpenNewUser(true)}
                    >
                      Créer un nouvel intervenant
                    </Button>
                  </Stack>
                }
              />
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="secondary">
            Annuler
          </Button>
          <Button onClick={handleSave} variant="contained" color="primary">
            Enregistrer
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openNewUser} onClose={() => setOpenNewUser(false)}>
        <DialogTitle>Créer un nouvel utilisateur</DialogTitle>
        <DialogContent>
          <CreateUser onCancel={() => setOpenNewUser(false)} />
        </DialogContent>
      </Dialog>
    </>
  );
};

LessonDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
  initialData: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    title: PropTypes.string,
    description: PropTypes.string,
    lecturers: PropTypes.arrayOf(
      PropTypes.oneOfType([PropTypes.string, PropTypes.number])
    ),
  }),
};

export default LessonDialog;
