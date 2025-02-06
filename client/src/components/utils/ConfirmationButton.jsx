// src/components/ConfirmationButton.js
import React, { useState } from "react";
import PropTypes from "prop-types";
import { Button, Tooltip } from "@mui/material";
import ConfirmationDialog from "./ConfirmationDialog";

const ConfirmationButton = ({
  buttonComponent,
  variant,
  buttonText = "Effectuer une action",
  tooltip,
  buttonColor = "primary",
  dialogTitle = "Confirmation",
  dialogMessage = "Êtes-vous sûr de vouloir effectuer cette action ?",
  confirmText = "Confirmer",
  cancelText = "Annuler",
  onConfirm = () => {},
}) => {
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <>
      {/* Bouton déclencheur */}
      <Tooltip title={tooltip}>
        {buttonComponent ? (
          React.cloneElement(buttonComponent, { onClick: handleOpen })
        ) : (
          <Button
            variant={variant ?? "contained"}
            color={buttonColor}
            onClick={handleOpen}
          >
            {buttonText}
          </Button>
        )}
      </Tooltip>

      {/* Popup de confirmation */}
      <ConfirmationDialog
        cancelText={cancelText}
        confirmText={confirmText}
        dialogMessage={dialogMessage}
        dialogTitle={dialogTitle}
        onConfirm={onConfirm}
        handleClose={handleClose}
        open={open}
      />
    </>
  );
};

ConfirmationButton.propTypes = {
  buttonComponent: PropTypes.element,
  variant: PropTypes.string,
  buttonText: PropTypes.string,
  tooltip: PropTypes.string,
  buttonColor: PropTypes.string,
  dialogTitle: PropTypes.string,
  dialogMessage: PropTypes.string,
  confirmText: PropTypes.string,
  cancelText: PropTypes.string,
  onConfirm: PropTypes.func,
};

export default ConfirmationButton;
