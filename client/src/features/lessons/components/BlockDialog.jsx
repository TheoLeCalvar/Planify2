// React imports
import React, { useState } from "react";
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
  MenuItem,
  Select,
  InputLabel,
  FormControl,
} from "@mui/material";

// Extracted style objects for consistent styling
const styles = {
  formContainer: {
    display: "flex",
    flexDirection: "column",
    gap: 2,
    mt: 1,
  },
};

/**
 * DependenciesSelect Component
 * Renders a select field for choosing dependencies from a list of blocks.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.dependencies - The currently selected dependencies.
 * @param {Function} props.onDependenciesChange - Callback for when dependencies are updated.
 * @param {Array} props.allBlocks - The list of all available blocks.
 * @param {Object} props.initialData - The initial data for the block being edited.
 * @returns {JSX.Element} - The rendered DependenciesSelect component.
 */
const DependenciesSelect = ({
  dependencies,
  onDependenciesChange,
  allBlocks,
  initialData,
}) => (
  <FormControl fullWidth>
    <InputLabel>Dépendances</InputLabel>
    <Select
      multiple
      value={dependencies}
      onChange={onDependenciesChange}
      label="Dépendances"
      renderValue={(selected) =>
        selected
          .map((id) => allBlocks.find((block) => block.id === id)?.title)
          .join(", ")
      }
    >
      {allBlocks
        .filter((block) => block.id !== initialData?.id) // Exclude the current block from the list
        .map((block) => (
          <MenuItem key={block.id} value={block.id}>
            {block.title}
          </MenuItem>
        ))}
      {allBlocks.length === 0 && (
        <MenuItem disabled>Aucun bloc disponible</MenuItem>
      )}
    </Select>
  </FormControl>
);

DependenciesSelect.propTypes = {
  dependencies: PropTypes.arrayOf(
    PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  ).isRequired,
  onDependenciesChange: PropTypes.func.isRequired,
  allBlocks: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      title: PropTypes.string.isRequired,
    }),
  ).isRequired,
  initialData: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  }),
};

/**
 * BlockFormFields Component
 * Renders the form fields for editing or creating a block, including title, description, and dependencies.
 *
 * @param {Object} props - The component props.
 * @returns {JSX.Element} - The rendered BlockFormFields component.
 */
const BlockFormFields = ({
  title,
  onTitleChange,
  description,
  onDescriptionChange,
  dependencies,
  onDependenciesChange,
  allBlocks,
  initialData,
}) => (
  <Box sx={styles.formContainer}>
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
    <DependenciesSelect
      dependencies={dependencies}
      onDependenciesChange={onDependenciesChange}
      allBlocks={allBlocks}
      initialData={initialData}
    />
  </Box>
);

BlockFormFields.propTypes = {
  title: PropTypes.string.isRequired,
  onTitleChange: PropTypes.func.isRequired,
  description: PropTypes.string,
  onDescriptionChange: PropTypes.func.isRequired,
  dependencies: PropTypes.arrayOf(
    PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  ).isRequired,
  onDependenciesChange: PropTypes.func.isRequired,
  allBlocks: PropTypes.array.isRequired,
  initialData: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  }),
};

/**
 * DialogActionsButtons Component
 * Renders the action buttons for the dialog (Cancel and Save).
 *
 * @param {Object} props - The component props.
 * @returns {JSX.Element} - The rendered DialogActionsButtons component.
 */
const DialogActionsButtons = ({ onClose, handleSave }) => (
  <DialogActions>
    <Button onClick={onClose} color="secondary">
      Annuler
    </Button>
    <Button onClick={handleSave} variant="contained" color="primary">
      Enregistrer
    </Button>
  </DialogActions>
);

DialogActionsButtons.propTypes = {
  onClose: PropTypes.func.isRequired,
  handleSave: PropTypes.func.isRequired,
};

/**
 * BlockDialog Component
 * The main dialog component for creating, editing, or duplicating a block.
 *
 * @param {Object} props - The component props.
 * @returns {JSX.Element} - The rendered BlockDialog component.
 */
const BlockDialog = ({ open, onClose, onSubmit, initialData, allBlocks }) => {
  // State variables for form fields
  const [title, setTitle] = useState(initialData?.title || "");
  const [description, setDescription] = useState(
    initialData?.description || "",
  );
  const [dependencies, setDependencies] = useState(
    initialData?.dependencies || [],
  );

  // Handlers for form field changes
  const handleTitleChange = (e) => setTitle(e.target.value);
  const handleDescriptionChange = (e) => setDescription(e.target.value);
  const handleDependenciesChange = (e) => setDependencies(e.target.value);

  // Determine the dialog title based on the action
  const getDialogTitle = () => {
    if (initialData?.id) return "Modifier le bloc";
    if (initialData) return "Dupliquer le bloc";
    return "Ajouter un bloc";
  };

  // Handle the save action
  const handleSave = () => {
    if (!title.trim()) {
      alert("Le titre est obligatoire.");
      return;
    }
    onSubmit({
      ...initialData,
      title,
      description,
      dependencies,
    });
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{getDialogTitle()}</DialogTitle>
      <DialogContent>
        <BlockFormFields
          title={title}
          onTitleChange={handleTitleChange}
          description={description}
          onDescriptionChange={handleDescriptionChange}
          dependencies={dependencies}
          onDependenciesChange={handleDependenciesChange}
          allBlocks={allBlocks}
          initialData={initialData}
        />
      </DialogContent>
      <DialogActionsButtons onClose={onClose} handleSave={handleSave} />
    </Dialog>
  );
};

BlockDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
  initialData: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    title: PropTypes.string,
    description: PropTypes.string,
    dependencies: PropTypes.arrayOf(
      PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    ),
  }),
  allBlocks: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      title: PropTypes.string.isRequired,
    }),
  ).isRequired,
};

export default BlockDialog;
