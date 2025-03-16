// React imports
import React from "react";
import PropTypes from "prop-types";
import { useNavigate, useRevalidator } from "react-router-dom";
import { toast } from "react-toastify";

// Local imports
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests
import ConfirmationButton from "@/components/ConfirmationButton"; // Component for rendering a confirmation dialog button

/**
 * DeleteConfigButton Component
 * This component renders a button that allows users to delete a configuration.
 * When clicked, it shows a confirmation dialog. If confirmed, it sends a delete request to the server.
 *
 * @param {Object} props - The component props.
 * @param {number} props.idConfig - The ID of the configuration to delete.
 * @returns {JSX.Element} - The rendered DeleteConfigButton component.
 */
const DeleteConfigButton = ({ idConfig }) => {
  const navigate = useNavigate(); // Hook for navigating between routes
  const revalidator = useRevalidator(); // Hook for revalidating data after deletion

  /**
   * Handles the deletion of the configuration.
   * Sends a DELETE request to the server and handles success or error responses.
   */
  const handleDelete = () => {
    axiosInstance
      .delete(`/config/${idConfig}`) // API endpoint for deleting the configuration
      .then(() => {
        // Show a success toast notification
        toast.success("Configuration supprimée");

        // Navigate back to the parent route
        navigate("../..");

        // Revalidate the data to refresh the UI
        revalidator.revalidate();
      })
      .catch(() => {
        // Show an error toast notification if the deletion fails
        toast.error("Erreur lors de la suppression de la configuration");
      });
  };

  return (
    <ConfirmationButton
      buttonText="Supprimer la configuration" // Text displayed on the button
      onConfirm={handleDelete} // Callback function triggered on confirmation
      buttonColor="warning" // Button color (warning indicates a destructive action)
      dialogMessage="Êtes-vous sûr de vouloir supprimer cette configuration ? Cette action est irréversible." // Confirmation dialog message
      confirmText="Supprimer" // Text for the confirmation button in the dialog
    />
  );
};

// Define the expected prop types for the component
DeleteConfigButton.propTypes = {
  /**
   * The ID of the configuration to delete.
   * This is a required number.
   */
  idConfig: PropTypes.number.isRequired,
};

export default DeleteConfigButton;
