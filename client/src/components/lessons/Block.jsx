import React, { useState } from "react";
import PropTypes from "prop-types";
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
import { Draggable, Droppable } from "@hello-pangea/dnd";
import ConfirmationButton from "../utils/ConfirmationButton";
import Lesson from "./Lesson";

// Extracted style objects
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

// Subcomponent to display dependencies
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

// Subcomponent for the header actions (Add lesson, Edit, Delete, Duplicate)
const BlockActions = ({
  blockId,
  blockTitle,
  onAddLesson,
  onEdit,
  onDelete,
  onDuplicate,
  isHovered,
}) => (
  <Box sx={styles.actionBox(isHovered)}>
    <Button
      startIcon={<AddIcon />}
      variant="text"
      onClick={() => onAddLesson(blockId)}
      sx={styles.addButton}
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
};

// Subcomponent for rendering the lessons list using Droppable/Draggable
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

// Main Block component
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
