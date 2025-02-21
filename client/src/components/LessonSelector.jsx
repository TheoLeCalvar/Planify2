/* eslint-disable react/prop-types */
import React, { useState, useEffect, Fragment } from "react";
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
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import { motion, AnimatePresence } from "framer-motion";
import axiosInstance from "@/config/axiosConfig";
import { useOutletContext } from "react-router-dom";

const LessonSelector = ({ onValidate }) => {
  const [currentItems, setCurrentItems] = useState([]);
  const [breadcrumb, setBreadcrumb] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);

  const context = useOutletContext();
  const tafId = context.taf.id;

  const fetchItems = async (parents) => {
    if (parents.length === 0) {
      return axiosInstance
        .get("/alltaf")
        .then((response) => response.data.filter((taf) => taf.id != tafId));
    } else if (parents.length == 1) {
      return axiosInstance
        .get(`/taf/${parents[0].id}`)
        .then((response) => response.data.UE);
    } else if (parents.length == 2) {
      return axiosInstance.get(`/ue/${parents[1].id}/lesson`).then((response) =>
        response.data.map((block) => ({
          id: block.id,
          name: block.title,
          empty: block.lessons.length === 0,
          ...block,
        })),
      );
    } else if (parents.length == 3) {
      return currentItems
        .find((item) => item.id === parents[2].id)
        .lessons.map((lesson) => ({
          id: lesson.id,
          name: lesson.title,
        }));
    }
  };

  useEffect(() => {
    setLoading(true);
    fetchItems(breadcrumb).then((items) => {
      setCurrentItems(items);
      setLoading(false);
    });
  }, []);

  const handleItemClick = async (item) => {
    if (breadcrumb.length === 3) {
      onValidate({ ...item, taf: breadcrumb[0].name, ue: breadcrumb[1].name });
      return;
    }
    if (item.empty) return;
    setLoading(true);
    const newItems = await fetchItems([...breadcrumb, item]);
    setCurrentItems(newItems);
    setBreadcrumb([...breadcrumb, item]);
    setLoading(false);
  };

  const handleBack = () => {
    if (breadcrumb.length === 0) return;
    navigateToBreadcrumb(breadcrumb.length - 2);
  };

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

  const filteredItems =
    currentItems?.filter((item) =>
      item.name.toLowerCase().includes(searchTerm.toLowerCase()),
    ) || [];

  return (
    <div style={{ width: 350 }}>
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
      <TextField
        fullWidth
        size="small"
        variant="standard"
        placeholder="Rechercher..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        style={{ marginTop: 10, marginBottom: 10 }}
      />
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
                          aria-label="delete"
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
                  <ListItemText primary="Aucun élement..." />
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
