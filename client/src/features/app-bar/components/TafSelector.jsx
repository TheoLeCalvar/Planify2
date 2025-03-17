// React imports
import React, { useContext } from "react";
import PropTypes from "prop-types";

// Material-UI imports
import {
  Box,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
} from "@mui/material";

// Local imports
import layout from "@/config/locale.json"; // Localization configuration
import styles from "./TafSelector.styles"; // Custom styles for the TAF selector
import { AddCircle } from "@mui/icons-material"; // Icon for adding a new TAF
import { Link, useNavigate, useParams } from "react-router-dom"; // React Router utilities
import { ProfileContext } from "@/hooks/ProfileContext"; // Context for managing user profiles

/**
 * TAFSelector Component
 * This component renders a dropdown menu for selecting a TAF.
 * It allows users to:
 * - Switch between existing TAFs.
 * - Add a new TAF (if the user is not a lecturer).
 *
 * @param {Object} props - The component props.
 * @param {Array} props.tafs - The list of TAFs available for selection.
 *
 * @returns {JSX.Element} - The rendered TAFSelector component.
 */
const TAFSelector = ({ tafs }) => {
  // React Router hooks for navigation and accessing route parameters
  const navigate = useNavigate();
  const params = useParams();

  /**
   * Handles the change event when a TAF is selected from the dropdown.
   *
   * @param {Object} event - The change event.
   */
  const handleChange = (event) => {
    navigate(`/taf/${event.target.value}`); // Navigate to the selected TAF's page
  };

  // Retrieve the user's profile from the ProfileContext
  const { profile } = useContext(ProfileContext);

  return (
    <Box sx={styles.TAFSelectorBox}>
      {/* Dropdown menu for selecting a TAF */}
      <FormControl sx={styles.formControl}>
        <InputLabel id="taf-selector-label" sx={styles.label}>
          {layout.layout.appBar.TAFSelector}
        </InputLabel>
        <Select
          labelId="taf-selector-label"
          id="dropdown-selector"
          value={params?.idTAF || ""} // Set the current TAF based on the route parameter
          onChange={handleChange} // Handle TAF selection
          label={layout.layout.appBar.TAFSelector}
          sx={styles.select}
        >
          {/* Render a menu item for each TAF */}
          {tafs.map(({ id, name }) => (
            <MenuItem key={id} value={id}>
              {name}
            </MenuItem>
          ))}
          {/* Display a message if no TAFs are available */}
          {tafs.length === 0 && (
            <MenuItem disabled>Aucune TAF disponible</MenuItem>
          )}
        </Select>
      </FormControl>

      {/* Add new TAF button (visible only if the user is not a lecturer) */}
      {profile != "lecturer" && (
        <Link to="/taf/new">
          <IconButton>
            <AddCircle color="secondary" />
          </IconButton>
        </Link>
      )}
    </Box>
  );
};

// Define the prop types for the component
TAFSelector.propTypes = {
  tafs: PropTypes.array.isRequired, // Array of TAFs available for selection
};

export default TAFSelector;
