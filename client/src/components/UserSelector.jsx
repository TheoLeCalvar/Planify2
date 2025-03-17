// React imports
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

// Material-UI imports
import {
  Button,
  Chip,
  FormControl,
  Stack,
  Typography,
  Autocomplete,
  Checkbox,
  TextField,
} from "@mui/material";

// Axios instance for API requests
import axiosInstance from "@/config/axiosConfig";

// PropTypes for type checking
import PropTypes from "prop-types";

// Local imports
import CreateUserDialog from "@/components/CreateUser"; // Dialog for creating a new user

// Styles for the checkbox
const styles = {
  checkbox: { mr: 1 },
};

/**
 * UserSelector component.
 * This component provides an interface for selecting users (lecturers) from a list.
 * It supports grouping, searching, and creating new users.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.lecturers - The currently selected lecturers.
 * @param {Function} props.setLecturers - Function to update the selected lecturers.
 * @param {Array} [props.options] - Predefined list of user options (optional).
 * @param {string} props.title - The title for the input field.
 *
 * @returns {JSX.Element} - The rendered UserSelector component.
 */
const UserSelector = ({ lecturers, setLecturers, options, title }) => {
  const [openNewUser, setOpenNewUser] = useState(false); // State to manage the "Create User" dialog visibility
  const [users, setUsers] = useState(options ?? []); // State to store the list of users

  const params = useParams(); // Access route parameters (e.g., TAF ID)

  /**
   * Handles changes in the selected lecturers.
   *
   * @param {Object} e - The event object.
   * @param {Array} value - The updated list of selected lecturers.
   */
  const handleLecturersChange = (e, value) => {
    // Map lecturer objects to their IDs (or keep the ID if already a primitive)
    setLecturers(value.map((lecturer) => lecturer.id || lecturer));
  };

  /**
   * Fetches the list of users from the server.
   */
  const updateUsers = () => {
    axiosInstance
      .get(`/users`, { params: { tafId: params?.idTAF } }) // Fetch users for the current TAF
      .then((response) => {
        setUsers(response.data); // Update the user list
      });
  };

  // Fetch users when the component mounts or when no predefined options are provided
  useEffect(() => {
    // FIXME: User list is not updated when a new user is created
    if (!options) {
      updateUsers();
    }
  }, []);

  // Sort options by "alreadySelected" status (selected users appear first)
  const sortedOptions = [...users].sort(
    (a, b) => Number(b.alreadySelected) - Number(a.alreadySelected),
  );

  /**
   * Renders a tag (Chip) for a selected lecturer.
   *
   * @param {string} optionId - The ID of the lecturer.
   * @param {number} index - The index of the tag.
   * @param {Function} getTagProps - Function to get props for the tag.
   * @returns {JSX.Element|null} - The rendered Chip component or null if the lecturer is not found.
   */
  const renderTag = (optionId, index, getTagProps) => {
    const lecturer = users.find((opt) => opt.id === optionId);
    if (!lecturer) return null;
    const { key, ...chipProps } = getTagProps({ index }); // eslint-disable-line no-unused-vars
    return <Chip key={lecturer.id} label={lecturer.name} {...chipProps} />;
  };

  return (
    <>
      {/* Autocomplete input for selecting users */}
      <FormControl fullWidth>
        <Autocomplete
          multiple
          options={sortedOptions}
          groupBy={(option) =>
            option.alreadySelected
              ? "Intervenants de cette TAF"
              : "Toutes les personnes"
          }
          disableCloseOnSelect
          value={lecturers}
          getOptionLabel={(option) => option.name}
          onChange={handleLecturersChange}
          isOptionEqualToValue={(option, value) => option.id === value}
          renderOption={(props, option, { selected }) => {
            const { key, ...otherProps } = props;
            return (
              <li key={key} {...otherProps}>
                <Checkbox checked={selected} sx={styles.checkbox} />
                {option.name}
              </li>
            );
          }}
          renderTags={(value, getTagProps) =>
            value.map((option, index) => renderTag(option, index, getTagProps))
          }
          renderInput={(params) => (
            <TextField
              {...params}
              label={title}
              placeholder="Sélectionnez des personnes..."
            />
          )}
          noOptionsText={
            <Stack gap={1} alignItems="center">
              <Typography variant="body2">Aucun utilisateur trouvé.</Typography>
              <Button
                color="primary"
                variant="contained"
                onClick={() => setOpenNewUser(true)}
              >
                Créer un nouvel utilisateur
              </Button>
            </Stack>
          }
        />
      </FormControl>

      {/* Dialog for creating a new user */}
      <CreateUserDialog
        open={openNewUser}
        onClose={() => {
          setOpenNewUser(false);
        }}
      />
    </>
  );
};

// Define the prop types for the component
UserSelector.propTypes = {
  key: PropTypes.string,
  lecturers: PropTypes.array.isRequired, // List of selected lecturers
  setLecturers: PropTypes.func.isRequired, // Function to update the selected lecturers
  options: PropTypes.array, // Predefined list of user options (optional)
  title: PropTypes.string.isRequired, // Title for the input field
};

// Export the component as the default export
export default UserSelector;
