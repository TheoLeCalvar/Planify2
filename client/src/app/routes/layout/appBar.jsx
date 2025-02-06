// React imports
import React, { useState, useCallback } from "react";
import { Outlet, useLoaderData } from "react-router-dom";
import { useLocation, useNavigate } from "react-router";

// Material-UI imports
import { AppBar, Toolbar, Typography, IconButton } from "@mui/material";
import { Menu as MenuIcon } from "@mui/icons-material";

// Data imports
import useStore from "../../../store/store";
import axiosInstance from "../../../config/axiosConfig";
import { USE_MOCK_DATA } from "../../../constants";
import { app } from "../../../config/locale.json";

// Custom Components imports
import ProfileMenu from "../../../features/app-bar/components/profileMenu";
import TAFSelector from "../../../features/app-bar/components/tafSelector";
import NotificationMenu from "../../../features/app-bar/components/notificationsMenu";

const styles = {
  toolbar: {
    justifyContent: "space-between",
  },
};

// Data loader function
export async function loader() {
  if (USE_MOCK_DATA) {
    return [
      {
        id: 1,
        name: "TAF DCL",
        description: "Développement Collaboratif de Logiciel",
      },
      {
        id: 2,
        name: "TAF EDP",
        description: "Environnement de Développement de Projet",
      },
      {
        id: 3,
        name: "TAF GPE",
        description: "Gestion de Projet et Entrepreneuriat",
      },
    ];
  }
  const response = await axiosInstance.get("/taf");
  return response.data;
}

// Main AppBar Component
const AppBarComponent = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const tafs = useLoaderData();
  const toggleDrawer = useStore((state) => state.toggleSideBar);
  const tafId = location.pathname.match(/\/taf\/(\d+)/)?.[1];

  const [selectedOption, setSelectedOption] = useState(tafId || "");

  const handleMenuSelect = useCallback(
    (event) => {
      navigate(`/taf/${event.target.value}`);
      setSelectedOption(event.target.value);
    },
    [navigate],
  );

  return (
    <>
      <AppBar position="static">
        <Toolbar sx={styles.toolbar}>
          {tafId && (
            <IconButton onClick={toggleDrawer} color="inherit">
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6">{app.applicationName}</Typography>
          <TAFSelector
            selectedOption={selectedOption}
            onChange={handleMenuSelect}
            tafs={tafs}
          />
          <NotificationMenu />
          <ProfileMenu />
        </Toolbar>
      </AppBar>
      <Outlet />
    </>
  );
};

export default AppBarComponent;
