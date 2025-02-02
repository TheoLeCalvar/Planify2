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

  const renderDependencies = () => {
    if (dependencies.length > 0) {
      return (
        <Typography variant="body2" color="textSecondary">
          Précédence: {dependencies.join(", ")}
        </Typography>
      );
    }
    return (
      <Typography variant="body2" color="textSecondary">
        Pas de précédence
      </Typography>
    );
  };

  return (
    <Box onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
      <Paper elevation={2} sx={{ padding: 2 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">{block.title}</Typography>
          {renderDependencies()}
          <Box sx={{ visibility: isHovered ? "visible" : "hidden" }}>
            <Button
              startIcon={<AddIcon />}
              variant="text"
              onClick={() => onAddLesson(block.id)}
              sx={{ marginRight: 3 }}
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
              dialogMessage={`Êtes-vous sûr de vouloir supprimer le bloc '${block.title}' ?`}
            />
            <Tooltip title="Dupliquer">
              <IconButton onClick={onDuplicate} color="secondary">
                <FileCopyIcon />
              </IconButton>
            </Tooltip>
          </Box>
        </Stack>

        <Typography variant="body2" color="textSecondary" gutterBottom>
          {block.description || "Aucune description fournie"}
        </Typography>

        <Droppable droppableId={`lessons-${block.id}`} type="lesson">
          {(provided) => (
            <Box
              ref={provided.innerRef}
              {...provided.droppableProps}
              sx={{
                marginTop: 2,
                padding: 1,
                border: "1px dashed gray",
              }}
            >
              {block.lessons.map((lesson, index) => (
                <Draggable key={lesson.id} draggableId={`lesson-${lesson.id}`} index={index}>
                  {(provided) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.draggableProps}
                      {...provided.dragHandleProps}
                    >
                      <Lesson
                        lesson={lesson}
                        onEdit={() => onEditLesson(block.id, lesson)}
                        onDelete={() => onDeleteLesson(lesson.id)}
                        onDuplicate={() => onDuplicateLesson(block.id, lesson)}
                      />
                    </div>
                  )}
                </Draggable>
              ))}
              {provided.placeholder}
            </Box>
          )}
        </Droppable>
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
