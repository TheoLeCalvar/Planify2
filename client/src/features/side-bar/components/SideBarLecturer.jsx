import React, { Fragment } from "react";
import PropTypes from "prop-types";
import {
  Box,
  Collapse,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Stack,
  Typography,
} from "@mui/material";

// Styles
import styles from "./LessonList.styles";
import { ExpandLess, ExpandMore } from "@mui/icons-material";

const LessonListLecturer = ({ lessons }) => {
  const [open, setOpen] = React.useState(
    Object.fromEntries(lessons.map(({ id }) => [id, true])),
  );

  const handleOpenUE = (id) => {
    setOpen((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  return (
    <Box sx={styles.container}>
      <Typography variant="h6" sx={styles.title}>
        Vos cours
      </Typography>
      <List>
        {lessons.map(
          ({ id, title, description, managers, lessons: lessonList }) => (
            <Fragment key={id}>
              <ListItemButton onClick={() => handleOpenUE(id)}>
                <Stack direction="column" spacing={2}>
                  <ListItemText primary={title} secondary={description} />
                  {managers.length > 0 && (
                    <Typography variant="body2" color="textSecondary">
                      Responsables UE : {managers.map((m) => m.name).join(", ")}
                    </Typography>
                  )}
                </Stack>
                {open[id] ? <ExpandLess /> : <ExpandMore />}
              </ListItemButton>
              <Collapse in={open[id]} timeout="auto" unmountOnExit>
                <List component="div" disablePadding>
                  {lessonList.map((lesson) => (
                    <ListItem sx={{ pl: 4 }} key={lesson.id}>
                      <Stack direction="column">
                        <ListItemText
                          primary={lesson.title}
                          secondary={lesson.description}
                        />
                        {lesson.lecturers.length > 0 && (
                          <Typography variant="body2" color="textSecondary">
                            Autres intervenants :{" "}
                            {lesson.lecturers.map((m) => m.name).join(", ")}
                          </Typography>
                        )}
                        {lesson.synchronise.length > 0 && (
                          <>
                            <Typography variant="body2" color="textSecondary">
                              Synchronisé avec les cours :{" "}
                            </Typography>
                            {lesson.synchronise.map((sync) => (
                              <Typography
                                key={sync.id}
                                variant="body2"
                                color="textSecondary"
                              >
                                TAF {sync.taf}, UE {sync.ue}, Cours {sync.name}
                              </Typography>
                            ))}
                          </>
                        )}
                      </Stack>
                    </ListItem>
                  ))}
                </List>
              </Collapse>
            </Fragment>
          ),
        )}
      </List>
    </Box>
  );
};

LessonListLecturer.propTypes = {
  lessons: PropTypes.array.isRequired,
};

export default LessonListLecturer;
