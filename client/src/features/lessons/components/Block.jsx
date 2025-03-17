// React imports
import React, { useState } from "react";
import PropTypes from "prop-types";

// Material-UI imports
import {
  Box,
  Button,
  IconButton,
  Paper,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import FileCopyIcon from "@mui/icons-material/FileCopy";

// DND imports
import { Draggable, Droppable } from "@hello-pangea/dnd";

// Local imports
import ConfirmationButton from "@/components/ConfirmationButton";
import Lesson from "./Lesson";

// Extracted style objects for consistent styling
const styles = {
  paper: { padding: 2 },
  actionBox: (isHovered) => ({
    visibility: isHovered ? "visible" : "hidden",
  }),
  addButton: {
    marginRight: 3,
  },
  lessonContainer: {
    marginTop: 2,
    padding: 1,
    border: "1px dashed gray",
  },
};

/**
 * DependenciesDisplay Component
 * Displays the dependencies of the block.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.dependencies - The list of dependencies.
 * @returns {JSX.Element} - The rendered DependenciesDisplay component.
 */
const DependenciesDisplay = ({ dependencies }) => {
  const text =
    dependencies.length > 0
      ? `Précédence: ${dependencies.join(", ")}`
      : "Pas de précédence";
  return (
    <Typography variant="body2" color="textSecondary">
      {text}
    </Typography>
  );
};

DependenciesDisplay.propTypes = {
  dependencies: PropTypes.arrayOf(PropTypes.string).isRequired,
};

/**
 * BlockActions Component
 * Renders the action buttons for the block (Add Lesson, Edit, Delete, Duplicate).
 *
 * @param {Object} props - The component props.
 * @returns {JSX.Element} - The rendered BlockActions component.
 */
const BlockActions = ({
  blockId,
  blockTitle,
  onAddLesson,
  onEdit,
  onDelete,
  onDuplicate,
  isHovered,
  isAddDisabled,
}) => (
  <Box sx={styles.actionBox(isHovered)}>
    <Button
      startIcon={<AddIcon />}
      variant="text"
      onClick={() => onAddLesson(blockId)}
      sx={styles.addButton}
      disabled={isAddDisabled}
    >
      Ajouter un cours
    </Button>
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
      tooltip="Supprimer"
      dialogTitle="Supprimer le bloc ?"
      dialogMessage={`Êtes-vous sûr de vouloir supprimer le bloc '${blockTitle}' ?`}
    />
    <Tooltip title="Dupliquer">
      <IconButton onClick={onDuplicate} color="secondary">
        <FileCopyIcon />
      </IconButton>
    </Tooltip>
  </Box>
);

BlockActions.propTypes = {
  blockId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
  blockTitle: PropTypes.string.isRequired,
  onAddLesson: PropTypes.func.isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onDuplicate: PropTypes.func.isRequired,
  isHovered: PropTypes.bool.isRequired,
  isAddDisabled: PropTypes.bool.isRequired,
};

/**
 * LessonsList Component
 * Renders the list of lessons within the block using Droppable and Draggable components.
 *
 * @param {Object} props - The component props.
 * @returns {JSX.Element} - The rendered LessonsList component.
 */
const LessonsList = ({
  blockId,
  lessons,
  onEditLesson,
  onDeleteLesson,
  onDuplicateLesson,
}) => (
  <Droppable droppableId={`lessons-${blockId}`} type="lesson">
    {(provided) => (
      <Box
        ref={provided.innerRef}
        {...provided.droppableProps}
        sx={styles.lessonContainer}
      >
        {lessons.map((lesson, index) => (
          <Draggable
            key={lesson.id}
            draggableId={`lesson-${lesson.id}`}
            index={index}
          >
            {(provided) => (
              <div
                ref={provided.innerRef}
                {...provided.draggableProps}
                {...provided.dragHandleProps}
              >
                <Lesson
                  lesson={lesson}
                  onEdit={() => onEditLesson(blockId, lesson)}
                  onDelete={() => onDeleteLesson(lesson.id)}
                  onDuplicate={() => onDuplicateLesson(blockId, lesson)}
                />
              </div>
            )}
          </Draggable>
        ))}
        {provided.placeholder}
      </Box>
    )}
  </Droppable>
);

LessonsList.propTypes = {
  blockId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
  lessons: PropTypes.arrayOf(PropTypes.object).isRequired,
  onEditLesson: PropTypes.func.isRequired,
  onDeleteLesson: PropTypes.func.isRequired,
  onDuplicateLesson: PropTypes.func.isRequired,
};

/**
 * Block Component
 * The main component that represents a block containing lessons and actions.
 *
 * @param {Object} props - The component props.
 * @returns {JSX.Element} - The rendered Block component.
 */
const Block = ({
  block,
  dependencies,
  onEdit,
  onDelete,
  onDuplicate,
  onAddLesson,
  onEditLesson,
  onDeleteLesson,
  onDuplicateLesson,
}) => {
  const [isHovered, setIsHovered] = useState(false);

  const handleMouseEnter = () => setIsHovered(true);
  const handleMouseLeave = () => setIsHovered(false);

  return (
    <Box onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
      <Paper elevation={2} sx={styles.paper}>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
        >
          <Typography variant="h6">{block.title}</Typography>
          <DependenciesDisplay dependencies={dependencies} />
          <BlockActions
            blockId={block.id}
            blockTitle={block.title}
            onAddLesson={onAddLesson}
            onEdit={onEdit}
            onDelete={onDelete}
            onDuplicate={onDuplicate}
            isHovered={isHovered}
            isAddDisabled={block.lessons.length >= 3}
          />
        </Stack>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          {block.description || "Aucune description fournie"}
        </Typography>
        <LessonsList
          blockId={block.id}
          lessons={block.lessons}
          onEditLesson={onEditLesson}
          onDeleteLesson={onDeleteLesson}
          onDuplicateLesson={onDuplicateLesson}
        />
      </Paper>
    </Box>
  );
};

Block.propTypes = {
  block: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    lessons: PropTypes.arrayOf(PropTypes.object).isRequired,
  }).isRequired,
  dependencies: PropTypes.arrayOf(PropTypes.string).isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onDuplicate: PropTypes.func.isRequired,
  onAddLesson: PropTypes.func.isRequired,
  onEditLesson: PropTypes.func.isRequired,
  onDeleteLesson: PropTypes.func.isRequired,
  onDuplicateLesson: PropTypes.func.isRequired,
};

export default Block;
