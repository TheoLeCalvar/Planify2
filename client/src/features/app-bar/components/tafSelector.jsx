// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Box, FormControl, InputLabel, MenuItem, Select } from "@mui/material";

// Local imports
import layout from "../../../config/locale.json";
import styles from "./tafSelector.styles";

const TAFSelector = ({ selectedOption, onChange, tafs }) => {
  // Wrap the onChange to extract the value and then forward it.
  const handleChange = (event) => {
    onChange(event.target.value);
  };

  return (
    <Box sx={styles.TAFSelectorBox}>
      <FormControl variant="standard" sx={styles.formControl}>
        <InputLabel htmlFor="dropdown-selector">
          {layout.layout.appBar.TAFSelector}
        </InputLabel>
        <Select
          id="dropdown-selector"
          value={selectedOption}
          onChange={handleChange}
          label={layout.layout.appBar.TAFSelector}
        >
          {tafs.map(({ id, name }) => (
            <MenuItem key={id} value={id}>
              {name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  );
};

TAFSelector.propTypes = {
  selectedOption: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  tafs: PropTypes.array.isRequired,
};

export default TAFSelector;
