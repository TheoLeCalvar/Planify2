// React imports
import React, { useEffect } from "react";
import { Outlet, useOutletContext, useParams } from "react-router-dom"; // React Router hooks for nested routes and context
import { useNavigate } from "react-router-dom"; // Hook for navigation

// Material-UI imports
import { Box } from "@mui/material"; // Box component for layout
import { Select, MenuItem } from "@mui/material"; // Dropdown components
import { FormControl, InputLabel } from "@mui/material"; // Form control components

/**
 * TAFResults component.
 * This component manages the selection and display of generated planning results.
 * It provides a dropdown menu to select a specific planning result and automatically redirects to the default result on load.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function TAFResults() {
  const context = useOutletContext(); // Access context data from the parent route
  const taf = context.taf; // Retrieve TAF data from the context

  const options = taf?.resultPlanning ?? []; // List of generated planning results

  const { idPlanning } = useParams(); // Get the current planning ID from the URL parameters

  const navigate = useNavigate(); // Hook for programmatic navigation
  const [selectedResult, setSelectedResult] = React.useState(idPlanning ?? ""); // State to manage the selected planning result

  /**
   * Handles changes in the dropdown selection.
   * Updates the selected result and navigates to the corresponding route.
   *
   * @param {Object} event - The event object from the dropdown.
   */
  const handleChange = (event) => {
    const selectedValue = event.target.value;
    setSelectedResult(selectedValue); // Update the selected result state
    if (selectedValue) {
      navigate(`./${selectedValue}`); // Navigate to the selected result's route
    }
  };

  /**
   * Sorts the planning options by ID in descending order.
   * Ensures the most recent planning results appear first in the dropdown.
   */
  const sortedOptions = options.sort((a, b) => b.id - a.id);

  /**
   * Automatically redirects to the default planning result on component load.
   * If no result is selected, it selects the most recent result by default.
   */
  useEffect(() => {
    if (sortedOptions.length > 0 && selectedResult === "") {
      const defaultId = sortedOptions[0].id; // Get the ID of the most recent result
      setSelectedResult(defaultId); // Set the default result
      navigate(`./${defaultId}`); // Navigate to the default result's route
    }
  }, [sortedOptions, selectedResult, navigate]);

  return (
    <>
      {/* Dropdown menu for selecting a generated planning result */}
      <Box
        sx={{
          p: 1,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <FormControl sx={{ minWidth: 200 }}>
          <InputLabel id="select-label">Choisir un EDT généré</InputLabel>
          <Select
            labelId="select-label"
            value={selectedResult}
            onChange={handleChange}
            label="Choisir un EDT généré"
          >
            {sortedOptions.map((option) => (
              <MenuItem key={option.id} value={option.id}>
                {option.timestamp} - {option.name}{" "}
                {/* Display timestamp and name */}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {/* Nested route rendering with additional context */}
      <Outlet
        context={{
          ...context, // Pass down the parent context
          planning: options.find((o) => o.id == selectedResult), // Add the selected planning result to the context
        }}
      />
    </>
  );
}
