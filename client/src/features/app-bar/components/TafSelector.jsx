// React imports
import React from "react";
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
import layout from "@/config/locale.json";
import styles from "./TafSelector.styles";
import { AddCircle } from "@mui/icons-material";
import { Link } from "react-router-dom";

const TAFSelector = ({ selectedOption, onChange, tafs }) => {
  // Wrap the onChange to extract the value and then forward it.
  const handleChange = (event) => {
    onChange(event.target.value);
  };

  return (
    <Box sx={styles.TAFSelectorBox}>
      <FormControl sx={styles.formControl}>
        <InputLabel id="taf-selector-label" sx={styles.label}>
          {layout.layout.appBar.TAFSelector}
        </InputLabel>
        <Select
          labelId="taf-selector-label"
          id="dropdown-selector"
          value={selectedOption}
          onChange={handleChange}
          label={layout.layout.appBar.TAFSelector}
          sx={styles.select}
        >
          {tafs.map(({ id, name }) => (
            <MenuItem key={id} value={id}>
              {name}
            </MenuItem>
          ))}
          {tafs.length === 0 && (
            <MenuItem disabled>Aucune TAF disponible</MenuItem>
          )}
        </Select>
      </FormControl>
      <Link to="/taf/new">
        <IconButton>
          <AddCircle color="secondary" />
        </IconButton>
      </Link>
    </Box>
  );
};

TAFSelector.propTypes = {
  selectedOption: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  tafs: PropTypes.array.isRequired,
};

export default TAFSelector;
