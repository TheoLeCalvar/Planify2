// React imports
import React from "react";

// Material-UI imports
import { List, ListItemButton, ListItemText } from "@mui/material";

// React Router imports
import { Link } from "react-router-dom";

// Localization
import locale from "@/config/locale.json";

// Styles
import styles from "./SideBarNavigation.styles";

/**
 * SidebarNavigation Component
 * This component renders a list of navigation links in the sidebar.
 * Each link navigates to a specific section of the application, such as:
 * - Status overview
 * - Calendar view
 * - User preferences/settings
 *
 * @returns {JSX.Element} - The rendered SidebarNavigation component.
 */
const SidebarNavigation = () => {
  return (
    <List>
      {/* Status overview link */}
      <ListItemButton sx={styles.item}>
        <ListItemText primary={locale.layout.sideBar.status} />
      </ListItemButton>

      {/* Calendar view link */}
      <ListItemButton component={Link} to="calendar" sx={styles.item}>
        <ListItemText primary={locale.layout.sideBar.calendar} />
      </ListItemButton>

      {/* User preferences/settings link */}
      <ListItemButton component={Link} to="settings" sx={styles.item}>
        <ListItemText primary={locale.layout.sideBar.preferences} />
      </ListItemButton>
    </List>
  );
};

export default SidebarNavigation;
