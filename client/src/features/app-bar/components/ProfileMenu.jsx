// React imports
import React, { useContext, useEffect } from "react";
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
import { useNavigate, useRevalidator } from "react-router-dom";
import { ArrowRightIcon } from "@mui/x-date-pickers";
import { ProfileContext } from "@/hooks/ProfileContext";

/**
 * ProfileMenu Component
 * This component renders a profile menu in the AppBar.
 * It allows users to:
 * - Switch between profiles (e.g., admin, taf-manager, lecturer).
 * - Access settings (not implemented).
 * - Toggle dark mode (not implemented).
 * - Log out of the application.
 *
 * @returns {JSX.Element} - The rendered ProfileMenu component.
 */
const ProfileMenu = () => {
  // Use a reducer to manage the state of the profile menu
  const [state, dispatch] = useReducer(profileMenuReducer, initialState);
  const { profileAnchorEl } = state; // Destructure the anchor element for the menu

  // Retrieve authentication and profile context
  const { logout, getUser } = useContext(AuthContext);
  const { profile, setProfile } = useContext(ProfileContext);

  // React Router hooks for navigation and revalidation
  const navigate = useNavigate();
  const revalidator = useRevalidator();

  // Retrieve the current user
  const user = getUser();

  // Revalidate the profile when it changes
  useEffect(() => {
    revalidator.revalidate();
  }, [profile]);

  /**
   * Opens the profile menu.
   *
   * @param {Object} event - The click event.
   */
  const handleProfileClick = (event) => {
    dispatch({ type: "OPEN_PROFILE", payload: event.currentTarget });
  };

  /**
   * Closes the profile menu.
   */
  const handleProfileClose = () => {
    dispatch({ type: "CLOSE_PROFILE" });
  };

  /**
   * Logs the user out and redirects to the login page.
   */
  const handleDisconnect = () => {
    logout();
    handleProfileClose();
    navigate("/login");
  };

  /**
   * Switches the user's profile and navigates to the home page.
   *
   * @param {string} role - The role to switch to (e.g., "admin", "taf-manager", "lecturer").
   */
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
      {/* Icon button to open the profile menu */}
      <IconButton onClick={handleProfileClick} color="inherit">
        <Avatar alt="User Profile" src="/static/images/avatar/1.jpg" />
      </IconButton>

      {/* Profile menu */}
      <Menu
        anchorEl={profileAnchorEl}
        open={Boolean(profileAnchorEl)}
        onClose={handleProfileClose}
      >
        {/* Nested menu for profile switching */}
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

        {/* Settings menu item (not implemented) */}
        <MenuItem onClick={handleProfileClose}>
          <SettingsIcon sx={styles.menuIcon} /> Paramètres (non implémenté)
        </MenuItem>

        {/* Dark mode toggle menu item (not implemented) */}
        <MenuItem onClick={handleProfileClose}>
          <Brightness4Icon sx={styles.menuIcon} /> Mode sombre (non implémenté)
        </MenuItem>

        {/* Logout menu item */}
        <MenuItem onClick={handleDisconnect}>
          <LogoutIcon sx={styles.menuIcon} /> Déconnexion
        </MenuItem>
      </Menu>
    </>
  );
};

export default ProfileMenu;
