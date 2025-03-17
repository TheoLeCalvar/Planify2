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
import LessonListLecturer from "./SideBarLecturer";

// Styles
import styles from "./SideBarComponent.styles";

// Context
import { ProfileContext } from "@/hooks/ProfileContext";

/**
 * SideBar Component
 * This component renders a persistent sidebar with navigation, lesson lists, and action buttons.
 * The content of the sidebar changes based on the user's profile (e.g., lecturer or admin).
 *
 * @returns {JSX.Element} - The rendered SideBar component.
 */
const SideBar = () => {
  // Get the sidebar open state from the global store
  const isOpen = useStore((state) => state.sideBarOpen);

  // Get the user's profile from the ProfileContext
  const { profile } = useContext(ProfileContext);

  // Get the TAF object and lessons from the outlet context
  const { taf, lessons } = useOutletContext();

  return (
    <Box sx={{ display: "flex" }}>
      {/* Ensures consistent baseline styles */}
      <CssBaseline />

      {/* Persistent sidebar drawer */}
      <Drawer
        variant="persistent"
        anchor="left"
        open={isOpen}
        sx={styles.drawer}
      >
        <Box sx={styles.sidebarContainer} role="presentation">
          {/* Render different content based on the user's profile */}
          {profile === "lecturer" ? (
            // If the user is a lecturer, display the lecturer-specific lesson list
            <LessonListLecturer lessons={lessons} />
          ) : (
            // If the user is an admin or other profile, display the full sidebar
            <>
              {/* Sidebar navigation links */}
              <SidebarNavigation />

              {/* List of lessons (UEs) */}
              <LessonList lessons={taf.UE} tafID={taf.id} />

              {/* Sidebar action buttons */}
              <SidebarActions
                tafID={taf.id}
                resultPlanning={taf.resultPlanning}
              />
            </>
          )}
        </Box>
      </Drawer>

      {/* Main content area */}
      <Box component="main" sx={styles.mainContent}>
        {/* Render the child routes with the TAF context */}
        <Outlet context={{ taf }} />
      </Box>
    </Box>
  );
};

export default SideBar;
