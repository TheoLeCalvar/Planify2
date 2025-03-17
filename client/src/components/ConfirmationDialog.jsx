// React imports
import React from "react";
import PropTypes from "prop-types"; // PropTypes for type checking

// Material-UI imports
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material"; // Material-UI components for dialog and buttons

/**
 * ConfirmationDialog component.
 * This component renders a confirmation dialog with customizable title, message, and actions.
 * It allows users to confirm or cancel an action.
 *
 * @param {Object} props - The component props.
 * @param {string} [props.dialogTitle="Confirmation"] - The title of the confirmation dialog.
 * @param {string} [props.dialogMessage="Êtes-vous sûr de vouloir effectuer cette action ?"] - The message displayed in the dialog.
 * @param {string} [props.confirmText="Confirmer"] - The text for the confirm button.
 * @param {string} [props.cancelText="Annuler"] - The text for the cancel button.
 * @param {Function} [props.onConfirm=() => {}] - Callback function executed when the confirm button is clicked.
 * @param {Function} [props.onCancel=() => {}] - Callback function executed when the cancel button is clicked.
 * @param {boolean} props.open - Whether the dialog is open or not.
 * @param {Function} props.handleClose - Function to close the dialog.
 *
 * @returns {JSX.Element} - The rendered ConfirmationDialog component.
 */
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
  /**
   * Handles the confirm action.
   * Executes the `onConfirm` callback and closes the dialog.
   */
  const handleConfirm = () => {
    onConfirm();
    handleClose();
  };

  /**
   * Handles the cancel action.
   * Executes the `onCancel` callback and closes the dialog.
   */
  const handleCancel = () => {
    onCancel();
    handleClose();
  };

  return (
    <>
      {/* Confirmation dialog */}
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="confirmation-dialog-title"
        aria-describedby="confirmation-dialog-description"
      >
        {/* Dialog title */}
        <DialogTitle id="confirmation-dialog-title">{dialogTitle}</DialogTitle>

        {/* Dialog content */}
        <DialogContent>
          <DialogContentText id="confirmation-dialog-description">
            {dialogMessage}
          </DialogContentText>
        </DialogContent>

        {/* Dialog actions */}
        <DialogActions>
          {/* Cancel button */}
          <Button onClick={handleCancel} color="secondary">
            {cancelText}
          </Button>

          {/* Confirm button (conditionally rendered) */}
          {onConfirm != null && (
            <Button
              onClick={handleConfirm}
              variant="contained"
              color="primary"
              autoFocus
            >
              {confirmText}
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </>
  );
};

// Define the prop types for the component
ConfirmationDialog.propTypes = {
  dialogTitle: PropTypes.string, // Title of the dialog
  dialogMessage: PropTypes.string, // Message displayed in the dialog
  confirmText: PropTypes.string, // Text for the confirm button
  cancelText: PropTypes.string, // Text for the cancel button
  onConfirm: PropTypes.func, // Callback for the confirm action
  onCancel: PropTypes.func, // Callback for the cancel action
  open: PropTypes.bool.isRequired, // Whether the dialog is open
  handleClose: PropTypes.func.isRequired, // Function to close the dialog
};

// Export the component as the default export
export default ConfirmationDialog;
