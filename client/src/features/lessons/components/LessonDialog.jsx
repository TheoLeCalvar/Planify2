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
  IconButton,
  Breadcrumbs,
  Stack,
  Tooltip,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";

// Local imports
import { LessonsContext } from "@/hooks/LessonsContext"; // Context for managing lessons
import UserSelector from "@/components/UserSelector"; // Component for selecting users
import LessonSelector from "@/components/LessonSelector"; // Component for selecting lessons
import { toast } from "react-toastify"; // Library for displaying toast notifications

// Extracted style objects for consistent styling
const styles = {
  formContainer: {
    display: "flex",
    flexDirection: "column",
    gap: 2,
    mt: 1,
  },
  checkbox: { mr: 1 },
};

/**
 * LessonTextFields Component
 * Renders the text fields for the lesson title and description.
 *
 * @param {Object} props - The component props.
 * @param {string} props.title - The title of the lesson.
 * @param {string} props.description - The description of the lesson.
 * @param {Function} props.onTitleChange - Callback for when the title changes.
 * @param {Function} props.onDescriptionChange - Callback for when the description changes.
 * @returns {JSX.Element} - The rendered LessonTextFields component.
 */
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

/**
 * LessonDialog Component
 * The main dialog component for creating, editing, or duplicating a lesson.
 *
 * @param {Object} props - The component props.
 * @param {boolean} props.open - Whether the dialog is open.
 * @param {Function} props.onClose - Callback for closing the dialog.
 * @param {Function} props.onSubmit - Callback for submitting the lesson data.
 * @param {Object} [props.initialData] - The initial data for the lesson (used for editing or duplicating).
 * @returns {JSX.Element} - The rendered LessonDialog component.
 */
const LessonDialog = ({ open, onClose, onSubmit, initialData }) => {
  // Local state for form fields
  const [title, setTitle] = useState(initialData?.title || "");
  const [description, setDescription] = useState(
    initialData?.description || "",
  );
  const [lecturers, setLecturers] = useState(initialData?.lecturers || []);
  const [synchroniseDialogOpen, setSynchroniseDialogOpen] = useState(false);
  const [synchronisedLesson, setSynchronisedLesson] = useState(
    initialData?.synchronise || [],
  );

  const { lecturersList: options, setLecturersList } =
    useContext(LessonsContext);

  // Handlers for input changes
  const handleTitleChange = (e) => setTitle(e.target.value);
  const handleDescriptionChange = (e) => setDescription(e.target.value);

  // Determine the dialog title based on the action
  const getDialogTitle = () => {
    if (initialData?.id) return "Modifier le cours";
    if (initialData) return "Dupliquer le cours";
    return "Ajouter un cours";
  };

  // Update the lecturers list to mark selected lecturers
  const updateSelectedLecturers = () => {
    setLecturersList((prev) =>
      prev.map((lecturer) =>
        lecturers.includes(lecturer.id)
          ? { ...lecturer, alreadySelected: true }
          : lecturer,
      ),
    );
  };

  // Handle saving the lesson
  const handleSave = () => {
    if (!title.trim()) {
      alert("Le titre est obligatoire.");
      return;
    }
    updateSelectedLecturers();
    onSubmit({
      id: initialData?.id,
      title: title.trim(),
      description,
      lecturers,
      synchronise: synchronisedLesson,
    });
    toast.info("Cours enregistré", { autoClose: 1000 });
    onClose();
  };

  // Handle validating the synchronisation with another lesson
  const handleValidateSynchronise = (lesson) => {
    setSynchronisedLesson([lesson]);
    setSynchroniseDialogOpen(false);
    toast.info("Synchronisation du cours activée", { autoClose: 1000 });
  };

  // Handle cancelling the synchronisation
  const handleCancelSynchronise = () => {
    setSynchronisedLesson([]);
    toast.info("Synchronisation du cours supprimée", { autoClose: 1000 });
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
            {synchronisedLesson.length > 0 ? (
              <Stack direction="row" alignItems="center" gap={3}>
                <p>Synchronisé avec le cours : </p>
                <Breadcrumbs
                  aria-label="breadcrumb"
                  separator={<NavigateNextIcon fontSize="small" />}
                >
                  {[
                    synchronisedLesson[0].taf,
                    synchronisedLesson[0].ue,
                    synchronisedLesson[0].name || synchronisedLesson[0].title,
                  ].map((b, index) => (
                    <span key={index}>{b}</span>
                  ))}
                </Breadcrumbs>
                <Tooltip title="Supprimer la synchronisation">
                  <IconButton
                    onClick={handleCancelSynchronise}
                    sx={{ marginLeft: "auto" }}
                  >
                    <DeleteIcon />
                  </IconButton>
                </Tooltip>
              </Stack>
            ) : (
              <Button
                onClick={() => setSynchroniseDialogOpen(true)}
                color="primary"
                variant="outlined"
              >
                Synchroniser avec un autre cours
              </Button>
            )}
            {synchroniseDialogOpen && (
              <Dialog open={synchroniseDialogOpen}>
                <DialogTitle>Synchroniser avec un autre cours...</DialogTitle>
                <DialogContent>
                  <LessonSelector onValidate={handleValidateSynchronise} />
                </DialogContent>
                <DialogActions>
                  <Button
                    onClick={() => setSynchroniseDialogOpen(false)}
                    color="secondary"
                  >
                    Annuler
                  </Button>
                </DialogActions>
              </Dialog>
            )}
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
    synchronise: PropTypes.array,
  }),
};

export default LessonDialog;
