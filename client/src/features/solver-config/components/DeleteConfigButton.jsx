// React imports
import React from "react";
import PropTypes from "prop-types";
import { useNavigate, useRevalidator } from "react-router-dom";
import { toast } from "react-toastify";

// Local imports
import axiosInstance from "@/config/axiosConfig";
import ConfirmationButton from "@/components/ConfirmationButton";

const DeleteConfigButton = ({ idConfig }) => {
  const navigate = useNavigate();
  const revalidator = useRevalidator();

  const handleDelete = () => {
    axiosInstance
      .delete(`/config/${idConfig}`)
      .then(() => {
        toast.success("Configuration supprimée");
        navigate("../..");
        revalidator.revalidate();
      })
      .catch(() => {
        toast.error("Erreur lors de la suppression de la configuration");
      });
  };

  return (
    <ConfirmationButton
      buttonText="Supprimer la configuration"
      onConfirm={handleDelete}
      buttonColor="warning"
      dialogMessage="Êtes-vous sûr de vouloir supprimer cette configuration ? Cette action est irréversible."
      confirmText="Supprimer"
    />
  );
};

DeleteConfigButton.propTypes = {
  idConfig: PropTypes.number.isRequired,
};

export default DeleteConfigButton;
