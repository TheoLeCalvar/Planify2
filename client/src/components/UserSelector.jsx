import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  Button,
  Chip,
  FormControl,
  Stack,
  Typography,
  Autocomplete,
} from "@mui/material";
import { Checkbox, TextField } from "@mui/material";
import axiosInstance from "@/config/axiosConfig";
import PropTypes from "prop-types";

// Local imports
import CreateUserDialog from "@/components/CreateUser";

const styles = {
  checkbox: { mr: 1 },
};

// Subcomponent for the lecturer Autocomplete field
const UserSelector = ({ lecturers, setLecturers, options, title }) => {
  const [openNewUser, setOpenNewUser] = useState(false);
  const [users, setUsers] = useState(options ?? []);

  const params = useParams();

  const handleLecturersChange = (e, value) => {
    // Map lecturer objects to their ids (or keep id if already a primitive)
    setLecturers(value.map((lecturer) => lecturer.id || lecturer));
  };

  const updateUsers = () => {
    axiosInstance
      .get(`/users`, { params: { tafId: params?.idTAF } })
      .then((response) => {
        setUsers(response.data);
      });
  };

  useEffect(() => {
    //FIXME : Userlist is not updated when a new user is created
    if (!options) {
      updateUsers();
    }
  }, []);

  // Sort options by "alreadySelected" status (selected ones first)
  const sortedOptions = [...users].sort(
    (a, b) => Number(b.alreadySelected) - Number(a.alreadySelected),
  );

  // Helper to render a tag (Chip) from a lecturer id
  const renderTag = (optionId, index, getTagProps) => {
    const lecturer = users.find((opt) => opt.id === optionId);
    if (!lecturer) return null;
    const { key, ...chipProps } = getTagProps({ index }); // eslint-disable-line no-unused-vars
    return <Chip key={lecturer.id} label={lecturer.name} {...chipProps} />;
  };

  return (
    <>
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
      <CreateUserDialog
        open={openNewUser}
        onClose={() => {
          setOpenNewUser(false);
        }}
      />
    </>
  );
};

UserSelector.propTypes = {
  key: PropTypes.string,
  lecturers: PropTypes.array.isRequired,
  setLecturers: PropTypes.func.isRequired,
  options: PropTypes.array,
  title: PropTypes.string.isRequired,
};

export default UserSelector;
