import React from "react";
import PropTypes from "prop-types";
import { Box, Button, Stack } from "@mui/material";
import { Link } from "react-router-dom";
import AddIcon from "@mui/icons-material/Add";
import locale from "@/config/locale.json";

// Styles
import styles from "./SideBarActions.styles";

const SidebarActions = ({
  tafID,
  resultPlanning,
  handleGenerateCalendar,
  generatingCalendar,
}) => {
  return (
    <Box sx={styles.container}>
      <Stack spacing={2}>
        {resultPlanning?.length > 0 && (
          <Link to={`/taf/${tafID}/results`}>
            <Button variant="outlined" sx={{ width: "100%" }}>
              Voir les calendriers
            </Button>
          </Link>
        )}
        <Button
          onClick={handleGenerateCalendar}
          variant="outlined"
          sx={{ width: "100%" }}
          disabled={generatingCalendar}
        >
          {generatingCalendar ? "Génération..." : "Générer le calendrier"}
        </Button>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          sx={{ width: "100%" }}
        >
          {locale.layout.sideBar.addUE}
        </Button>
      </Stack>
    </Box>
  );
};

SidebarActions.propTypes = {
  tafID: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
  resultPlanning: PropTypes.array,
  handleGenerateCalendar: PropTypes.func.isRequired,
  generatingCalendar: PropTypes.bool.isRequired,
};

export default SidebarActions;
