// src/components/SideBar.js

import React, { useState } from "react";
import {
    Drawer,
    Box,
    List,
    ListItem,
    ListItemText,
    ListItemButton,
    Typography,
    IconButton,
    Button,
    CssBaseline,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import AddIcon from "@mui/icons-material/Add";
import { Outlet, useOutletContext } from "react-router-dom";

import locale from "../../config/locale.json";

const drawerWidth = 250; // Width of the sidebar

const SideBar = () => {
    const [isOpen, setIsOpen] = useOutletContext(); // Sidebar is open by default
    const [courses, setCourses] = useState([
        { id: 1, name: "UE A", responsible: "Théo Le Calvar" },
        { id: 2, name: "UE B", responsible: "Jacques Noyé" },
        { id: 3, name: "UE C", responsible: "Gilles Simonin" },
    ]);

    const toggleDrawer = () => {
        setIsOpen(!isOpen);
    };

    const handleAddCourse = () => {
        const newCourse = {
            id: courses.length + 1,
            name: `UE ${courses.length + 1}`,
            responsible: "NA",
        };
        setCourses([...courses, newCourse]);
    };

    return (
        <Box sx={{ display: "flex" }}>
            <CssBaseline />

            {/* Sidebar Drawer */}
            <Drawer
                variant="persistent"
                anchor="left"
                open={isOpen}
                sx={{
                    width: isOpen ? drawerWidth : 0,
                    flexShrink: 0,
                    "& .MuiDrawer-paper": {
                        width: drawerWidth + 1,
                        boxSizing: "border-box",
                        position: "relative",
                    },
                }}
            >
                <Box
                    sx={{
                        width: drawerWidth,
                        display: "flex",
                        flexDirection: "column",
                        height: "100%",
                    }}
                    role="presentation"
                >
                    {/* Sidebar Buttons */}
                    <ListItemButton sx={{ flex: "initial" }}>
                        <ListItemText primary={locale.layout.sideBar.status} />
                    </ListItemButton>
                    <ListItemButton sx={{ flex: "initial" }}>
                        <ListItemText
                            primary={locale.layout.sideBar.preferences}
                        />
                    </ListItemButton>
                    <ListItemButton sx={{ flex: "initial" }}>
                        <ListItemText
                            primary={locale.layout.sideBar.lessons}
                        />
                    </ListItemButton>
                    {/* Scrollable Courses List - Takes available space */}
                    <Typography variant="h6" sx={{ mt: 2, px: 2 }}>
                        {locale.layout.sideBar.UE}
                    </Typography>
                    <Box sx={{ flexGrow: 1, overflowY: "auto", px: 2 }}>
                        <List>
                            {courses.map((course) => (
                                <ListItem
                                    key={course.id}
                                    sx={{
                                        display: "flex",
                                        flexDirection: "column",
                                    }}
                                >
                                    <Typography variant="body1">
                                        {course.name}
                                    </Typography>
                                    <Typography
                                        variant="body2"
                                        color="textSecondary"
                                    >
                                        {locale.layout.sideBar.UEManager}: {course.responsible}
                                    </Typography>
                                </ListItem>
                            ))}
                        </List>
                    </Box>

                    {/* Add Button */}
                    <Box sx={{ p: 2 }}>
                        <Button
                            variant="contained"
                            startIcon={<AddIcon />}
                            onClick={handleAddCourse}
                            sx={{ width: "100%" }}
                        >
                            {locale.layout.sideBar.addUE}
                        </Button>
                    </Box>
                </Box>
            </Drawer>

            {/* Main Content with Outlet */}
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    transition: (theme) =>
                        theme.transitions.create(["margin", "width"], {
                            easing: theme.transitions.easing.sharp,
                            duration: theme.transitions.duration.leavingScreen,
                        }),
                    //marginLeft: isOpen ? `${drawerWidth}px` : 0,
                }}
            >
                <Outlet />
            </Box>
        </Box>
    );
};

export default SideBar;
