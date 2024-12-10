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
    Button,
    CssBaseline,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { Link, Outlet, useOutletContext } from "react-router-dom";
import locale from "../../config/locale.json";
import useStore from "../../hooks/store";

const drawerWidth = 250; // Width of the sidebar

const SideBar = () => {
    const isOpen = useStore((state) => state.sideBarOpen)

    const context = useOutletContext()

    const { UE: courses, id: tafID}  = useOutletContext().taf

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
                    <ListItemButton sx={{ flex: "initial" }} LinkComponent={Link} to="calendar">
                        <ListItemText
                            primary={locale.layout.sideBar.calendar}
                        />
                    </ListItemButton>
                    <ListItemButton sx={{ flex: "initial" }}>
                        <ListItemText
                            primary={locale.layout.sideBar.preferences}
                        />
                    </ListItemButton>
                    {/* Scrollable Courses List - Takes available space */}
                    <Typography variant="h6" sx={{ mt: 2, px: 2 }}>
                        {locale.layout.sideBar.UE}
                    </Typography>
                    <Box sx={{ flexGrow: 1, overflowY: "auto", px: 2 }}>
                        <List>
                            {courses.map((course) => (
                                <Link to={`/taf/${tafID}/ue/${course.id}`} key={course.id}>
                                    <ListItem
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
                                </Link>
                            ))}
                        </List>
                    </Box>

                    {/* Add Button */}
                    <Box sx={{ p: 2 }}>
                        <Button
                            variant="contained"
                            startIcon={<AddIcon />}
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
                <Outlet context={context}/>
            </Box>
        </Box>
    );
};

export default SideBar;
