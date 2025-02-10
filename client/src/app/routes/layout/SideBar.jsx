// React imports
import React, { useCallback, useReducer } from "react";
import PropTypes from "prop-types";
import { Link, Outlet, useOutletContext } from "react-router-dom";

// Material-UI imports
import {
  Drawer,
  Box,
  List,
  ListItem,
  ListItemText,
  ListItemButton,
  Typography,
  Button,
  CssBaseline,
  Stack,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";

// Local imports
import locale from "@/config/locale.json";
import useStore from "@/store/store";
import axiosInstance from "@/config/axiosConfig";
import { drawerWidth, appBarHeight } from "@/config/constants";

// Extracted styles for the sidebar components
const styles = {
  drawer: {
    width: drawerWidth,
    "& .MuiDrawer-paper": {
      width: drawerWidth,
      boxSizing: "border-box",
      top: appBarHeight,
      height: "calc(100vh - ${appBarHeight}px)",
      position: "fixed",
      overflowX: "hidden",
    },
  },
  sidebarContainer: {
    width: drawerWidth,
    display: "flex",
    flexDirection: "column",
    height: "90vh",
  },
  lessonList: {
    flexGrow: 1, // Takes all available vertical space
    overflowY: "auto", // Scrolls if content is too long
    px: 2,
  },
  sidebarActions: {
    px: 2,
    py: 2,
    mt: "auto", // Pushes this section to the bottom
  },
  mainContent: {
    flexGrow: 1, // Takes all available space
    padding: 3,
    paddingTop: appBarHeight + "px",
  },
};

// Reducer to manage UI state
const initialState = {
  generatingCalendar: false,
};

function reducer(state, action) {
  switch (action.type) {
    case "START_GENERATION":
      return { ...state, generatingCalendar: true };
    case "END_GENERATION":
      return { ...state, generatingCalendar: false };
    default:
      return state;
  }
}

// Lesson List Component
const LessonList = ({ lessons, tafID }) => (
  <Box sx={styles.lessonList}>
    <Typography variant="h6" sx={styles.lessonListTitle}>
      {locale.layout.sideBar.UE}
    </Typography>
    <List>
      {lessons.map(({ id, name, responsible }) => (
        <Link
          to={`/taf/${tafID}/ue/${id}`}
          key={id}
          style={{ textDecoration: "none", color: "inherit" }}
        >
          <ListItem sx={{ display: "flex", flexDirection: "column" }}>
            <Typography variant="body1">{name}</Typography>
            <Typography variant="body2" color="textSecondary">
              {locale.layout.sideBar.UEManager}: {responsible}
            </Typography>
          </ListItem>
        </Link>
      ))}
    </List>
  </Box>
);

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

// Sidebar Actions (Buttons)
const SidebarActions = ({
  tafID,
  resultPlanning,
  handleGenerateCalendar,
  generatingCalendar,
}) => (
  <Box sx={styles.sidebarActions}>
    <Stack spacing={2}>
      {resultPlanning?.length > 0 && (
        <Link to={`/taf/${tafID}/results`}>
          <Button variant="outlined" sx={{ width: "100%" }}>
            Voir les calendriers
          </Button>
        </Link>
      )}
      <Button
        onClick={handleGenerateCalendar}
        variant="outlined"
        sx={{ width: "100%" }}
        disabled={generatingCalendar}
      >
        {generatingCalendar ? "Génération..." : "Générer le calendrier"}
      </Button>
      <Button
        variant="contained"
        startIcon={<AddIcon />}
        sx={{ width: "100%" }}
      >
        {locale.layout.sideBar.addUE}
      </Button>
    </Stack>
  </Box>
);

SidebarActions.propTypes = {
  tafID: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
  resultPlanning: PropTypes.array,
  handleGenerateCalendar: PropTypes.func.isRequired,
  generatingCalendar: PropTypes.bool.isRequired,
};

// Main Sidebar Component
const SideBar = () => {
  const isOpen = useStore((state) => state.sideBarOpen);

  // Destructure the taf object from the outlet context
  const { taf } = useOutletContext();
  const { UE: lessons, id: tafID, resultPlanning } = taf;

  const [state, dispatch] = useReducer(reducer, initialState);

  const handleGenerateCalendar = useCallback(async () => {
    dispatch({ type: "START_GENERATION" });
    try {
      await axiosInstance.get(`/solver/run/${tafID}`);
    } catch (error) {
      console.error("Error generating calendar:", error);
    } finally {
      dispatch({ type: "END_GENERATION" });
    }
  }, [tafID]);

  return (
    <Box sx={{ display: "flex" }}>
      <CssBaseline />

      <Drawer
        variant="persistent"
        anchor="left"
        open={isOpen}
        sx={styles.drawer}
      >
        <Box sx={styles.sidebarContainer} role="presentation">
          {/* Sidebar Navigation */}
          <List>
            <ListItemButton>
              <ListItemText primary={locale.layout.sideBar.status} />
            </ListItemButton>
            <ListItemButton component={Link} to="calendar">
              <ListItemText primary={locale.layout.sideBar.calendar} />
            </ListItemButton>
            <ListItemButton component={Link} to="settings">
              <ListItemText primary={locale.layout.sideBar.preferences} />
            </ListItemButton>
          </List>

          {/* Lessons List */}
          <LessonList lessons={lessons} tafID={tafID} />

          {/* Sidebar Actions (pushed to the bottom) */}
          <SidebarActions
            tafID={tafID}
            resultPlanning={resultPlanning}
            handleGenerateCalendar={handleGenerateCalendar}
            generatingCalendar={state.generatingCalendar}
          />
        </Box>
      </Drawer>

      {/* Main Content */}
      <Box component="main" sx={styles.mainContent}>
        <Outlet context={{ taf }} />
      </Box>
    </Box>
  );
};

export default SideBar;
