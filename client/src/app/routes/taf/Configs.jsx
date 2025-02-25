import { Box, IconButton, Typography } from "@mui/material";
import {
  Link,
  Outlet,
  useLoaderData,
  useNavigate,
  useOutletContext,
  useParams,
} from "react-router-dom";
import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import React, { useEffect, useState } from "react";
import axiosInstance from "@/config/axiosConfig";
import { AddCircle } from "@mui/icons-material";

export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}/configs`);
  return response.data;
}

export default function TAFConfigs() {
  const options = useLoaderData();
  const { idConfig } = useParams();

  const navigate = useNavigate();
  const context = useOutletContext();
  const [selectedConfig, setSelectedConfig] = useState(idConfig ?? "");

  const handleChange = (event) => {
    const selectedValue = event.target.value;
    setSelectedConfig(selectedValue);
    if (selectedValue) {
      navigate(`./${selectedValue}`);
    }
  };

  // Redirection automatique au chargement
  useEffect(() => {
    if (options.length > 0 && selectedConfig === "") {
      const defaultId = options[0].id;
      setSelectedConfig(defaultId);
      navigate(`./${defaultId}`);
    }
  }, [options, selectedConfig, navigate]);

  return (
    <>
      <Typography variant="h5" gutterBottom mt={2}>
        Contraintes de génération
      </Typography>
      <Box
        sx={{
          p: 1,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <FormControl sx={{ minWidth: 300 }}>
          <InputLabel id="select-label-configs">
            Choisir une configuration
          </InputLabel>
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
          </Select>
        </FormControl>

        <Link to="./new">
          <IconButton>
            <AddCircle color="secondary" />
          </IconButton>
        </Link>
      </Box>
      <Outlet context={context} />
    </>
  );
}
