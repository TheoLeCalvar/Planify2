// React imports
import React from "react";
import { useReducer } from "react";

// Material-UI imports
import {
  Box,
  Button,
  Menu,
  MenuItem,
  Typography,
  IconButton,
  Badge,
} from "@mui/material";
import { Notifications as NotificationsIcon } from "@mui/icons-material";

// Local imports
import styles from "./NotificationsMenu.styles";
import {
  notificationsMenuReducer,
  initialState,
} from "../stores/NotificationsMenu.reducer";

/**
 * NotificationMenu
 * This component renders a notification menu in the AppBar.
 * It displays a badge with the number of unread notifications and allows users to view and dismiss notifications.
 *
 * @returns {JSX.Element} - The rendered NotificationMenu component.
 */
const NotificationMenu = () => {
  // Use a reducer to manage the state of the notifications menu
  const [state, dispatch] = useReducer(notificationsMenuReducer, initialState);
  const { notificationsAnchorEl, notifications } = state; // Destructure state variables

  /**
   * Handles the click event to open the notifications menu.
   *
   * @param {Object} event - The click event.
   */
  const handleNotificationClick = (event) => {
    dispatch({ type: "OPEN_NOTIFICATIONS", payload: event.currentTarget }); // Dispatch action to open the menu
  };

  /**
   * Handles the event to close the notifications menu.
   */
  const handleNotificationClose = () => {
    dispatch({ type: "CLOSE_NOTIFICATIONS" }); // Dispatch action to close the menu
  };

  /**
   * Dismisses a specific notification by its ID.
   *
   * @param {string} id - The ID of the notification to dismiss.
   */
  const dismissNotification = (id) => {
    dispatch({ type: "DISMISS_NOTIFICATION", payload: id }); // Dispatch action to dismiss the notification
  };

  return (
    <>
      {/* Icon button to open the notifications menu */}
      <IconButton color="inherit" onClick={handleNotificationClick}>
        <Badge badgeContent={notifications.length} color="error">
          <NotificationsIcon />
        </Badge>
      </IconButton>

      {/* Notifications menu */}
      <Menu
        anchorEl={notificationsAnchorEl}
        open={Boolean(notificationsAnchorEl)}
        onClose={handleNotificationClose}
      >
        {/* Render notifications if available */}
        {notifications.length > 0 ? (
          notifications.map(({ id, title, description, date }) => (
            <MenuItem key={id}>
              <Box sx={styles.notificationBox}>
                <Typography variant="subtitle1">{title}</Typography>
                <Typography variant="body2" color="textSecondary">
                  {description}
                </Typography>
                <Typography variant="caption" color="textSecondary">
                  {date}
                </Typography>
                <Button
                  size="small"
                  color="primary"
                  onClick={() => dismissNotification(id)}
                  sx={styles.dismissButton}
                >
                  Dismiss
                </Button>
              </Box>
            </MenuItem>
          ))
        ) : (
          // Display a message if there are no notifications
          <MenuItem>No new notifications</MenuItem>
        )}
      </Menu>
    </>
  );
};

export default NotificationMenu;
