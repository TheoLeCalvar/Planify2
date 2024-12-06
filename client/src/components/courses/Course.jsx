import React from "react";
import { Paper, Typography, Stack, Tooltip } from "@mui/material";
import { Box, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import FileCopyIcon from "@mui/icons-material/FileCopy";
import ConfirmationButton from "../ConfirmationButton";

export default function Course({ course, onEdit, onDelete, onDuplicate }) {
    return (
        <Paper
            elevation={1}
            sx={{
                padding: 1,
                marginBottom: 1,
                backgroundColor: "#f9f9f9",
            }}
        >
            <Stack direction="row" justifyContent="space-between" alignItems="center">
                <Typography variant="subtitle1">{course.title}</Typography>
                <Box>
                    <Tooltip title="Editer">
                        <IconButton onClick={onEdit} color="primary">
                            <EditIcon />
                        </IconButton>
                    </Tooltip>
                    <ConfirmationButton
                        buttonComponent={
                            <IconButton color="secondary">
                                <DeleteIcon />
                            </IconButton>
                        }
                        onConfirm={onDelete}
                        dialogTitle="Supprimer le cours ?"
                        tooltip={"Supprimer"}
                        dialogMessage={`Êtes-vous sûr de vouloir supprimer le cours '${course.title}' ?`}
                    />
                    <Tooltip title="Dupliquer">
                        <IconButton onClick={onDuplicate} color="secondary">
                            <FileCopyIcon />
                        </IconButton>
                    </Tooltip>
                </Box>
            </Stack>
            <Typography variant="body2" color="textSecondary">
                {course.description || "Aucune description fournie"}
            </Typography>
        </Paper>
    );
}
