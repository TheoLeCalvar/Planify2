import React, { useState } from "react";
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
    InputLabel,
    FormLabel,
    Typography,
    Stack,
} from "@mui/material";
import { useContext } from "react";
import { LessonsContext } from "../../context/LessonsContext";

export default function LessonDialog({ open, onClose, onSubmit, initialData }) {
    const [title, setTitle] = useState(initialData?.title || "");
    const [description, setDescription] = useState(
        initialData?.description || ""
    );
    const [lecturers, setLecturers] = useState(initialData?.lecturers || []);

    const { lecturersList: options, setLecturersList } =
        useContext(LessonsContext);

    const handleSave = () => {
        if (!title.trim()) {
            alert("Le titre est obligatoire.");
            return;
        }
        setLecturersList((prev) => {
            return prev.map((lecturer) => {
                if (lecturers.includes(lecturer.id)) {
                    return {
                        ...lecturer,
                        alreadySelected: true,
                    };
                }
                return lecturer;
            });
        });
        onSubmit({
            id: initialData?.id,
            title,
            description,
            lecturers,
        });
        onClose();
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>
                {initialData?.id
                    ? "Modifier le cours"
                    : initialData
                    ? "Dupliquer le cours"
                    : "Ajouter un cours"}
            </DialogTitle>
            <DialogContent>
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        gap: 2,
                        marginTop: 1,
                    }}
                >
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
                        <Autocomplete
                            multiple
                            options={options.sort(
                                (a, b) => b.alreadySelected - a.alreadySelected
                            )}
                            groupBy={(option) =>
                                option.alreadySelected
                                    ? "Intervenants de cette TAF"
                                    : "Toutes les personnes"
                            }
                            disableCloseOnSelect
                            value={lecturers}
                            getOptionLabel={(option) => option.name}
                            onChange={(e, value) =>
                                setLecturers(
                                    value.map(
                                        (lecturer) => lecturer.id || lecturer
                                    )
                                )
                            }
                            isOptionEqualToValue={(option, value) =>
                                option.id === value
                            }
                            renderOption={(props, option, { selected }) => {
                                console.log(option);
                                const { key, ...other } = props;
                                return (
                                    <li key={key} {...other}>
                                        <Checkbox
                                            checked={selected}
                                            style={{ marginRight: 8 }}
                                        />
                                        {option.name}
                                    </li>
                                );
                            }}
                            renderTags={(value, getTagProps) =>
                                value.map((option, index) => {
                                    console.log(value);
                                    const { key, ...tagProps } = getTagProps({
                                        index,
                                    });
                                    const lecturer = options.find(
                                        (opt) => opt.id === option
                                    );
                                    return (
                                        <Chip
                                            key={lecturer.id}
                                            label={lecturer.name}
                                            {...tagProps}
                                        />
                                    );
                                })
                            }
                            renderInput={(params) => (
                                <TextField
                                    {...params}
                                    label="Intervenants"
                                    placeholder="Sélectionnez des personnes..."
                                />
                            )}
                            noOptionsText={
                                <Stack gap={1} alignItems={"center"}>
                                    <Typography variant="body2">
                                        Aucun intervenant trouvé.
                                    </Typography>
                                    <Button color="primary" variant="contained">
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
                <Button
                    onClick={handleSave}
                    variant="contained"
                    color="primary"
                >
                    Enregistrer
                </Button>
            </DialogActions>
        </Dialog>
    );
}
