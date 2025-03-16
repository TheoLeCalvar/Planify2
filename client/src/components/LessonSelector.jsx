/* eslint-disable react/prop-types */

// React imports
import React, { useState, useEffect, Fragment } from "react";

// Material-UI imports
import {
  TextField,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  IconButton,
  CircularProgress,
  Breadcrumbs,
  Divider,
} from "@mui/material";

// Material-UI icons
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";

// Animation imports
import { motion, AnimatePresence } from "framer-motion";

// Axios instance for API requests
import axiosInstance from "@/config/axiosConfig";

// React Router hook
import { useOutletContext } from "react-router-dom";

/**
 * LessonSelector component.
 * This component provides a hierarchical selection interface for TAFs, UEs, blocks, and lessons.
 * It allows users to navigate through a breadcrumb-based structure and select a lesson.
 *
 * @param {Object} props - The component props.
 * @param {Function} props.onValidate - Callback function executed when a lesson is selected.
 *
 * @returns {JSX.Element} - The rendered LessonSelector component.
 */
const LessonSelector = ({ onValidate }) => {
  // State variables
  const [currentItems, setCurrentItems] = useState([]); // Current list of items to display
  const [breadcrumb, setBreadcrumb] = useState([]); // Breadcrumb navigation state
  const [searchTerm, setSearchTerm] = useState(""); // Search term for filtering items
  const [loading, setLoading] = useState(false); // Loading state for API requests

  const context = useOutletContext(); // Access context data from the parent route
  const tafId = context.taf.id; // Current TAF ID from the context

  /**
   * Fetches items based on the current breadcrumb level.
   *
   * @param {Array} parents - The breadcrumb trail representing the current hierarchy.
   * @returns {Promise<Array>} - A promise that resolves to the fetched items.
   */
  const fetchItems = async (parents) => {
    if (parents.length === 0) {
      // Fetch all TAFs except the current one
      return axiosInstance
        .get("/alltaf")
        .then((response) => response.data.filter((taf) => taf.id !== tafId));
    } else if (parents.length === 1) {
      // Fetch UEs for the selected TAF
      return axiosInstance
        .get(`/taf/${parents[0].id}`)
        .then((response) => response.data.UE);
    } else if (parents.length === 2) {
      // Fetch blocks for the selected UE
      return axiosInstance.get(`/ue/${parents[1].id}/lesson`).then((response) =>
        response.data.map((block) => ({
          id: block.id,
          name: block.title,
          empty: block.lessons.length === 0,
          ...block,
        })),
      );
    } else if (parents.length === 3) {
      // Fetch lessons for the selected block
      return currentItems
        .find((item) => item.id === parents[2].id)
        .lessons.map((lesson) => ({
          id: lesson.id,
          name: lesson.title,
        }));
    }
  };

  // Fetch initial items when the component mounts or breadcrumb changes
  useEffect(() => {
    setLoading(true);
    fetchItems(breadcrumb).then((items) => {
      setCurrentItems(items);
      setLoading(false);
    });
  }, [breadcrumb]);

  /**
   * Handles item click events.
   * Navigates to the next level or validates the selected lesson.
   *
   * @param {Object} item - The clicked item.
   */
  const handleItemClick = async (item) => {
    if (breadcrumb.length === 3) {
      // Validate the selected lesson
      onValidate({ ...item, taf: breadcrumb[0].name, ue: breadcrumb[1].name });
      return;
    }
    if (item.empty) return; // Prevent navigation if the item is empty
    setLoading(true);
    const newItems = await fetchItems([...breadcrumb, item]);
    setCurrentItems(newItems);
    setBreadcrumb([...breadcrumb, item]);
    setLoading(false);
  };

  /**
   * Handles the back button click.
   * Navigates to the previous breadcrumb level.
   */
  const handleBack = () => {
    if (breadcrumb.length === 0) return;
    navigateToBreadcrumb(breadcrumb.length - 2);
  };

  /**
   * Navigates to a specific breadcrumb level.
   *
   * @param {number} index - The index of the breadcrumb to navigate to.
   */
  const navigateToBreadcrumb = (index) => {
    if (breadcrumb.length === index + 1) return;
    const newBreadcrumb = breadcrumb.slice(0, index + 1);
    setBreadcrumb(newBreadcrumb);
    setLoading(true);
    fetchItems(newBreadcrumb).then((items) => {
      setCurrentItems(items);
      setLoading(false);
    });
  };

  // Filter items based on the search term
  const filteredItems =
    currentItems?.filter((item) =>
      item.name.toLowerCase().includes(searchTerm.toLowerCase()),
    ) || [];

  return (
    <div style={{ width: 350 }}>
      {/* Breadcrumb navigation */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: 8,
        }}
      >
        {breadcrumb.length > 0 && (
          <IconButton onClick={handleBack}>
            <ArrowBackIcon />
          </IconButton>
        )}
        <Breadcrumbs
          aria-label="breadcrumb"
          separator={<NavigateNextIcon fontSize="small" />}
        >
          {breadcrumb.length > 0 &&
            breadcrumb.map((b, index) => (
              <span
                key={index}
                style={{
                  cursor: "pointer",
                  textDecoration: "underline",
                  marginRight: 5,
                }}
                onClick={() => navigateToBreadcrumb(index)}
              >
                {b.name}
              </span>
            ))}
          {breadcrumb.length === 0 ? (
            <span>Sélection de la TAF</span>
          ) : breadcrumb.length === 1 ? (
            <span>Sélection de l&apos;UE</span>
          ) : breadcrumb.length === 2 ? (
            <span>Sélection du bloc</span>
          ) : (
            <span>Sélection du cours</span>
          )}
        </Breadcrumbs>
      </div>

      {/* Search input */}
      <TextField
        fullWidth
        size="small"
        variant="standard"
        placeholder="Rechercher..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        style={{ marginTop: 10, marginBottom: 10 }}
      />

      {/* Item list */}
      {loading ? (
        <CircularProgress style={{ display: "block", margin: "auto" }} />
      ) : (
        <AnimatePresence>
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
          >
            <List>
              {filteredItems.map((item) => (
                <Fragment key={item.id}>
                  <ListItem
                    disablePadding
                    secondaryAction={
                      breadcrumb.length < 3 ? (
                        <IconButton
                          edge="end"
                          aria-label="navigate"
                          onClick={() => handleItemClick(item)}
                        >
                          <ArrowForwardIosIcon />
                        </IconButton>
                      ) : null
                    }
                  >
                    <ListItemButton
                      onClick={() => handleItemClick(item)}
                      disabled={item.empty}
                    >
                      <ListItemText primary={item.name} />
                    </ListItemButton>
                  </ListItem>
                  <Divider />
                </Fragment>
              ))}
              {filteredItems.length === 0 && (
                <ListItem>
                  <ListItemText primary="Aucun élément..." />
                </ListItem>
              )}
            </List>
          </motion.div>
        </AnimatePresence>
      )}
    </div>
  );
};

export default LessonSelector;
