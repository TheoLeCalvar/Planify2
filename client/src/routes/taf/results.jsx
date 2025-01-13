import React, { useEffect } from "react";
import { Box, Typography } from "@mui/material";
import { Outlet, useOutletContext, useParams } from "react-router-dom";
import { Select, MenuItem } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { FormControl, InputLabel } from "@mui/material";

export default function TAFResults() {
    const context = useOutletContext();
    const taf = context.taf;

    const options = taf?.resultPlannings ?? [];

    const { idPlanning } = useParams();

    const navigate = useNavigate();
    const [selectedResult, setSelectedResult] = React.useState(
        idPlanning ?? ""
    );

    const handleChange = (event) => {
        const selectedValue = event.target.value;
        setSelectedResult(selectedValue);
        if (selectedValue) {
            navigate(`./${selectedValue}`);
        }
    };

    // Redirection automatique au chargement
    useEffect(() => {
        if (options.length > 0 && selectedResult === "") {
            const defaultId = options[0].id;
            setSelectedResult(defaultId);
            navigate(`./${defaultId}`);
        }
    }, [options, selectedResult, navigate]);

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
                    <InputLabel id="select-label">
                        Choisir un EDT généré
                    </InputLabel>
                    <Select
                        labelId="select-label"
                        value={selectedResult}
                        onChange={handleChange}
                        label="Choisir un EDT généré"
                        displayEmpty
                    >
                        {options.map((option) => (
                            <MenuItem key={option.id} value={option.id}>
                                {option.date}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </Box>
            <Outlet context={context} />
        </>
    );
}
