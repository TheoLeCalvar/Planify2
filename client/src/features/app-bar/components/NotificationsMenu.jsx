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
} from "../stores/notificationsMenu.reducer";

const NotificationMenu = () => {
  const [state, dispatch] = useReducer(notificationsMenuReducer, initialState);
  const { notificationsAnchorEl, notifications } = state;

  const handleNotificationClick = (event) => {
    dispatch({ type: "OPEN_NOTIFICATIONS", payload: event.currentTarget });
  };
  const handleNotificationClose = () => {
    dispatch({ type: "CLOSE_NOTIFICATIONS" });
  };
  const dismissNotification = (id) => {
    dispatch({ type: "DISMISS_NOTIFICATION", payload: id });
  };

  return (
    <>
      <IconButton color="inherit" onClick={handleNotificationClick}>
        <Badge badgeContent={notifications.length} color="error">
          <NotificationsIcon />
        </Badge>
      </IconButton>
      <Menu
        anchorEl={notificationsAnchorEl}
        open={Boolean(notificationsAnchorEl)}
        onClose={handleNotificationClose}
      >
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
          <MenuItem>No new notifications</MenuItem>
        )}
      </Menu>
    </>
  );
};

export default NotificationMenu;
