// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Box, List, ListItemButton, Typography } from "@mui/material";

// React Router imports
import { Link, useParams } from "react-router-dom";

// Localization
import locale from "@/config/locale.json";

// Styles
import styles from "./LessonList.styles";

/**
 * LessonList Component
 * This component renders a list of lessons (UEs) in the sidebar.
 * Each lesson is displayed as a clickable link that navigates to the corresponding UE page.
 * If a lesson has managers, their names are displayed below the lesson name.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.lessons - The list of lessons to display.
 * @param {string|number} props.tafID - The ID of the TAF to which the lessons belong.
 * @returns {JSX.Element} - The rendered LessonList component.
 */
const LessonList = ({ lessons, tafID }) => {
  // Get the current route parameters
  const params = useParams();

  return (
    <Box sx={styles.container}>
      {/* Sidebar title */}
      <Typography variant="h6" sx={styles.title}>
        {locale.layout.sideBar.UE}
      </Typography>

      {/* List of lessons */}
      <List>
        {lessons.map(({ id, name, managers }) => (
          <Link
            to={`/taf/${tafID}/ue/${id}`} // Dynamic link to the lesson page
            key={id}
            style={{ textDecoration: "none", color: "inherit" }} // Remove default link styling
          >
            <ListItemButton
              sx={styles.listItem}
              selected={params?.idUE == id} // Highlight the selected lesson
            >
              {/* Lesson name */}
              <Typography variant="body1">{name}</Typography>

              {/* Display managers if available */}
              {managers.length > 0 && (
                <Typography variant="body2" color="textSecondary">
                  {locale.layout.sideBar.UEManager}:{" "}
                  {managers.map((m) => m.name).join(", ")}
                </Typography>
              )}
            </ListItemButton>
          </Link>
        ))}
      </List>
    </Box>
  );
};

LessonList.propTypes = {
  /**
   * The list of lessons to display.
   * Each lesson object should have:
   * - `id`: The unique identifier of the lesson (string or number).
   * - `name`: The name of the lesson (string).
   * - `managers`: An array of manager objects, each with a `name` property.
   */
  lessons: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
      name: PropTypes.string.isRequired,
      managers: PropTypes.array.isRequired,
    }),
  ).isRequired,

  /**
   * The ID of the TAF to which the lessons belong.
   * This is used to construct the navigation links.
   */
  tafID: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
};

export default LessonList;
