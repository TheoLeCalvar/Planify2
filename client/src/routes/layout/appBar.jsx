// src/components/AppBarComponent.js

import React, { useState } from "react";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import Badge from "@mui/material/Badge";
import Button from "@mui/material/Button";
import MenuIcon from "@mui/icons-material/Menu";

// Import Material-UI icons
import NotificationsIcon from "@mui/icons-material/Notifications";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import SettingsIcon from "@mui/icons-material/Settings";
import Brightness4Icon from "@mui/icons-material/Brightness4";
import LogoutIcon from "@mui/icons-material/Logout";

import { Outlet } from "react-router-dom";
import { app, layout } from "../../config/locale.json";

const AppBarComponent = () => {
    const [selectedOption, setSelectedOption] = useState("");
    const [profileAnchorEl, setProfileAnchorEl] = useState(null);
    const [notificationsAnchorEl, setNotificationsAnchorEl] = useState(null);
    const [sideBarOpen, setSideBarOpen] = useState(true);
    const [notifications, setNotifications] = useState([
        {
            id: 1,
            title: "New Message",
            description: "You have received a new message from John Doe.",
            date: "2024-11-19",
        },
        {
            id: 2,
            title: "System Update",
            description: "System update is scheduled for tonight.",
            date: "2024-11-18",
        },
    ]);

    const toggleDrawer = () => {
        setSideBarOpen(!sideBarOpen);
    };

    const handleProfileClick = (event) => {
        setProfileAnchorEl(event.currentTarget);
    };

    const handleProfileClose = () => {
        setProfileAnchorEl(null);
    };

    const handleMenuSelect = (event) => {
        setSelectedOption(event.target.value);
    };

    const handleNotificationsClick = (event) => {
        setNotificationsAnchorEl(event.currentTarget);
    };

    const handleNotificationsClose = () => {
        setNotificationsAnchorEl(null);
    };

    const dismissNotification = (id) => {
        setNotifications(
            notifications.filter((notification) => notification.id !== id)
        );
    };

    return (
        <>
            <AppBar position="static">
                <Toolbar sx={{ justifyContent: "space-between" }}>
                    {/* App Title */}
                    <IconButton onClick={toggleDrawer} color="inherit">
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6">{app.applicationName}</Typography>

                    {/* Centered Dropdown Selector */}
                    <Box
                        sx={{
                            display: "flex",
                            justifyContent: "center",
                            flexGrow: 1,
                        }}
                    >
                        <FormControl variant="standard" sx={{ minWidth: 300 }}>
                            <InputLabel htmlFor="dropdown-selector">
                                {layout.appBar.TAFSelector}
                            </InputLabel>
                            <Select
                                id="dropdown-selector"
                                value={selectedOption}
                                onChange={handleMenuSelect}
                                label={layout.appBar.TAFSelector}
                                
                            >
                                <MenuItem value={1}>Option 1</MenuItem>
                                <MenuItem value={2}>Option 2</MenuItem>
                                <MenuItem value={3}>Option 3</MenuItem>
                            </Select>
                        </FormControl>
                    </Box>

                    {/* Notification Icon */}
                    <IconButton
                        color="inherit"
                        onClick={handleNotificationsClick}
                    >
                        <Badge
                            badgeContent={notifications.length}
                            color="error"
                        >
                            <NotificationsIcon />
                        </Badge>
                    </IconButton>
                    <Menu
                        anchorEl={notificationsAnchorEl}
                        open={Boolean(notificationsAnchorEl)}
                        onClose={handleNotificationsClose}
                    >
                        {notifications.length > 0 ? (
                            notifications.map((notification) => (
                                <MenuItem key={notification.id}>
                                    <Box
                                        sx={{
                                            display: "flex",
                                            flexDirection: "column",
                                            width: "100%",
                                        }}
                                    >
                                        <Typography variant="subtitle1">
                                            {notification.title}
                                        </Typography>
                                        <Typography
                                            variant="body2"
                                            color="textSecondary"
                                        >
                                            {notification.description}
                                        </Typography>
                                        <Typography
                                            variant="caption"
                                            color="textSecondary"
                                        >
                                            {notification.date}
                                        </Typography>
                                        <Button
                                            size="small"
                                            color="primary"
                                            onClick={() =>
                                                dismissNotification(
                                                    notification.id
                                                )
                                            }
                                            sx={{ mt: 1 }}
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

                    {/* Account Profile Picture with Dropdown Menu */}
                    <IconButton onClick={handleProfileClick} color="inherit">
                        <Avatar
                            alt="User Profile"
                            src="/static/images/avatar/1.jpg"
                        />
                    </IconButton>
                    <Menu
                        anchorEl={profileAnchorEl}
                        open={Boolean(profileAnchorEl)}
                        onClose={handleProfileClose}
                    >
                        <MenuItem onClick={handleProfileClose}>
                            <AccountCircleIcon sx={{ mr: 1 }} />
                            Profile
                        </MenuItem>
                        <MenuItem onClick={handleProfileClose}>
                            <SettingsIcon sx={{ mr: 1 }} />
                            Settings
                        </MenuItem>
                        <MenuItem onClick={handleProfileClose}>
                            <Brightness4Icon sx={{ mr: 1 }} />
                            Dark Mode
                        </MenuItem>
                        <MenuItem onClick={handleProfileClose}>
                            <LogoutIcon sx={{ mr: 1 }} />
                            Logout
                        </MenuItem>
                    </Menu>
                </Toolbar>
            </AppBar>
            <Outlet context={[sideBarOpen, setSideBarOpen]} />
        </>
    );
};

export default AppBarComponent;
