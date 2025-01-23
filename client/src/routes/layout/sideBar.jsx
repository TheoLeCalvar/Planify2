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
    Stack,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { Link, Outlet, useOutletContext } from "react-router-dom";
import locale from "../../config/locale.json";
import useStore from "../../hooks/store";
import axiosInstance from "../../services/axiosConfig";

const drawerWidth = 250; // Width of the sidebar

const SideBar = () => {
    const isOpen = useStore((state) => state.sideBarOpen);

    const context = useOutletContext();

    const { UE: lessons, id: tafID, resultPlanning } = useOutletContext().taf;

    const handleGenerateCalendar = async () => {
        const response = await axiosInstance.get(`/solver/run/${tafID}`);
        return response.data;
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
                    <ListItemButton
                        sx={{ flex: "initial" }}
                        LinkComponent={Link}
                        to="calendar"
                    >
                        <ListItemText
                            primary={locale.layout.sideBar.calendar}
                        />
                    </ListItemButton>
                    <ListItemButton
                        sx={{ flex: "initial" }}
                        LinkComponent={Link}
                        to="settings"
                    >
                        <ListItemText
                            primary={locale.layout.sideBar.preferences}
                        />
                    </ListItemButton>
                    {/* Scrollable Lessons List - Takes available space */}
                    <Typography variant="h6" sx={{ mt: 2, px: 2 }}>
                        {locale.layout.sideBar.UE}
                    </Typography>
                    <Box sx={{ flexGrow: 1, overflowY: "auto", px: 2 }}>
                        <List>
                            {lessons.map((lesson) => (
                                <Link
                                    to={`/taf/${tafID}/ue/${lesson.id}`}
                                    key={lesson.id}
                                >
                                    <ListItem
                                        sx={{
                                            display: "flex",
                                            flexDirection: "column",
                                        }}
                                    >
                                        <Typography variant="body1">
                                            {lesson.name}
                                        </Typography>
                                        <Typography
                                            variant="body2"
                                            color="textSecondary"
                                        >
                                            {locale.layout.sideBar.UEManager}:{" "}
                                            {lesson.responsible}
                                        </Typography>
                                    </ListItem>
                                </Link>
                            ))}
                        </List>
                    </Box>

                    {/* Add Button */}
                    <Box sx={{ p: 2 }}>
                        <Stack spacing={2}>
                            {resultPlanning?.length > 0 && (
                                <Link to={`/taf/${tafID}/results`}>
                                    <Button
                                        variant="outlined"
                                        sx={{ width: "100%" }}
                                    >
                                        Voir les calendriers
                                    </Button>
                                </Link>
                            )}
                            <Button
                                onClick={handleGenerateCalendar}
                                variant="outlined"
                                sx={{ width: "100%" }}
                            >
                                Générer le calendrier
                            </Button>
                            <Button
                                variant="contained"
                                startIcon={<AddIcon />}
                                sx={{ width: "100%" }}
                            >
                                {locale.layout.sideBar.addUE}
                            </Button>
                        </Stack>
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
                <Outlet context={context} />
            </Box>
        </Box>
    );
};

export default SideBar;
