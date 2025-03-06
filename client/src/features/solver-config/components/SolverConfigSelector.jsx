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

const ConfigSelect = ({ options, selectedConfig, handleChange }) => (
  <FormControl sx={styles.configSelect}>
    <InputLabel id="select-label-configs">Choisir une configuration</InputLabel>
    <Select
      labelId="select-label-configs"
      value={selectedConfig}
      onChange={handleChange}
      label="Choisir une configuration"
    >
      {options.map((option) => (
        <MenuItem key={option.id} value={option.id}>
          {option.name}
        </MenuItem>
      ))}
      {options.length === 0 && (
        <MenuItem disabled>Aucune configuration</MenuItem>
      )}
    </Select>
  </FormControl>
);

ConfigSelect.propTypes = {
  options: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      name: PropTypes.string.isRequired,
    }),
  ).isRequired,
  selectedConfig: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
    .isRequired,
  handleChange: PropTypes.func.isRequired,
};

const SolverConfigSelectorComponent = () => {
  const options = useLoaderData();
  const { idConfig } = useParams();
  const navigate = useNavigate();
  const context = useOutletContext();

  const [selectedConfig, setSelectedConfig] = useState(idConfig ?? "");

  const handleSelectChange = (event) => {
    const newConfigId = event.target.value;
    setSelectedConfig(newConfigId);
    if (newConfigId) {
      navigate(`./${newConfigId}`);
    }
  };

  // Auto-redirect on load if no configuration is selected
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
        <Typography variant="h5" gutterBottom>
          Configuration actuelle :
        </Typography>

        <ConfigSelect
          options={options}
          selectedConfig={selectedConfig}
          handleChange={handleSelectChange}
        />

        <Link to="./new">
          <IconButton>
            <AddCircle color="secondary" />
          </IconButton>
        </Link>
      </Box>
      <Outlet context={context} />
    </>
  );
};

export default SolverConfigSelectorComponent;
