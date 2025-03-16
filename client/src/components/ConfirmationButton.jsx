// React imports
import React, { useState } from "react";
import PropTypes from "prop-types"; // PropTypes for type checking

// Material-UI imports
import { Button, Tooltip } from "@mui/material"; // Button and Tooltip components

// Local imports
import ConfirmationDialog from "./ConfirmationDialog"; // Custom confirmation dialog component

/**
 * ConfirmationButton component.
 * This component renders a button that triggers a confirmation dialog when clicked.
 * It allows users to confirm or cancel an action before proceeding.
 *
 * @param {Object} props - The component props.
 * @param {React.Element} [props.buttonComponent] - Custom button component to use instead of the default button.
 * @param {string} [props.variant] - The variant of the button (e.g., "contained", "outlined").
 * @param {string} [props.buttonText="Effectuer une action"] - The text displayed on the button.
 * @param {string} [props.tooltip] - Tooltip text displayed when hovering over the button.
 * @param {string} [props.buttonColor="primary"] - The color of the button.
 * @param {string} [props.dialogTitle="Confirmation"] - The title of the confirmation dialog.
 * @param {string} [props.dialogMessage="Êtes-vous sûr de vouloir effectuer cette action ?"] - The message displayed in the confirmation dialog.
 * @param {string} [props.confirmText="Confirmer"] - The text for the confirm button in the dialog.
 * @param {string} [props.cancelText="Annuler"] - The text for the cancel button in the dialog.
 * @param {Function} [props.onConfirm=() => {}] - Callback function executed when the confirm button is clicked.
 *
 * @returns {JSX.Element} - The rendered ConfirmationButton component.
 */
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
  const [open, setOpen] = useState(false); // State to manage the dialog's open/close status

  /**
   * Opens the confirmation dialog.
   */
  const handleOpen = () => setOpen(true);

  /**
   * Closes the confirmation dialog.
   */
  const handleClose = () => setOpen(false);

  return (
    <>
      {/* Trigger button */}
      <Tooltip title={tooltip}>
        {buttonComponent ? (
          // If a custom button component is provided, clone it and attach the onClick handler
          React.cloneElement(buttonComponent, { onClick: handleOpen })
        ) : (
          // Default button
          <Button
            variant={variant ?? "contained"}
            color={buttonColor}
            onClick={handleOpen}
          >
            {buttonText}
          </Button>
        )}
      </Tooltip>

      {/* Confirmation dialog */}
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

// Define the prop types for the component
ConfirmationButton.propTypes = {
  buttonComponent: PropTypes.element, // Custom button component
  variant: PropTypes.string, // Button variant (e.g., "contained", "outlined")
  buttonText: PropTypes.string, // Text displayed on the button
  tooltip: PropTypes.string, // Tooltip text
  buttonColor: PropTypes.string, // Button color (e.g., "primary", "secondary")
  dialogTitle: PropTypes.string, // Title of the confirmation dialog
  dialogMessage: PropTypes.string, // Message displayed in the confirmation dialog
  confirmText: PropTypes.string, // Text for the confirm button in the dialog
  cancelText: PropTypes.string, // Text for the cancel button in the dialog
  onConfirm: PropTypes.func, // Callback function executed on confirmation
};

// Export the component as the default export
export default ConfirmationButton;
