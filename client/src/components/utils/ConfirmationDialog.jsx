// src/components/ConfirmationButton.js
import React from "react";
import PropTypes from "prop-types";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";

const ConfirmationDialog = ({
  dialogTitle = "Confirmation",
  dialogMessage = "Êtes-vous sûr de vouloir effectuer cette action ?",
  confirmText = "Confirmer",
  cancelText = "Annuler",
  onConfirm = () => {},
  onCancel = () => {},
  open,
  handleClose,
}) => {
  const handleConfirm = () => {
    onConfirm();
    handleClose();
  };

  const handleCancel = () => {
    onCancel();
    handleClose();
  };

  return (
    <>
      {/* Popup de confirmation */}
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="confirmation-dialog-title"
        aria-describedby="confirmation-dialog-description"
      >
        <DialogTitle id="confirmation-dialog-title">{dialogTitle}</DialogTitle>
        <DialogContent>
          <DialogContentText id="confirmation-dialog-description">
            {dialogMessage}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancel} color="secondary">
            {cancelText}
          </Button>
          <Button
            onClick={handleConfirm}
            variant="contained"
            color="primary"
            autoFocus
          >
            {confirmText}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

ConfirmationDialog.propTypes = {
  dialogTitle: PropTypes.string,
  dialogMessage: PropTypes.string,
  confirmText: PropTypes.string,
  cancelText: PropTypes.string,
  onConfirm: PropTypes.func,
  onCancel: PropTypes.func,
  open: PropTypes.bool.isRequired,
  handleClose: PropTypes.func.isRequired,
};

export default ConfirmationDialog;
