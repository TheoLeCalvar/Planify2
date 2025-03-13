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

const AppBarComponent = () => {
  // Retrieve TAF data from the loader
  const tafs = useLoaderData();
  const params = useParams();
  const isTaf = !!params.idTAF;

  // Global store function to toggle the side drawer
  const toggleDrawer = useStore((state) => state.toggleSideBar);

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
          {isTaf && (
            <IconButton onClick={toggleDrawer} color="inherit">
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6">{app.applicationName}</Typography>
          <Typography variant="body1" ml={2}>
            {" "}
            ● {profileTitle}
          </Typography>
          <TAFSelector tafs={tafs} />
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
