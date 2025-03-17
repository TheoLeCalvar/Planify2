// React imports
import React, { Fragment } from "react";
import PropTypes from "prop-types";

// Material-UI imports
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
import { ExpandLess, ExpandMore } from "@mui/icons-material";

// Styles
import styles from "./LessonList.styles";

/**
 * LessonListLecturer Component
 * This component renders a list of lessons (UEs) for lecturers in the sidebar.
 * Each UE can be expanded to show its associated lessons.
 * Lessons display their title, description, lecturers, and synchronization details.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.lessons - The list of UEs and their associated lessons.
 * @returns {JSX.Element} - The rendered LessonListLecturer component.
 */
const LessonListLecturer = ({ lessons }) => {
  // State to manage the open/close state of each UE
  const [open, setOpen] = React.useState(
    Object.fromEntries(lessons.map(({ id }) => [id, true])), // Initialize all UEs as open
  );

  /**
   * Toggles the open/close state of a specific UE.
   *
   * @param {string|number} id - The ID of the UE to toggle.
   */
  const handleOpenUE = (id) => {
    setOpen((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  return (
    <Box sx={styles.container}>
      {/* Sidebar title */}
      <Typography variant="h6" sx={styles.title}>
        Vos cours
      </Typography>

      {/* List of UEs */}
      <List>
        {lessons.map(
          ({ id, title, description, managers, lessons: lessonList }) => (
            <Fragment key={id}>
              {/* UE List Item */}
              <ListItemButton onClick={() => handleOpenUE(id)}>
                <Stack direction="column" spacing={2}>
                  {/* UE Title and Description */}
                  <ListItemText primary={title} secondary={description} />

                  {/* Display UE Managers if available */}
                  {managers.length > 0 && (
                    <Typography variant="body2" color="textSecondary">
                      Responsables UE : {managers.map((m) => m.name).join(", ")}
                    </Typography>
                  )}
                </Stack>

                {/* Expand/Collapse Icon */}
                {open[id] ? <ExpandLess /> : <ExpandMore />}
              </ListItemButton>

              {/* Collapsible List of Lessons */}
              <Collapse in={open[id]} timeout="auto" unmountOnExit>
                <List component="div" disablePadding>
                  {lessonList.map((lesson) => (
                    <ListItem sx={{ pl: 4 }} key={lesson.id}>
                      <Stack direction="column">
                        {/* Lesson Title and Description */}
                        <ListItemText
                          primary={lesson.title}
                          secondary={lesson.description}
                        />

                        {/* Display Other Lecturers if available */}
                        {lesson.lecturers.length > 0 && (
                          <Typography variant="body2" color="textSecondary">
                            Autres intervenants :{" "}
                            {lesson.lecturers.map((m) => m.name).join(", ")}
                          </Typography>
                        )}

                        {/* Display Synchronization Details if available */}
                        {lesson.synchronise.length > 0 && (
                          <>
                            <Typography variant="body2" color="textSecondary">
                              Synchronisé avec les cours :
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
  /**
   * The list of UEs and their associated lessons.
   * Each UE object should have:
   * - `id`: The unique identifier of the UE (string or number).
   * - `title`: The title of the UE (string).
   * - `description`: The description of the UE (string).
   * - `managers`: An array of manager objects, each with a `name` property.
   * - `lessons`: An array of lesson objects, each with:
   *   - `id`: The unique identifier of the lesson (string or number).
   *   - `title`: The title of the lesson (string).
   *   - `description`: The description of the lesson (string).
   *   - `lecturers`: An array of lecturer objects, each with a `name` property.
   *   - `synchronise`: An array of synchronization details, each with:
   *     - `taf`: The TAF ID (string or number).
   *     - `ue`: The UE ID (string or number).
   *     - `name`: The name of the synchronized lesson (string).
   */
  lessons: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      title: PropTypes.string.isRequired,
      description: PropTypes.string,
      managers: PropTypes.arrayOf(
        PropTypes.shape({
          name: PropTypes.string.isRequired,
        }),
      ),
      lessons: PropTypes.arrayOf(
        PropTypes.shape({
          id: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
            .isRequired,
          title: PropTypes.string.isRequired,
          description: PropTypes.string,
          lecturers: PropTypes.arrayOf(
            PropTypes.shape({
              name: PropTypes.string.isRequired,
            }),
          ),
          synchronise: PropTypes.arrayOf(
            PropTypes.shape({
              taf: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
                .isRequired,
              ue: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
                .isRequired,
              name: PropTypes.string.isRequired,
            }),
          ),
        }),
      ),
    }),
  ).isRequired,
};

export default LessonListLecturer;
