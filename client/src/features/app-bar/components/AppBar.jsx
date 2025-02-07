// React Imports
import React from "react";
import { Outlet, useLoaderData } from "react-router-dom";

// Material-UI imports
import {
  AppBar as MuiAppBar,
  Toolbar,
  Typography,
  IconButton,
} from "@mui/material";
import { Menu as MenuIcon } from "@mui/icons-material";

// Global store and configuration imports
import useStore from "@/store/store";
import { app } from "@/config/locale.json";

// Local imports
import TAFSelector from "./tafSelector";
import NotificationMenu from "./notificationsMenu";
import ProfileMenu from "./profileMenu";
import { useTAFSelection } from "@/hooks/useTAFSelection";
import styles from "./AppBar.styles";

const AppBar = () => {
  // Retrieve TAF data from the loader (this could be mock or live)
  const tafs = useLoaderData();

  // Global store function to toggle the side drawer
  const toggleDrawer = useStore((state) => state.toggleSideBar);

  // Use the custom hook for managing TAF selection
  const { selectedTAF, onTAFChange } = useTAFSelection();

  return (
    <>
      <MuiAppBar position="static">
        <Toolbar sx={styles.toolbar}>
          {selectedTAF && (
            <IconButton onClick={toggleDrawer} color="inherit">
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6">{app.applicationName}</Typography>
          <TAFSelector
            selectedOption={selectedTAF}
            onChange={onTAFChange}
            tafs={tafs}
          />
          <NotificationMenu />
          <ProfileMenu />
        </Toolbar>
      </MuiAppBar>
      <Outlet />
    </>
  );
};

export default AppBar;
