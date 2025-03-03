// React imports
import React, { useEffect } from "react";
import { Outlet, useOutletContext, useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";

// Material-UI imports
import { Box } from "@mui/material";
import { Select, MenuItem } from "@mui/material";
import { FormControl, InputLabel } from "@mui/material";

export default function TAFResults() {
  const context = useOutletContext();
  const taf = context.taf;

  const options = taf?.resultPlanning ?? [];

  const { idPlanning } = useParams();

  const navigate = useNavigate();
  const [selectedResult, setSelectedResult] = React.useState(idPlanning ?? "");

  const handleChange = (event) => {
    const selectedValue = event.target.value;
    setSelectedResult(selectedValue);
    if (selectedValue) {
      navigate(`./${selectedValue}`);
    }
  };

  const sortedOptions = options.sort((a, b) => b.id - a.id);

  // Redirection automatique au chargement
  useEffect(() => {
    if (sortedOptions.length > 0 && selectedResult === "") {
      const defaultId = sortedOptions[0].id;
      setSelectedResult(defaultId);
      navigate(`./${defaultId}`);
    }
  }, [sortedOptions, selectedResult, navigate]);

  return (
    <>
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
                {option.timestamp} - {option.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>
      <Outlet
        context={{
          ...context,
          planning: options.find((o) => o.id == selectedResult),
        }}
      />
    </>
  );
}
