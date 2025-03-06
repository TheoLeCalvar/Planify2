import React from "react";
import PropTypes from "prop-types";
import { Box, List, ListItem, ListItemButton, Typography } from "@mui/material";
import { Link, useParams } from "react-router-dom";
import locale from "@/config/locale.json";

// Styles
import styles from "./LessonList.styles";

const LessonList = ({ lessons, tafID }) => {
  const params = useParams();

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
            <ListItemButton sx={styles.listItem} selected={params?.idUE == id}>
              <Typography variant="body1">{name}</Typography>
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
  lessons: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
      name: PropTypes.string.isRequired,
      managers: PropTypes.array.isRequired,
    }),
  ).isRequired,
  tafID: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
};

export default LessonList;
