import React, { useState } from "react";
import PropTypes from "prop-types";
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

const BlockDialog = ({ open, onClose, onSubmit, initialData, allBlocks }) => {
  const [title, setTitle] = useState(initialData?.title || "");
  const [description, setDescription] = useState(initialData?.description || "");
  const [dependencies, setDependencies] = useState(initialData?.dependencies || []);

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
            <InputLabel>Dépendances</InputLabel>
            <Select
              multiple
              value={dependencies}
              onChange={handleDependenciesChange}
              renderValue={(selected) =>
                selected
                  .map(
                    (id) =>
                      allBlocks.find((block) => block.id === id)?.title
                  )
                  .join(", ")
              }
              label="Dépendances"
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
      PropTypes.oneOfType([PropTypes.string, PropTypes.number])
    ),
  }),
  allBlocks: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      title: PropTypes.string.isRequired,
    })
  ).isRequired,
};

export default BlockDialog;
