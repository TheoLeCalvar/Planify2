import React from "react";
import PropTypes from "prop-types";
import { Box, Button, Stack } from "@mui/material";
import { Link } from "react-router-dom";
import AddIcon from "@mui/icons-material/Add";
import locale from "@/config/locale.json";

// Styles
import styles from "./SideBarActions.styles";
import ConfirmationButton from "@/components/ConfirmationButton";

const SidebarActions = ({
  tafID,
  resultPlanning,
  handleGenerateCalendar,
  generatingCalendar,
}) => {
  return (
    <Box sx={styles.container}>
      <Stack spacing={2}>
        <Link to={`/taf/${tafID}/ue/new`}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            sx={{ width: "100%" }}
          >
            {locale.layout.sideBar.addUE}
          </Button>
        </Link>
        {resultPlanning?.length > 0 && (
          <Link to={`/taf/${tafID}/results`}>
            <Button variant="outlined" sx={{ width: "100%" }}>
              Voir les plannings
            </Button>
          </Link>
        )}
        <ConfirmationButton
          onClick={handleGenerateCalendar}
          variant="outlined"
          sx={{ width: "100%" }}
          disabled={generatingCalendar}
          buttonText={
            generatingCalendar ? "Génération..." : "Générer le planning"
          }
          dialogMessage="Êtes-vous sûr de vouloir générer un nouveau planning ?"
        />
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
