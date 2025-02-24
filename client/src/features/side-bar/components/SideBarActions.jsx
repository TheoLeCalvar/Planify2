import React from "react";
import PropTypes from "prop-types";
import { Box, Button, Stack } from "@mui/material";
import { Link } from "react-router-dom";
import AddIcon from "@mui/icons-material/Add";
import locale from "@/config/locale.json";

// Styles
import styles from "./SideBarActions.styles";
import ConfirmationButton from "@/components/ConfirmationButton";
import axiosInstance from "@/config/axiosConfig";
import { toast } from "react-toastify";

const SidebarActions = ({ tafID, resultPlanning }) => {
  const handleGenerateCalendar = () => {
    axiosInstance
      .get(`/solver/run/${tafID}`)
      .then(() => {
        toast.info("Génération du planning... ");
      })
      .catch(() => {
        toast.error("Erreur lors de la génération du planning");
      });
  };

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
          onConfirm={handleGenerateCalendar}
          variant="outlined"
          sx={{ width: "100%" }}
          buttonText="Générer le planning"
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
