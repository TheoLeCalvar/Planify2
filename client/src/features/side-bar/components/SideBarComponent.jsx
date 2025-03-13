// React imports
import React, { useContext } from "react";
import { Box, CssBaseline, Drawer } from "@mui/material";
import { Outlet, useOutletContext } from "react-router-dom";

// Global store
import useStore from "@/store/store";

// Custom components
import SidebarNavigation from "./SideBarNavigation";
import LessonList from "./LessonList";
import SidebarActions from "./SideBarActions";

// Styles
import styles from "./SideBarComponent.styles";
import { ProfileContext } from "@/hooks/ProfileContext";
import LessonListLecturer from "./SideBarLecturer";

const SideBar = () => {
  const isOpen = useStore((state) => state.sideBarOpen);

  const { profile } = useContext(ProfileContext);

  // Get the taf object from the outlet context
  const { taf, lessons } = useOutletContext();

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
          {profile === "lecturer" ? (
            <LessonListLecturer lessons={lessons} />
          ) : (
            <>
              <SidebarNavigation />
              <LessonList lessons={taf.UE} tafID={taf.id} />
              <SidebarActions
                tafID={taf.id}
                resultPlanning={taf.resultPlanning}
              />
            </>
          )}
        </Box>
      </Drawer>

      <Box component="main" sx={styles.mainContent}>
        <Outlet context={{ taf }} />
      </Box>
    </Box>
  );
};

export default SideBar;
