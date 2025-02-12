import React from "react";
import PropTypes from "prop-types";
import { Box, List, ListItem, Typography } from "@mui/material";
import { Link } from "react-router-dom";
import locale from "@/config/locale.json";

// Styles
import styles from "./LessonList.styles";

const LessonList = ({ lessons, tafID }) => {
  return (
    <Box sx={styles.container}>
      <Typography variant="h6" sx={styles.title}>
        {locale.layout.sideBar.UE}
      </Typography>
      <List>
        {lessons.map(({ id, name, managers }) => (
          <Link
            to={`/taf/${tafID}/ue/${id}`}
            key={id}
            style={{ textDecoration: "none", color: "inherit" }}
          >
            <ListItem sx={styles.listItem}>
              <Typography variant="body1">{name}</Typography>
              <Typography variant="body2" color="textSecondary">
                {locale.layout.sideBar.UEManager}: {managers.join(", ")}
              </Typography>
            </ListItem>
          </Link>
        ))}
      </List>
    </Box>
  );
};

LessonList.propTypes = {
  lessons: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
      name: PropTypes.string.isRequired,
      responsible: PropTypes.string.isRequired,
    }),
  ).isRequired,
  tafID: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
};

export default LessonList;
