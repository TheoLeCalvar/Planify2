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

// Extracted style objects
const styles = {
  formContainer: {
    display: "flex",
    flexDirection: "column",
    gap: 2,
    mt: 1,
  },
};

// Subcomponent for the dependencies select field
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
        .filter((block) => block.id !== initialData?.id)
        .map((block) => (
          <MenuItem key={block.id} value={block.id}>
            {block.title}
          </MenuItem>
        ))}
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

// Subcomponent for form fields: title, description, and dependencies
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

// Subcomponent for the dialog action buttons
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

// Main BlockDialog component
const BlockDialog = ({ open, onClose, onSubmit, initialData, allBlocks }) => {
  const [title, setTitle] = useState(initialData?.title || "");
  const [description, setDescription] = useState(
    initialData?.description || "",
  );
  const [dependencies, setDependencies] = useState(
    initialData?.dependencies || [],
  );

  const handleTitleChange = (e) => setTitle(e.target.value);
  const handleDescriptionChange = (e) => setDescription(e.target.value);
  const handleDependenciesChange = (e) => setDependencies(e.target.value);

  const getDialogTitle = () => {
    if (initialData?.id) return "Modifier le bloc";
    if (initialData) return "Dupliquer le bloc";
    return "Ajouter un bloc";
  };

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
