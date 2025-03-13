// React Imports
import React, { useContext } from "react";
import { Outlet, useLoaderData } from "react-router-dom";

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
import { useTAFSelection } from "../hooks/useTAFSelection";
import styles from "./AppBarComponent.styles";
import { ProfileContext } from "@/hooks/ProfileContext";

const AppBarComponent = () => {
  // Retrieve TAF data from the loader
  const tafs = useLoaderData();

  // Global store function to toggle the side drawer
  const toggleDrawer = useStore((state) => state.toggleSideBar);

  // Use the custom hook for managing TAF selection
  const { selectedTAF, onTAFChange } = useTAFSelection();

  const { profile } = useContext(ProfileContext);

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

  const profileTitle = getProfileTitle(profile);

  return (
    <>
      <MuiAppBar sx={styles.appbar}>
        <Toolbar sx={styles.toolbar}>
          {selectedTAF && (
            <IconButton onClick={toggleDrawer} color="inherit">
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6">{app.applicationName}</Typography>
          <Typography variant="body1" ml={2}>
            {" "}
            ● {profileTitle}
          </Typography>
          <TAFSelector
            selectedOption={selectedTAF}
            onChange={onTAFChange}
            tafs={tafs}
          />
          <NotificationMenu />
          <ProfileMenu />
        </Toolbar>
      </MuiAppBar>
      <Box sx={styles.mainContent}>
        <Outlet />
      </Box>
    </>
  );
};

export default AppBarComponent;
