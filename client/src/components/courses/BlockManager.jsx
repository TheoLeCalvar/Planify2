import React, { useEffect, useState } from "react";
import {
    Container,
    Paper,
    Button,
    Typography,
    Box,
    Stack,
} from "@mui/material";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import Block from "./Block";
import BlockDialog from "./BlockDialog";
import CourseDialog from "./CourseDialog";


// Détection de cycles dans les dépendances entre blocs
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
            return true; // Cycle détecté
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

export default function BlockManager() {
    const [blocks, setBlocks] = useState([]);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [editBlock, setEditBlock] = useState(null);
    const [currentBlockId, setCurrentBlockId] = useState(null);
    const [courseDialogOpen, setCourseDialogOpen] = useState(false);
    const [editingCourse, setEditingCourse] = useState(null);
    const [dependencyCycle, setDependencyCycle] = useState(null);

    useEffect(() => {
        setDependencyCycle(hasDependencyCycle(blocks));
    }, [blocks]);


    // Ouvrir la popup pour ajouter un bloc
    const handleAddBlock = () => {
        setEditBlock(null);
        setDialogOpen(true);
    };

    // Ouvrir la popup pour modifier un bloc
    const handleEditBlock = (block) => {
        setEditBlock(block);
        setDialogOpen(true);
    };

    const handleDuplicateBlock = (block) => {
        const { id, ...rest } = block;
        setEditBlock(rest);
        setDialogOpen(true);
    };

    // Sauvegarder un bloc (nouveau ou modifié)
    const handleSaveBlock = (block) => {
        if (block.id) {
            // Modification d'un bloc existant
            setBlocks((prev) =>
                prev.map((b) => (b.id === block.id ? block : b))
            );
        } else {
            if (block.courses){
                block.courses = block.courses.map((course, index) => ({ ...course, id: Date.now() + index }));
            }
            // Ajout d'un nouveau bloc
            setBlocks((prev) => [
                ...prev,
                { id: Date.now(), courses: [], ...block },
            ]);
        }
    };

    // Supprimer un bloc
    const handleDeleteBlock = (blockId) => {
        setBlocks((prev) => prev.filter((block) => block.id !== blockId));
    };

    // Gestion du drag and drop
    const handleDragEnd = (result) => {
        if (!result.destination) return;

        const source = result.source;
        const destination = result.destination;

        if (source.droppableId === "blocks" && destination.droppableId === "blocks") {
            // Réorganisation des blocs
            const reorderedBlocks = Array.from(blocks);
            const [removed] = reorderedBlocks.splice(source.index, 1);
            reorderedBlocks.splice(destination.index, 0, removed);
            setBlocks(reorderedBlocks);
        } else if (source.droppableId.startsWith("courses-") && destination.droppableId.startsWith("courses-")) {
            const sourceBlockId = parseInt(source.droppableId.split("-")[1], 10);
            const destinationBlockId = parseInt(destination.droppableId.split("-")[1], 10);

            const sourceBlock = blocks.find((block) => block.id === sourceBlockId);
            const destinationBlock = blocks.find((block) => block.id === destinationBlockId);

            const sourceCourses = Array.from(sourceBlock.courses);
            const [removed] = sourceCourses.splice(source.index, 1);

            if (sourceBlockId === destinationBlockId) {
                // Réorganisation des cours dans le même bloc
                sourceCourses.splice(destination.index, 0, removed);
                setBlocks((prev) =>
                    prev.map((block) =>
                        block.id === sourceBlockId
                            ? { ...block, courses: sourceCourses }
                            : block
                    )
                );
            } else {
                // Déplacement des cours entre blocs
                const destinationCourses = Array.from(destinationBlock.courses);
                destinationCourses.splice(destination.index, 0, removed);

                setBlocks((prev) =>
                    prev.map((block) => {
                        if (block.id === sourceBlockId) {
                            return { ...block, courses: sourceCourses };
                        } else if (block.id === destinationBlockId) {
                            return { ...block, courses: destinationCourses };
                        }
                        return block;
                    })
                );
            }
        }
    };

    // Exporter les données en JSON
    const handleExport = () => {
        const cycle = hasDependencyCycle(blocks);
        if (cycle) {
            alert("Erreur : des cycles de dépendances ont été détectés !");
            console.log("Cycle de dépendances :", cycle);
        } else {
            const json = JSON.stringify(blocks, null, 2);
            console.log(json);
            alert("Configuration exportée (voir console).");
        }
    };

    const handleAddCourse = (blockId) => {
        setCurrentBlockId(blockId);
        setEditingCourse(null);
        setCourseDialogOpen(true);
    };

    const handleEditCourse = (blockId, course) => {
        setCurrentBlockId(blockId);
        setEditingCourse(course);
        setCourseDialogOpen(true);
    };

    const handleDuplicateCourse = (blockId, course) => {
        const { id, ...rest } = course;
        setCurrentBlockId(blockId);
        setEditingCourse(rest);
        setCourseDialogOpen(true);
    };

    const handleDeleteCourse = (courseId) => {
        setBlocks((prevBlocks) =>
            prevBlocks.map((block) => {
                const updatedCourses = block.courses.filter(
                    (course) => course.id !== courseId
                );
                return { ...block, courses: updatedCourses };
            })
        );
    };

    const handleSaveCourse = (course) => {
        console.log(course, currentBlockId);
        setBlocks((prevBlocks) =>
            prevBlocks.map((block) => {
                if (block.id === currentBlockId) {
                    const updatedCourses = course.id
                        ? block.courses.map((c) =>
                              c.id === editingCourse.id ? { ...editingCourse, ...course } : c
                          )
                        : [...block.courses, { ...course, id: Date.now() }];
                    return { ...block, courses: updatedCourses };
                }
                return block;
            })
        );
    };

    return (
        <Container maxWidth="lg">
            <Paper elevation={3} sx={{ padding: 2, marginTop: 2 }}>
                <Typography variant="h5" align="center" gutterBottom>
                    Gestion des Blocs et Cours
                </Typography>
                {dependencyCycle && (
                    <Box sx={{ backgroundColor: "#ffcccc", padding: 1, marginBottom: 2 }}>
                        <Typography variant="body2" color="error">
                            Erreur : des cycles de dépendances ont été détectés !
                        </Typography>
                        <Typography variant="body2" color="error">
                            Cycle : {dependencyCycle.map(dependBlockId => blocks.find(block => block.id === dependBlockId)?.title).join(" -> ")}
                        </Typography>
                    </Box>
                )}
                <DragDropContext onDragEnd={handleDragEnd}>
                    <Droppable droppableId="blocks" type="block">
                        {(provided) => (
                            <Stack
                                ref={provided.innerRef}
                                {...provided.droppableProps}
                                spacing={2}
                            >
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
                                                    dependencies={block.dependencies.map((id) =>
                                                        blocks.find((b) => b.id === id)?.title
                                                    )}
                                                    onEdit={() =>
                                                        handleEditBlock(block)
                                                    }
                                                    onDelete={() =>
                                                        handleDeleteBlock(
                                                            block.id
                                                        )
                                                    }
                                                    onDuplicate={
                                                        () => handleDuplicateBlock(block)
                                                    }
                                                    onAddCourse={handleAddCourse}
                                                    onEditCourse={handleEditCourse}
                                                    onDeleteCourse={handleDeleteCourse}
                                                    onDuplicateCourse={handleDuplicateCourse}
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
                <Box mt={2} textAlign="center">
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleAddBlock}
                    >
                        Ajouter un bloc
                    </Button>
                    <Button
                        variant="contained"
                        color="success"
                        onClick={handleExport}
                        sx={{ ml: 2 }}
                    >
                        Exporter JSON
                    </Button>
                </Box>
            </Paper>
            {isDialogOpen && <BlockDialog
                open={isDialogOpen}
                onClose={() => setDialogOpen(false)}
                onSubmit={handleSaveBlock}
                initialData={editBlock}
                allBlocks={blocks}
            />}
            {courseDialogOpen && <CourseDialog
                open={courseDialogOpen}
                onClose={() => setCourseDialogOpen(false)}
                onSubmit={handleSaveCourse}
                initialData={editingCourse}
            />}
        </Container>
    );
}
