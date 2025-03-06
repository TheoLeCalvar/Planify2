// React imports
import React, { useContext } from "react";
import { useReducer } from "react";

// Material-UI imports
import { Menu, MenuItem, IconButton, Avatar } from "@mui/material";
import {
  AccountCircle as AccountCircleIcon,
  Settings as SettingsIcon,
  Brightness4 as Brightness4Icon,
  Logout as LogoutIcon,
} from "@mui/icons-material";

// Local imports
import styles from "./ProfileMenu.styles";
import {
  profileMenuReducer,
  initialState,
} from "../stores/ProfileMenu.reducer";
import { AuthContext } from "@/hooks/AuthContext";
import { useNavigate } from "react-router-dom";

// Profile Menu Component
const ProfileMenu = () => {
  const [state, dispatch] = useReducer(profileMenuReducer, initialState);
  const { profileAnchorEl } = state;

  const { logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleProfileClick = (event) => {
    dispatch({ type: "OPEN_PROFILE", payload: event.currentTarget });
  };
  const handleProfileClose = () => {
    dispatch({ type: "CLOSE_PROFILE" });
  };

  const handleDisconnect = () => {
    logout();
    handleProfileClose();
    navigate("/login");
  };

  return (
    <>
      <IconButton onClick={handleProfileClick} color="inherit">
        <Avatar alt="User Profile" src="/static/images/avatar/1.jpg" />
      </IconButton>
      <Menu
        anchorEl={profileAnchorEl}
        open={Boolean(profileAnchorEl)}
        onClose={handleProfileClose}
      >
        <MenuItem onClick={handleProfileClose}>
          <AccountCircleIcon sx={styles.menuIcon} /> Profile
        </MenuItem>
        <MenuItem onClick={handleProfileClose}>
          <SettingsIcon sx={styles.menuIcon} /> Paramètres (non implémenté)
        </MenuItem>
        <MenuItem onClick={handleProfileClose}>
          <Brightness4Icon sx={styles.menuIcon} /> Mode sombre (non implémenté)
        </MenuItem>
        <MenuItem onClick={handleDisconnect}>
          <LogoutIcon sx={styles.menuIcon} /> Déconnexion
        </MenuItem>
      </Menu>
    </>
  );
};

export default ProfileMenu;
