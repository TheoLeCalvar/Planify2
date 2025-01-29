// src/components/AppBarComponent.js

import React, { useState, useReducer, useMemo } from "react";
import {
    AppBar,
    Toolbar,
    Typography,
    IconButton,
    Menu,
    MenuItem,
    Select,
    FormControl,
    InputLabel,
    Avatar,
    Box,
    Badge,
    Button
} from "@mui/material";
import {
    Menu as MenuIcon,
    Notifications as NotificationsIcon,
    AccountCircle as AccountCircleIcon,
    Settings as SettingsIcon,
    Brightness4 as Brightness4Icon,
    Logout as LogoutIcon
} from "@mui/icons-material";

import { Outlet, useLoaderData } from "react-router-dom";
import { useLocation, useNavigate } from "react-router";
import useStore from "../../hooks/store";
import axiosInstance from "../../services/axiosConfig";
import { USE_MOCK_DATA } from "../../contants";
import { app, layout } from "../../config/locale.json";

// Data loader function
export async function loader() {
    if (USE_MOCK_DATA) {
        return [
            { id: 1, name: "TAF DCL", description: "Développement Collaboratif de Logiciel" },
            { id: 2, name: "TAF EDP", description: "Environnement de Développement de Projet" },
            { id: 3, name: "TAF GPE", description: "Gestion de Projet et Entrepreneuriat" }
        ];
    }
    const response = await axiosInstance.get("/taf");
    return response.data;
}

// Reducer for managing component state
const initialState = {
    profileAnchorEl: null,
    notificationsAnchorEl: null,
    notifications: [
        { id: 1, title: "New Message", description: "You have a new message.", date: "2024-11-19" },
        { id: 2, title: "System Update", description: "System update scheduled.", date: "2024-11-18" }
    ]
};

function reducer(state, action) {
    switch (action.type) {
        case "OPEN_PROFILE_MENU":
            return { ...state, profileAnchorEl: action.payload };
        case "CLOSE_PROFILE_MENU":
            return { ...state, profileAnchorEl: null };
        case "OPEN_NOTIFICATIONS_MENU":
            return { ...state, notificationsAnchorEl: action.payload };
        case "CLOSE_NOTIFICATIONS_MENU":
            return { ...state, notificationsAnchorEl: null };
        case "DISMISS_NOTIFICATION":
            return { ...state, notifications: state.notifications.filter(n => n.id !== action.payload) };
        default:
            return state;
    }
}

// Profile Menu Component
const ProfileMenu = ({ anchorEl, handleClose }) => (
    <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleClose}>
        <MenuItem onClick={handleClose}><AccountCircleIcon sx={{ mr: 1 }} /> Profile</MenuItem>
        <MenuItem onClick={handleClose}><SettingsIcon sx={{ mr: 1 }} /> Settings</MenuItem>
        <MenuItem onClick={handleClose}><Brightness4Icon sx={{ mr: 1 }} /> Dark Mode</MenuItem>
        <MenuItem onClick={handleClose}><LogoutIcon sx={{ mr: 1 }} /> Logout</MenuItem>
    </Menu>
);

// Notifications Menu Component
const NotificationMenu = ({ anchorEl, notifications, handleClose, dismissNotification }) => (
    <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleClose}>
        {notifications.length > 0 ? (
            notifications.map(({ id, title, description, date }) => (
                <MenuItem key={id}>
                    <Box sx={{ display: "flex", flexDirection: "column", width: "100%" }}>
                        <Typography variant="subtitle1">{title}</Typography>
                        <Typography variant="body2" color="textSecondary">{description}</Typography>
                        <Typography variant="caption" color="textSecondary">{date}</Typography>
                        <Button size="small" color="primary" onClick={() => dismissNotification(id)} sx={{ mt: 1 }}>
                            Dismiss
                        </Button>
                    </Box>
                </MenuItem>
            ))
        ) : (
            <MenuItem>No new notifications</MenuItem>
        )}
    </Menu>
);

// TAF Selector Component
const TAFSelector = ({ selectedOption, onChange, tafs }) => (
    <Box sx={{ display: "flex", justifyContent: "center", flexGrow: 1 }}>
        <FormControl variant="standard" sx={{ minWidth: 300 }}>
            <InputLabel htmlFor="dropdown-selector">{layout.appBar.TAFSelector}</InputLabel>
            <Select id="dropdown-selector" value={selectedOption} onChange={onChange} label={layout.appBar.TAFSelector}>
                {tafs.map(({ id, name }) => (
                    <MenuItem key={id} value={id}>{name}</MenuItem>
                ))}
            </Select>
        </FormControl>
    </Box>
);

// Main AppBar Component
const AppBarComponent = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const tafs = useLoaderData();
    const toggleDrawer = useStore(state => state.toggleSideBar);
    const tafId = location.pathname.match(/\/taf\/(\d+)/)?.[1];

    const [selectedOption, setSelectedOption] = useState(tafId);
    const [state, dispatch] = useReducer(reducer, initialState);

    // Memoize handlers to prevent unnecessary renders
    const handleProfileClick = (event) => dispatch({ type: "OPEN_PROFILE_MENU", payload: event.currentTarget });
    const handleProfileClose = () => dispatch({ type: "CLOSE_PROFILE_MENU" });

    const handleNotificationsClick = (event) => dispatch({ type: "OPEN_NOTIFICATIONS_MENU", payload: event.currentTarget });
    const handleNotificationsClose = () => dispatch({ type: "CLOSE_NOTIFICATIONS_MENU" });

    const handleMenuSelect = (event) => {
        navigate(`/taf/${event.target.value}`);
        setSelectedOption(event.target.value);
    };

    const dismissNotification = (id) => dispatch({ type: "DISMISS_NOTIFICATION", payload: id });

    return (
        <>
            <AppBar position="static">
                <Toolbar sx={{ justifyContent: "space-between" }}>
                    {/* App Title and Drawer Button */}
                    {tafId && (
                        <IconButton onClick={toggleDrawer} color="inherit">
                            <MenuIcon />
                        </IconButton>
                    )}
                    <Typography variant="h6">{app.applicationName}</Typography>

                    {/* Centered Dropdown Selector */}
                    <TAFSelector selectedOption={selectedOption} onChange={handleMenuSelect} tafs={tafs} />

                    {/* Notifications Icon */}
                    <IconButton color="inherit" onClick={handleNotificationsClick}>
                        <Badge badgeContent={state.notifications.length} color="error">
                            <NotificationsIcon />
                        </Badge>
                    </IconButton>
                    <NotificationMenu
                        anchorEl={state.notificationsAnchorEl}
                        notifications={state.notifications}
                        handleClose={handleNotificationsClose}
                        dismissNotification={dismissNotification}
                    />

                    {/* Profile Avatar */}
                    <IconButton onClick={handleProfileClick} color="inherit">
                        <Avatar alt="User Profile" src="/static/images/avatar/1.jpg" />
                    </IconButton>
                    <ProfileMenu anchorEl={state.profileAnchorEl} handleClose={handleProfileClose} />
                </Toolbar>
            </AppBar>
            <Outlet />
        </>
    );
};

export default AppBarComponent;