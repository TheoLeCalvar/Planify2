// src/components/ConfirmationButton.js
import React, { useState } from "react";
import PropTypes from "prop-types";
import { Button } from "@mui/material";
import ConfirmationDialog from "./ConfirmationDialog";

const ConfirmationButton = ({
    buttonComponent,
    variant,
    buttonText = "Effectuer une action",
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
            {buttonComponent
                ? React.cloneElement(buttonComponent, { onClick: handleOpen })
                : (
                <Button
                    variant={variant ?? "contained"}
                    color={buttonColor}
                    onClick={handleOpen}
                >
                    {buttonText}
                </Button>
            )}

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
    buttonText: PropTypes.string,
    buttonColor: PropTypes.string,
    dialogTitle: PropTypes.string,
    dialogMessage: PropTypes.string,
    confirmText: PropTypes.string,
    cancelText: PropTypes.string,
    onConfirm: PropTypes.func,
};

export default ConfirmationButton;
