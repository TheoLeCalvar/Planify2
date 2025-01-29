// src/components/SideBar.js

import React, { useCallback, useReducer, useMemo } from "react";
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
    Stack
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { Link, Outlet, useOutletContext } from "react-router-dom";
import locale from "../../config/locale.json";
import useStore from "../../hooks/store";
import axiosInstance from "../../services/axiosConfig";

const drawerWidth = 250; // Sidebar width

// Reducer to manage UI state
const initialState = {
    generatingCalendar: false,
};

function reducer(state, action) {
    switch (action.type) {
        case "START_GENERATION":
            return { ...state, generatingCalendar: true };
        case "END_GENERATION":
            return { ...state, generatingCalendar: false };
        default:
            return state;
    }
}

// Lesson List Component
const LessonList = ({ lessons, tafID }) => (
    <Box sx={{ flexGrow: 1, overflowY: "auto", px: 2 }}>
        <Typography variant="h6" sx={{ mt: 2, px: 2 }}>
            {locale.layout.sideBar.UE}
        </Typography>
        <List>
            {lessons.map(({ id, name, responsible }) => (
                <Link to={`/taf/${tafID}/ue/${id}`} key={id} style={{ textDecoration: "none", color: "inherit" }}>
                    <ListItem sx={{ display: "flex", flexDirection: "column" }}>
                        <Typography variant="body1">{name}</Typography>
                        <Typography variant="body2" color="textSecondary">
                            {locale.layout.sideBar.UEManager}: {responsible}
                        </Typography>
                    </ListItem>
                </Link>
            ))}
        </List>
    </Box>
);

// Sidebar Actions (Buttons)
const SidebarActions = ({ tafID, resultPlanning, handleGenerateCalendar, generatingCalendar }) => (
    <Box sx={{ p: 2 }}>
        <Stack spacing={2}>
            {resultPlanning?.length > 0 && (
                <Link to={`/taf/${tafID}/results`}>
                    <Button variant="outlined" sx={{ width: "100%" }}>
                        Voir les calendriers
                    </Button>
                </Link>
            )}
            <Button
                onClick={handleGenerateCalendar}
                variant="outlined"
                sx={{ width: "100%" }}
                disabled={generatingCalendar}
            >
                {generatingCalendar ? "Génération..." : "Générer le calendrier"}
            </Button>
            <Button variant="contained" startIcon={<AddIcon />} sx={{ width: "100%" }}>
                {locale.layout.sideBar.addUE}
            </Button>
        </Stack>
    </Box>
);

// Main Sidebar Component
const SideBar = () => {
    const isOpen = useStore((state) => state.sideBarOpen);

    const context = useOutletContext();
    const lessons = useMemo(() => context.taf.UE, [context.taf.UE]);
    const tafID = useMemo(() => context.taf.id, [context.taf.id]);
    const resultPlanning = useMemo(() => context.taf.resultPlanning, [context.taf.resultPlanning]);
    
    const [state, dispatch] = useReducer(reducer, initialState);

    const handleGenerateCalendar = useCallback(async () => {
        dispatch({ type: "START_GENERATION" });
        try {
            await axiosInstance.get(`/solver/run/${tafID}`);
        } finally {
            dispatch({ type: "END_GENERATION" });
        }
    }, [tafID]);

    return (
        <Box sx={{ display: "flex" }}>
            <CssBaseline />
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
                <Box sx={{ width: drawerWidth, display: "flex", flexDirection: "column", height: "100%" }} role="presentation">
                    
                    {/* Sidebar Navigation */}
                    <List>
                        <ListItemButton>
                            <ListItemText primary={locale.layout.sideBar.status} />
                        </ListItemButton>
                        <ListItemButton component={Link} to="calendar">
                            <ListItemText primary={locale.layout.sideBar.calendar} />
                        </ListItemButton>
                        <ListItemButton component={Link} to="settings">
                            <ListItemText primary={locale.layout.sideBar.preferences} />
                        </ListItemButton>
                    </List>

                    {/* Lessons List */}
                    <LessonList lessons={lessons} tafID={tafID} />

                    {/* Sidebar Actions */}
                    <SidebarActions
                        tafID={tafID}
                        resultPlanning={resultPlanning}
                        handleGenerateCalendar={handleGenerateCalendar}
                        generatingCalendar={state.generatingCalendar}
                    />
                </Box>
            </Drawer>

            {/* Main Content */}
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
                }}
            >
                <Outlet context={context} />
            </Box>
        </Box>
    );
};

export default SideBar;
