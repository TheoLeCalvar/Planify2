import React, { useState } from "react";
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
    Chip,
} from "@mui/material";

export default function BlockDialog({ open, onClose, onSubmit, initialData, allBlocks }) {
    const [title, setTitle] = useState(initialData?.title || "");
    const [description, setDescription] = useState(initialData?.description || "");
    const [dependencies, setDependencies] = useState(initialData?.dependencies || []);

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
            <DialogTitle>{initialData?.id ? "Modifier le bloc" : initialData ? "Dupliquer le bloc" : "Ajouter un bloc"}</DialogTitle>
            <DialogContent>
                <Box sx={{ display: "flex", flexDirection: "column", gap: 2, marginTop: 1 }}>
                    <TextField
                        label="Titre"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        fullWidth
                        required
                    />
                    <TextField
                        label="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        fullWidth
                        multiline
                        rows={3}
                    />
                    <FormControl fullWidth>
                        <InputLabel>Dépendances</InputLabel>
                        <Select
                            multiple
                            value={dependencies}
                            onChange={(e) => setDependencies(e.target.value)}
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
}
