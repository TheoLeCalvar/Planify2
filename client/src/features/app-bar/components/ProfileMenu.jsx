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
import { NestedMenuItem } from "mui-nested-menu";

// Local imports
import styles from "./ProfileMenu.styles";
import {
  profileMenuReducer,
  initialState,
} from "../stores/ProfileMenu.reducer";
import { AuthContext } from "@/hooks/AuthContext";
import { useNavigate } from "react-router-dom";
import { ArrowRightIcon } from "@mui/x-date-pickers";
import { ProfileContext } from "@/hooks/ProfileContext";

// Profile Menu Component
const ProfileMenu = () => {
  const [state, dispatch] = useReducer(profileMenuReducer, initialState);
  const { profileAnchorEl } = state;

  const { logout, getUser } = useContext(AuthContext);
  const { setProfile } = useContext(ProfileContext);
  const navigate = useNavigate();
  const user = getUser();

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

  const handleSelectProfile = (role) => {
    switch (role) {
      case "admin":
        setProfile("admin");
        break;
      case "taf-manager":
        setProfile("taf_manager");
        break;
      case "lecturer":
        setProfile("lecturer");
        break;
      default:
        break;
    }
    navigate("/");
    handleProfileClose();
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
        <NestedMenuItem
          label="Profil"
          leftIcon={<AccountCircleIcon />}
          rightIcon={<ArrowRightIcon />}
          parentMenuOpen={Boolean(profileAnchorEl)}
        >
          <MenuItem
            disabled={!user?.profiles?.admin}
            onClick={() => handleSelectProfile("admin")}
          >
            Administrateur
          </MenuItem>
          <MenuItem
            disabled={!user?.profiles?.taf_manager}
            onClick={() => handleSelectProfile("taf-manager")}
          >
            Responsable TAF
          </MenuItem>
          <MenuItem
            disabled={!user?.profiles?.lecturer}
            onClick={() => handleSelectProfile("lecturer")}
          >
            Intervenant
          </MenuItem>
        </NestedMenuItem>
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
