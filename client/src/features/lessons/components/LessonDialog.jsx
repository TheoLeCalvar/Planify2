// React imports
import React, { useState, useContext } from "react";
import PropTypes from "prop-types";

// Material-UI imports
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Button,
  Box,
} from "@mui/material";

// Local imports
import { LessonsContext } from "@/hooks/LessonsContext";
import UserSelector from "@/components/UserSelector";

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

// Main LessonDialog component
const LessonDialog = ({ open, onClose, onSubmit, initialData }) => {
  // Local state for form fields
  const [title, setTitle] = useState(initialData?.title || "");
  const [description, setDescription] = useState(
    initialData?.description || "",
  );
  const [lecturers, setLecturers] = useState(initialData?.lecturers || []);

  const { lecturersList: options, setLecturersList } =
    useContext(LessonsContext);

  // Handlers for input changes
  const handleTitleChange = (e) => setTitle(e.target.value);
  const handleDescriptionChange = (e) => setDescription(e.target.value);

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
            <UserSelector
              lecturers={lecturers}
              options={options}
              setLecturers={setLecturers}
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
