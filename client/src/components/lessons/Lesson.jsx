import React, { useState, useContext } from "react";
import PropTypes from "prop-types";
import {
  Box,
  Paper,
  Typography,
  Stack,
  Tooltip,
  IconButton,
  Chip,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import FileCopyIcon from "@mui/icons-material/FileCopy";
import ConfirmationButton from "../utils/ConfirmationButton";
import { LessonsContext } from "../../context/LessonsContext";

const Lesson = ({ lesson, onEdit, onDelete, onDuplicate }) => {
  const [isHovered, setIsHovered] = useState(false);
  const { lecturersList } = useContext(LessonsContext);

  const handleMouseEnter = () => setIsHovered(true);
  const handleMouseLeave = () => setIsHovered(false);

  // Helper to retrieve lecturer name from the lecturersList based on lecturer id
  const getLecturerName = (lecturerId) =>
    lecturersList.find((item) => item.id === lecturerId)?.name;

  return (
    <Box onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
      <Paper
        elevation={1}
        sx={{
          padding: 1,
          marginBottom: 1,
          backgroundColor: "#f9f9f9",
        }}
      >
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          {/* Course Title */}
          <Typography variant="subtitle1" sx={{ flexGrow: 1 }}>
            {lesson.title}
          </Typography>

          {/* Lecturer List */}
          {lesson.lecturers?.length > 0 && (
            <Stack
              direction="row"
              gap={1}
              alignItems="center"
              sx={{ marginLeft: "auto" }}
            >
              <Typography variant="body2" color="text.secondary">
                Intervenants :
              </Typography>
              {lesson.lecturers.map((lecturer) => (
                <Chip key={lecturer} label={getLecturerName(lecturer)} />
              ))}
            </Stack>
          )}

          {/* Action Buttons (Edit, Delete, Duplicate) */}
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              marginLeft: "16px",
              visibility: isHovered ? "visible" : "hidden",
            }}
          >
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
              tooltip="Supprimer"
              dialogMessage={`Êtes-vous sûr de vouloir supprimer le cours '${lesson.title}' ?`}
            />
            <Tooltip title="Dupliquer">
              <IconButton onClick={onDuplicate} color="secondary">
                <FileCopyIcon />
              </IconButton>
            </Tooltip>
          </Box>
        </Stack>

        {/* Course Description */}
        <Typography variant="body2" color="text.secondary">
          {lesson.description || "Aucune description fournie"}
        </Typography>
      </Paper>
    </Box>
  );
};

Lesson.propTypes = {
  lesson: PropTypes.shape({
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    lecturers: PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  }).isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onDuplicate: PropTypes.func.isRequired,
};

export default Lesson;
