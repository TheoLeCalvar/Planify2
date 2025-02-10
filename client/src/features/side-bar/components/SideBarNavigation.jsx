import React from "react";
import { List, ListItemButton, ListItemText } from "@mui/material";
import { Link } from "react-router-dom";
import locale from "@/config/locale.json";

// Styles
import styles from "./SideBarNavigation.styles";

const SidebarNavigation = () => {
  return (
    <List>
      <ListItemButton sx={styles.item}>
        <ListItemText primary={locale.layout.sideBar.status} />
      </ListItemButton>
      <ListItemButton component={Link} to="calendar" sx={styles.item}>
        <ListItemText primary={locale.layout.sideBar.calendar} />
      </ListItemButton>
      <ListItemButton component={Link} to="settings" sx={styles.item}>
        <ListItemText primary={locale.layout.sideBar.preferences} />
      </ListItemButton>
    </List>
  );
};

export default SidebarNavigation;
