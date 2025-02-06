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
import { LessonsContext } from "../../../hooks/LessonsContext";
import CreateUser from "../../../components/createUser";

// Extracted style objects
const styles = {
  formContainer: {
    display: "flex",
    flexDirection: "column",
    gap: 2,
    mt: 1,
  },
  checkbox: { mr: 1 },
};

// Subcomponent for the Title and Description text fields
const LessonTextFields = ({
  title,
  description,
  onTitleChange,
  onDescriptionChange,
}) => (
  <>
    <TextField
      label="Titre"
      value={title}
      onChange={onTitleChange}
      fullWidth
      required
    />
    <TextField
      label="Description"
      value={description}
      onChange={onDescriptionChange}
      fullWidth
      multiline
      rows={3}
    />
  </>
);

LessonTextFields.propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
  onTitleChange: PropTypes.func.isRequired,
  onDescriptionChange: PropTypes.func.isRequired,
};

// Subcomponent for the lecturer Autocomplete field
const LecturerAutocomplete = ({
  lecturers,
  sortedOptions,
  onLecturersChange,
  renderTag,
  setOpenNewUser,
}) => (
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
      onChange={onLecturersChange}
      isOptionEqualToValue={(option, value) => option.id === value}
      renderOption={(props, option, { selected }) => {
        const { key, ...otherProps } = props;
        return (
          <li key={key} {...otherProps}>
            <Checkbox checked={selected} sx={styles.checkbox} />
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
          <Typography variant="body2">Aucun intervenant trouvé.</Typography>
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
);

LecturerAutocomplete.propTypes = {
  key: PropTypes.string,
  lecturers: PropTypes.array.isRequired,
  sortedOptions: PropTypes.array.isRequired,
  onLecturersChange: PropTypes.func.isRequired,
  renderTag: PropTypes.func.isRequired,
  setOpenNewUser: PropTypes.func.isRequired,
};

// Subcomponent for the CreateUser dialog
const CreateUserDialog = ({ open, onClose }) => (
  <Dialog open={open} onClose={onClose}>
    <DialogTitle>Créer un nouvel utilisateur</DialogTitle>
    <DialogContent>
      <CreateUser onCancel={onClose} />
    </DialogContent>
  </Dialog>
);

CreateUserDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

// Main LessonDialog component
const LessonDialog = ({ open, onClose, onSubmit, initialData }) => {
  // Local state for form fields
  const [title, setTitle] = useState(initialData?.title || "");
  const [description, setDescription] = useState(
    initialData?.description || "",
  );
  const [lecturers, setLecturers] = useState(initialData?.lecturers || []);
  const [openNewUser, setOpenNewUser] = useState(false);

  const { lecturersList: options, setLecturersList } =
    useContext(LessonsContext);

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
          : lecturer,
      ),
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
    (a, b) => Number(b.alreadySelected) - Number(a.alreadySelected),
  );

  // Helper to render a tag (Chip) from a lecturer id
  const renderTag = (optionId, index, getTagProps) => {
    const lecturer = options.find((opt) => opt.id === optionId);
    if (!lecturer) return null;
    const { key, ...chipProps } = getTagProps({ index }); // eslint-disable-line no-unused-vars
    return <Chip key={lecturer.id} label={lecturer.name} {...chipProps} />;
  };

  return (
    <>
      <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
        <DialogTitle>{getDialogTitle()}</DialogTitle>
        <DialogContent>
          <Box sx={styles.formContainer}>
            <LessonTextFields
              title={title}
              description={description}
              onTitleChange={handleTitleChange}
              onDescriptionChange={handleDescriptionChange}
            />
            <LecturerAutocomplete
              lecturers={lecturers}
              sortedOptions={sortedOptions}
              onLecturersChange={handleLecturersChange}
              renderTag={renderTag}
              setOpenNewUser={setOpenNewUser}
            />
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
      <CreateUserDialog
        open={openNewUser}
        onClose={() => setOpenNewUser(false)}
      />
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
      PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    ),
  }),
};

export default LessonDialog;
