// React imports
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";

// Material-UI imports
import {
  Container,
  Paper,
  Button,
  Typography,
  Box,
  Stack,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";

// DND imports
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";

// Local imports
import Block from "./Block";
import BlockDialog from "./BlockDialog";
import LessonDialog from "./LessonDialog";
import { toast } from "react-toastify";

// Helper: Detects cycles in block dependencies using DFS.
const hasDependencyCycle = (blocks) => {
  const graph = new Map();
  blocks.forEach((block) => {
    graph.set(block.id, block.dependencies || []);
  });

  const visited = new Set();
  const visiting = new Set();
  const stack = [];

  const dfs = (node) => {
    if (visiting.has(node)) {
      stack.push(node);
      return true; // Cycle detected
    }
    if (visited.has(node)) return false;

    visiting.add(node);
    stack.push(node);
    for (const neighbor of graph.get(node) || []) {
      if (dfs(neighbor)) return true;
    }
    visiting.delete(node);
    stack.pop();
    visited.add(node);
    return false;
  };

  for (const node of graph.keys()) {
    if (dfs(node)) {
      const cycleStart = stack.indexOf(node);
      return stack.slice(cycleStart);
    }
  }
  return null;
};

// Extracted style objects.
const styles = {
  paper: { p: 2, mt: 2 },
  errorBox: { backgroundColor: "#ffcccc", p: 1, mb: 2 },
  addBlockBox: { mt: 2, textAlign: "center" },
};

// Subcomponent: Renders an error message if a dependency cycle is detected.
const DependencyCycleError = ({ dependencyCycle, blocks }) => {
  if (!dependencyCycle) return null;
  const cycleTitles = dependencyCycle
    .map(
      (dependBlockId) =>
        blocks.find((block) => block.id === dependBlockId)?.title,
    )
    .join(" -> ");
  return (
    <Box sx={styles.errorBox}>
      <Typography variant="body2" color="error">
        Erreur : des cycles de dépendances ont été détectés !
      </Typography>
      <Typography variant="body2" color="error">
        Cycle : {cycleTitles}
      </Typography>
    </Box>
  );
};

DependencyCycleError.propTypes = {
  dependencyCycle: PropTypes.array,
  blocks: PropTypes.array.isRequired,
};

const EmptyBlockError = ({ blocks }) => {
  const errorBlock = blocks.find((block) => block.lessons.length === 0);
  if (!errorBlock) return null;
  return (
    <Box sx={styles.errorBox}>
      <Typography variant="body2" color="error">
        Erreur : tous les blocs doivent contenir au moins un cours.
      </Typography>
      <Typography variant="body2" color="error">
        Bloc vide : {errorBlock.title}
      </Typography>
    </Box>
  );
};

const SurchargeBlockError = ({ blocks }) => {
  const errorBlock = blocks.find((block) => block.lessons.length > 3);
  if (!errorBlock) return null;
  return (
    <Box sx={styles.errorBox}>
      <Typography variant="body2" color="error">
        Erreur : tous les blocs doivent contenir au maximum 3 cours.
      </Typography>
      <Typography variant="body2" color="error">
        Bloc surchargé : {errorBlock.title}
      </Typography>
    </Box>
  );
};

SurchargeBlockError.propTypes = {
  blocks: PropTypes.array.isRequired,
};

EmptyBlockError.propTypes = {
  blocks: PropTypes.array.isRequired,
};

// Subcomponent: Renders the blocks in a drag & drop context.
const BlocksDragDropList = ({
  blocks,
  onDragEnd,
  handleEditBlock,
  handleDeleteBlock,
  handleDuplicateBlock,
  handleAddLesson,
  handleEditLesson,
  handleDeleteLesson,
  handleDuplicateLesson,
}) => (
  <DragDropContext onDragEnd={onDragEnd}>
    <Droppable droppableId="blocks" type="block">
      {(provided) => (
        <Stack ref={provided.innerRef} {...provided.droppableProps} spacing={2}>
          {blocks.map((block, index) => (
            <Draggable
              key={block.id}
              draggableId={`block-${block.id}`}
              index={index}
            >
              {(provided) => (
                <div
                  ref={provided.innerRef}
                  {...provided.draggableProps}
                  {...provided.dragHandleProps}
                >
                  <Block
                    block={block}
                    dependencies={block.dependencies.map(
                      (id) => blocks.find((b) => b.id === id)?.title,
                    )}
                    onEdit={() => handleEditBlock(block)}
                    onDelete={() => handleDeleteBlock(block.id)}
                    onDuplicate={() => handleDuplicateBlock(block)}
                    onAddLesson={handleAddLesson}
                    onEditLesson={handleEditLesson}
                    onDeleteLesson={handleDeleteLesson}
                    onDuplicateLesson={handleDuplicateLesson}
                  />
                </div>
              )}
            </Draggable>
          ))}
          {provided.placeholder}
        </Stack>
      )}
    </Droppable>
  </DragDropContext>
);

BlocksDragDropList.propTypes = {
  blocks: PropTypes.array.isRequired,
  onDragEnd: PropTypes.func.isRequired,
  handleEditBlock: PropTypes.func.isRequired,
  handleDeleteBlock: PropTypes.func.isRequired,
  handleDuplicateBlock: PropTypes.func.isRequired,
  handleAddLesson: PropTypes.func.isRequired,
  handleEditLesson: PropTypes.func.isRequired,
  handleDeleteLesson: PropTypes.func.isRequired,
  handleDuplicateLesson: PropTypes.func.isRequired,
};

// Main component: BlockManager.
const BlockManager = ({
  lessonsData: blocks,
  setLessonsData: setBlocks,
  dependencyError: dependencyCycle,
  setDependencyError: setDependencyCycle,
}) => {
  // Local UI state for dialogs and editing items.
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [editBlock, setEditBlock] = useState(null);
  const [currentBlockId, setCurrentBlockId] = useState(null);
  const [lessonDialogOpen, setLessonDialogOpen] = useState(false);
  const [editingLesson, setEditingLesson] = useState(null);

  // Recompute dependency cycle whenever blocks are updated.
  useEffect(() => {
    setDependencyCycle(hasDependencyCycle(blocks));
  }, [blocks, setDependencyCycle]);

  /* === Block Handlers === */
  const handleAddBlock = () => {
    setEditBlock(null);
    setDialogOpen(true);
  };

  const handleEditBlock = (block) => {
    setEditBlock(block);
    setDialogOpen(true);
  };

  const handleDuplicateBlock = (block) => {
    const { id, ...rest } = block; // eslint-disable-line no-unused-vars
    setEditBlock(rest);
    setDialogOpen(true);
  };

  const handleSaveBlock = (block) => {
    if (block.id) {
      // Update existing block.
      toast.info("Bloc mis à jour", { autoClose: 1000 });
      setBlocks((prev) => prev.map((b) => (b.id === block.id ? block : b)));
    } else {
      // If there are lessons, assign them new ids and add the new block.
      if (block.lessons) {
        block.lessons = block.lessons.map((lesson, index) => ({
          ...lesson,
          id: Date.now() + index,
        }));
      }
      setBlocks((prev) => [...prev, { id: Date.now(), lessons: [], ...block }]);
      toast.info("Bloc ajouté", { autoClose: 1000 });
    }
  };

  const handleDeleteBlock = (blockId) => {
    setBlocks((prev) => prev.filter((block) => block.id !== blockId));
    toast.info("Bloc supprimé", { autoClose: 1000 });
  };

  /* === Drag & Drop Handler === */
  const handleDragEnd = (result) => {
    if (!result.destination) return;

    const { source, destination } = result;
    if (
      source.droppableId === "blocks" &&
      destination.droppableId === "blocks"
    ) {
      // Reorder blocks.
      const reorderedBlocks = Array.from(blocks);
      const [removed] = reorderedBlocks.splice(source.index, 1);
      reorderedBlocks.splice(destination.index, 0, removed);
      setBlocks(reorderedBlocks);
    } else if (
      source.droppableId.startsWith("lessons-") &&
      destination.droppableId.startsWith("lessons-")
    ) {
      // Handle lesson reordering or moving between blocks.
      const sourceBlockId = parseInt(source.droppableId.split("-")[1], 10);
      const destinationBlockId = parseInt(
        destination.droppableId.split("-")[1],
        10,
      );

      const sourceBlock = blocks.find((block) => block.id === sourceBlockId);
      const destinationBlock = blocks.find(
        (block) => block.id === destinationBlockId,
      );
      const sourceLessons = Array.from(sourceBlock.lessons);
      const [removed] = sourceLessons.splice(source.index, 1);

      if (sourceBlockId === destinationBlockId) {
        sourceLessons.splice(destination.index, 0, removed);
        setBlocks((prev) =>
          prev.map((block) =>
            block.id === sourceBlockId
              ? { ...block, lessons: sourceLessons }
              : block,
          ),
        );
      } else {
        const destinationLessons = Array.from(destinationBlock.lessons);
        destinationLessons.splice(destination.index, 0, removed);
        setBlocks((prev) =>
          prev.map((block) => {
            if (block.id === sourceBlockId) {
              return { ...block, lessons: sourceLessons };
            } else if (block.id === destinationBlockId) {
              return { ...block, lessons: destinationLessons };
            }
            return block;
          }),
        );
      }
    }
  };

  /* === Lesson Handlers === */
  const handleAddLesson = (blockId) => {
    setCurrentBlockId(blockId);
    setEditingLesson(null);
    setLessonDialogOpen(true);
  };

  const handleEditLesson = (blockId, lesson) => {
    setCurrentBlockId(blockId);
    setEditingLesson(lesson);
    setLessonDialogOpen(true);
  };

  const handleDuplicateLesson = (blockId, lesson) => {
    const { id, ...rest } = lesson; // eslint-disable-line no-unused-vars
    setCurrentBlockId(blockId);
    setEditingLesson(rest);
    setLessonDialogOpen(true);
  };

  const handleDeleteLesson = (lessonId) => {
    setBlocks((prevBlocks) =>
      prevBlocks.map((block) => {
        const updatedLessons = block.lessons.filter(
          (lesson) => lesson.id !== lessonId,
        );
        return { ...block, lessons: updatedLessons };
      }),
    );
    toast.info("Cours supprimé", { autoClose: 1000 });
  };

  const handleSaveLesson = (lesson) => {
    setBlocks((prevBlocks) =>
      prevBlocks.map((block) => {
        if (block.id === currentBlockId) {
          const updatedLessons = lesson.id
            ? block.lessons.map((l) =>
                l.id === editingLesson.id ? { ...editingLesson, ...lesson } : l,
              )
            : [...block.lessons, { ...lesson, id: Date.now() }];
          return { ...block, lessons: updatedLessons };
        }
        return block;
      }),
    );
    toast.info("Cours enregistré", { autoClose: 1000 });
  };

  return (
    <Container maxWidth="lg">
      <Paper elevation={3} sx={styles.paper}>
        <Typography variant="h5" align="center" gutterBottom>
          Gestion des Blocs et Cours
        </Typography>
        <DependencyCycleError
          dependencyCycle={dependencyCycle}
          blocks={blocks}
        />
        <EmptyBlockError blocks={blocks} />
        <SurchargeBlockError blocks={blocks} />
        <BlocksDragDropList
          blocks={blocks}
          onDragEnd={handleDragEnd}
          handleEditBlock={handleEditBlock}
          handleDeleteBlock={handleDeleteBlock}
          handleDuplicateBlock={handleDuplicateBlock}
          handleAddLesson={handleAddLesson}
          handleEditLesson={handleEditLesson}
          handleDeleteLesson={handleDeleteLesson}
          handleDuplicateLesson={handleDuplicateLesson}
        />
        <Box sx={styles.addBlockBox}>
          <Button
            variant="outlined"
            color="primary"
            startIcon={<AddIcon />}
            onClick={handleAddBlock}
          >
            Ajouter un bloc
          </Button>
        </Box>
      </Paper>
      {isDialogOpen && (
        <BlockDialog
          open={isDialogOpen}
          onClose={() => setDialogOpen(false)}
          onSubmit={handleSaveBlock}
          initialData={editBlock}
          allBlocks={blocks}
        />
      )}
      {lessonDialogOpen && (
        <LessonDialog
          open={lessonDialogOpen}
          onClose={() => setLessonDialogOpen(false)}
          onSubmit={handleSaveLesson}
          initialData={editingLesson}
        />
      )}
    </Container>
  );
};

BlockManager.propTypes = {
  lessonsData: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      title: PropTypes.string.isRequired,
      description: PropTypes.string,
      lessons: PropTypes.array,
      dependencies: PropTypes.array,
    }),
  ).isRequired,
  setLessonsData: PropTypes.func.isRequired,
  dependencyError: PropTypes.array,
  setDependencyError: PropTypes.func.isRequired,
};

export default BlockManager;
