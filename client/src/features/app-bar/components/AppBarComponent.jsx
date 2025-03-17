// React Imports
import React, { useContext } from "react";
import { Outlet, useLoaderData, useParams } from "react-router-dom";

// Material-UI imports
import {
  AppBar as MuiAppBar,
  Toolbar,
  Typography,
  IconButton,
  Box,
} from "@mui/material";
import { Menu as MenuIcon } from "@mui/icons-material";

// Global store and configuration imports
import useStore from "@/store/store";
import { app } from "@/config/locale.json";

// Local imports
import TAFSelector from "./TafSelector";
import NotificationMenu from "./NotificationsMenu";
import ProfileMenu from "./ProfileMenu";
import styles from "./AppBarComponent.styles";
import { ProfileContext } from "@/hooks/ProfileContext";

/**
 * AppBarComponent
 * This component renders the application's top AppBar, which includes:
 * - A menu toggle button for the side drawer.
 * - The application name.
 * - The user's profile view title.
 * - A TAF selector for switching between TAFs.
 * - Notification and profile menus.
 *
 * @returns {JSX.Element} - The rendered AppBarComponent.
 */
const AppBarComponent = () => {
  // Retrieve TAF data from the loader
  const tafs = useLoaderData(); // List of TAFs loaded from the route loader
  const params = useParams(); // Access route parameters
  const isTaf = !!params.idTAF; // Check if the current route is within a TAF context

  // Global store function to toggle the side drawer
  const toggleDrawer = useStore((state) => state.toggleSideBar);

  // Retrieve the user's profile from the ProfileContext
  const { profile } = useContext(ProfileContext);

  /**
   * Returns the title corresponding to the user's profile.
   *
   * @param {string} profile - The user's profile (e.g., "admin", "taf_manager", "lecturer").
   * @returns {string} - The title for the user's profile view.
   */
  const getProfileTitle = (profile) => {
    switch (profile) {
      case "admin":
        return "Vue administrateur";
      case "taf_manager":
        return "Vue responsable de TAF";
      case "lecturer":
        return "Vue intervenant";
      default:
        return "";
    }
  };

  const profileTitle = getProfileTitle(profile); // Get the title for the current profile

  return (
    <>
      {/* Top AppBar */}
      <MuiAppBar sx={styles.appbar}>
        <Toolbar sx={styles.toolbar}>
          {/* Menu toggle button (visible only in TAF context) */}
          {isTaf && (
            <IconButton onClick={toggleDrawer} color="inherit">
              <MenuIcon />
            </IconButton>
          )}
          {/* Application name */}
          <Typography variant="h6">{app.applicationName}</Typography>
          {/* Profile view title */}
          <Typography variant="body1" ml={2}>
            ● {profileTitle}
          </Typography>
          {/* TAF selector dropdown */}
          <TAFSelector tafs={tafs} />
          {/* Notification menu */}
          <NotificationMenu />
          {/* Profile menu */}
          <ProfileMenu />
        </Toolbar>
      </MuiAppBar>

      {/* Main content area */}
      <Box sx={styles.mainContent}>
        <Outlet /> {/* Render nested routes */}
      </Box>
    </>
  );
};

export default AppBarComponent;
