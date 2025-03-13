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
import layout from "@/config/locale.json";
import styles from "./TafSelector.styles";
import { AddCircle } from "@mui/icons-material";
import { Link, useNavigate, useParams } from "react-router-dom";
import { ProfileContext } from "@/hooks/ProfileContext";

const TAFSelector = ({ tafs }) => {
  // Wrap the onChange to extract the value and then forward it.
  const navigate = useNavigate();
  const params = useParams();

  const handleChange = (event) => {
    navigate(`/taf/${event.target.value}`);
  };

  const { profile } = useContext(ProfileContext);

  return (
    <Box sx={styles.TAFSelectorBox}>
      <FormControl sx={styles.formControl}>
        <InputLabel id="taf-selector-label" sx={styles.label}>
          {layout.layout.appBar.TAFSelector}
        </InputLabel>
        <Select
          labelId="taf-selector-label"
          id="dropdown-selector"
          value={params?.idTAF || ""}
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

TAFSelector.propTypes = {
  tafs: PropTypes.array.isRequired,
};

export default TAFSelector;
