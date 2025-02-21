// React imports
import React from "react";
import { Box, CssBaseline, Drawer } from "@mui/material";
import { Outlet, useOutletContext } from "react-router-dom";

// Global store
import useStore from "@/store/store";

// Custom components
import SidebarNavigation from "./SidebarNavigation";
import LessonList from "./LessonList";
import SidebarActions from "./SidebarActions";

// Styles
import styles from "./SideBarComponent.styles";

const SideBar = () => {
  const isOpen = useStore((state) => state.sideBarOpen);

  // Get the taf object from the outlet context
  const { taf } = useOutletContext();
  const { UE: lessons, id: tafID, resultPlanning } = taf;

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
          <SidebarNavigation />
          <LessonList lessons={lessons} tafID={tafID} />
          <SidebarActions tafID={tafID} resultPlanning={resultPlanning} />
        </Box>
      </Drawer>

      <Box component="main" sx={styles.mainContent}>
        <Outlet context={{ taf }} />
      </Box>
    </Box>
  );
};

export default SideBar;
