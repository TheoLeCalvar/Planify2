// React imports
import React, { useState, useContext } from "react";
import PropTypes from "prop-types";

// Material-UI imports
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

// Local imports
import ConfirmationButton from "@/components/ConfirmationButton";
import { LessonsContext } from "@/hooks/LessonsContext";

// Extracted style objects
const styles = {
  paper: {
    padding: 1,
    marginBottom: 1,
    backgroundColor: "#f9f9f9",
  },
  lessonTitle: {
    flexGrow: 1,
  },
  lecturerList: {
    marginLeft: "auto",
    display: "flex",
    gap: 1,
    alignItems: "center",
  },
  actionBox: (isHovered) => ({
    display: "flex",
    alignItems: "center",
    marginLeft: "16px",
    visibility: isHovered ? "visible" : "hidden",
  }),
};

/**
 * LessonTitle Component
 * Displays the title of the lesson.
 *
 * @param {Object} props - The component props.
 * @param {string} props.title - The title of the lesson.
 * @returns {JSX.Element} - The rendered LessonTitle component.
 */
const LessonTitle = ({ title }) => (
  <Typography variant="subtitle1" sx={styles.lessonTitle}>
    {title}
  </Typography>
);

LessonTitle.propTypes = {
  title: PropTypes.string.isRequired,
};

/**
 * LecturerList Component
 * Displays a list of lecturers associated with the lesson.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.lecturers - The list of lecturer IDs.
 * @param {Function} props.getLecturerName - Function to retrieve lecturer names by ID.
 * @returns {JSX.Element|null} - The rendered LecturerList component or null if no lecturers are provided.
 */
const LecturerList = ({ lecturers, getLecturerName }) => {
  if (!lecturers || lecturers.length === 0) return null;

  return (
    <Stack direction="row" sx={styles.lecturerList}>
      <Typography variant="body2" color="text.secondary">
        Intervenants :
      </Typography>
      {lecturers.map((lecturer) => (
        <Chip key={lecturer} label={getLecturerName(lecturer)} />
      ))}
    </Stack>
  );
};

LecturerList.propTypes = {
  lecturers: PropTypes.arrayOf(
    PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  ),
  getLecturerName: PropTypes.func.isRequired,
};

/**
 * LessonActions Component
 * Renders action buttons for the lesson (Edit, Delete, Duplicate).
 *
 * @param {Object} props - The component props.
 * @param {boolean} props.isHovered - Whether the lesson is hovered.
 * @param {Function} props.onEdit - Callback for editing the lesson.
 * @param {Function} props.onDelete - Callback for deleting the lesson.
 * @param {Function} props.onDuplicate - Callback for duplicating the lesson.
 * @param {string} props.lessonTitle - The title of the lesson.
 * @returns {JSX.Element} - The rendered LessonActions component.
 */
const LessonActions = ({
  isHovered,
  onEdit,
  onDelete,
  onDuplicate,
  lessonTitle,
}) => (
  <Box sx={styles.actionBox(isHovered)}>
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
      dialogMessage={`Êtes-vous sûr de vouloir supprimer le cours '${lessonTitle}' ?`}
    />
    <Tooltip title="Dupliquer">
      <IconButton onClick={onDuplicate} color="secondary">
        <FileCopyIcon />
      </IconButton>
    </Tooltip>
  </Box>
);

LessonActions.propTypes = {
  isHovered: PropTypes.bool.isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onDuplicate: PropTypes.func.isRequired,
  lessonTitle: PropTypes.string.isRequired,
};

/**
 * LessonDescription Component
 * Displays the description of the lesson.
 *
 * @param {Object} props - The component props.
 * @param {string} props.description - The description of the lesson.
 * @returns {JSX.Element} - The rendered LessonDescription component.
 */
const LessonDescription = ({ description }) => (
  <Typography variant="body2" color="text.secondary">
    {description || "Aucune description fournie"}
  </Typography>
);

LessonDescription.propTypes = {
  description: PropTypes.string,
};

/**
 * Lesson Component
 * The main component that represents a lesson.
 *
 * @param {Object} props - The component props.
 * @param {Object} props.lesson - The lesson data.
 * @param {Function} props.onEdit - Callback for editing the lesson.
 * @param {Function} props.onDelete - Callback for deleting the lesson.
 * @param {Function} props.onDuplicate - Callback for duplicating the lesson.
 * @returns {JSX.Element} - The rendered Lesson component.
 */
const Lesson = ({ lesson, onEdit, onDelete, onDuplicate }) => {
  const [isHovered, setIsHovered] = useState(false);
  const { lecturersList } = useContext(LessonsContext);

  const handleMouseEnter = () => setIsHovered(true);
  const handleMouseLeave = () => setIsHovered(false);

  // Helper to retrieve lecturer name from the lecturersList based on lecturer ID
  const getLecturerName = (lecturerId) =>
    lecturersList.find((item) => item.id === lecturerId)?.name;

  return (
    <Box onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
      <Paper elevation={1} sx={styles.paper}>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          spacing={2}
        >
          <LessonTitle title={lesson.title} />
          {lesson.synchronise.length > 0 && lesson.synchronise[0] && (
            <Chip label="Synchronisé" color="primary" variant="outlined" />
          )}
          <LecturerList
            lecturers={lesson.lecturers}
            getLecturerName={getLecturerName}
          />
          <LessonActions
            isHovered={isHovered}
            onEdit={onEdit}
            onDelete={onDelete}
            onDuplicate={onDuplicate}
            lessonTitle={lesson.title}
          />
        </Stack>
        <LessonDescription description={lesson.description} />
      </Paper>
    </Box>
  );
};

Lesson.propTypes = {
  lesson: PropTypes.shape({
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    lecturers: PropTypes.arrayOf(
      PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    ),
    synchronise: PropTypes.array,
  }).isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onDuplicate: PropTypes.func.isRequired,
};

export default Lesson;
