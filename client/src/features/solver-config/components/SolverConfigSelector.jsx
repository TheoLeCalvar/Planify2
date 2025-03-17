// React imports
import React, { useEffect, useState } from "react";
import {
  Link,
  Outlet,
  useLoaderData,
  useNavigate,
  useOutletContext,
  useParams,
} from "react-router-dom";
import PropTypes from "prop-types";

// Material-UI imports
import {
  Box,
  IconButton,
  Typography,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
} from "@mui/material";
import { AddCircle } from "@mui/icons-material";

// Local imports
import styles from "./SolverConfigSelector.styles";

/**
 * ConfigSelect Component
 * This component renders a dropdown menu for selecting a solver configuration.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.options - The list of configuration options.
 * @param {string|number} props.selectedConfig - The currently selected configuration ID.
 * @param {Function} props.handleChange - Callback for handling configuration selection changes.
 * @returns {JSX.Element} - The rendered ConfigSelect component.
 */
const ConfigSelect = ({ options, selectedConfig, handleChange }) => (
  <FormControl sx={styles.configSelect}>
    <InputLabel id="select-label-configs">Choisir une configuration</InputLabel>
    <Select
      labelId="select-label-configs"
      value={selectedConfig}
      onChange={handleChange}
      label="Choisir une configuration"
    >
      {/* Render each configuration option */}
      {options.map((option) => (
        <MenuItem key={option.id} value={option.id}>
          {option.name}
        </MenuItem>
      ))}

      {/* Display a disabled message if no configurations are available */}
      {options.length === 0 && (
        <MenuItem disabled>Aucune configuration</MenuItem>
      )}
    </Select>
  </FormControl>
);

ConfigSelect.propTypes = {
  /**
   * The list of configuration options.
   * Each option should have an `id` and a `name`.
   */
  options: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      name: PropTypes.string.isRequired,
    }),
  ).isRequired,

  /**
   * The currently selected configuration ID.
   */
  selectedConfig: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
    .isRequired,

  /**
   * Callback for handling configuration selection changes.
   */
  handleChange: PropTypes.func.isRequired,
};

/**
 * SolverConfigSelectorComponent
 * This component renders a selector for solver configurations.
 * It allows users to select an existing configuration or create a new one.
 * If no configuration is selected on load, it auto-selects the first available configuration.
 *
 * @returns {JSX.Element} - The rendered SolverConfigSelectorComponent.
 */
const SolverConfigSelectorComponent = () => {
  const options = useLoaderData(); // Load the list of configuration options
  const { idConfig } = useParams(); // Get the currently selected configuration ID from the route
  const navigate = useNavigate(); // Hook for navigating between routes
  const context = useOutletContext(); // Get additional context from the parent route

  const [selectedConfig, setSelectedConfig] = useState(idConfig ?? ""); // State for the selected configuration

  /**
   * Handles changes to the selected configuration.
   * Updates the state and navigates to the selected configuration's route.
   *
   * @param {Object} event - The change event from the dropdown.
   */
  const handleSelectChange = (event) => {
    const newConfigId = event.target.value;
    setSelectedConfig(newConfigId);
    if (newConfigId) {
      navigate(`./${newConfigId}`);
    }
  };

  /**
   * Auto-selects the first available configuration on load if none is selected.
   */
  useEffect(() => {
    if (options.length > 0 && !selectedConfig) {
      const defaultConfigId = options[0].id;
      setSelectedConfig(defaultConfigId);
      navigate(`./${defaultConfigId}`);
    }
  }, [options, selectedConfig, navigate]);

  return (
    <>
      <Box sx={styles.solverConfigSelector}>
        {/* Page title */}
        <Typography variant="h5" gutterBottom>
          Configuration actuelle :
        </Typography>

        {/* Dropdown for selecting a configuration */}
        <ConfigSelect
          options={options}
          selectedConfig={selectedConfig}
          handleChange={handleSelectChange}
        />

        {/* Button to create a new configuration */}
        <Link to="./new">
          <IconButton>
            <AddCircle color="secondary" />
          </IconButton>
        </Link>
      </Box>

      {/* Render child routes with the parent context */}
      <Outlet context={context} />
    </>
  );
};

export default SolverConfigSelectorComponent;
