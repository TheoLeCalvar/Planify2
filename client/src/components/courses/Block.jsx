import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import {
    Box,
    Button,
    IconButton,
    Paper,
    Stack,
    Tooltip,
    Typography,
} from "@mui/material";
import { Draggable, Droppable } from "@hello-pangea/dnd";
import Course from "./Course";
import FileCopyIcon from "@mui/icons-material/FileCopy";
import ConfirmationButton from "../ConfirmationButton";

export default function Block({
    block,
    dependencies,
    onEdit,
    onDelete,
    onDuplicate,
    onAddCourse,
    onEditCourse,
    onDeleteCourse,
    onDuplicateCourse,
}) {
    return (
        <Paper elevation={2} sx={{ padding: 2 }}>
            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
            >
                <Typography variant="h6">{block.title}</Typography>
                { dependencies.length > 0 ? (
                    <Typography variant="body2" color="textSecondary">
                        Précédence: {dependencies.join(", ")}
                    </Typography>
                ) :
                    <Typography variant="body2" color="textSecondary">
                        Pas de précédence
                    </Typography>
                }
                <Box>
                    <Button
                        startIcon={<AddIcon />}
                        variant="text"
                        onClick={() => onAddCourse(block.id)}
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
                        tooltip={"Supprimer"}
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
            <Droppable droppableId={`courses-${block.id}`} type="course">
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
                        {block.courses.map((course, index) => (
                            <Draggable
                                key={course.id}
                                draggableId={`course-${course.id}`}
                                index={index}
                            >
                                {(provided) => (
                                    <div
                                        ref={provided.innerRef}
                                        {...provided.draggableProps}
                                        {...provided.dragHandleProps}
                                    >
                                        <Course
                                            course={course}
                                            onEdit={() =>
                                                onEditCourse(block.id, course)
                                            }
                                            onDelete={() =>
                                                onDeleteCourse(course.id)
                                            }
                                            onDuplicate={() =>
                                                onDuplicateCourse(
                                                    block.id,
                                                    course
                                                )
                                            }
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
    );
}
