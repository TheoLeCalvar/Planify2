// React imports
import React from "react";
import {
  Outlet,
  redirect,
  useNavigate,
  useOutletContext,
  useParams,
  useRevalidator,
} from "react-router-dom"; // React Router hooks for navigation, context, and revalidation

// Custom components
import ValidatedInput from "@/components/ValidatedInput"; // Input component with validation
import ValidatedForm from "@/components/ValidatedForm"; // Form component with validation
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests
import UserSelector from "@/components/UserSelector"; // Component for selecting users
import ConfirmationButton from "@/components/ConfirmationButton"; // Button with confirmation dialog

// Material-UI imports
import { Typography } from "@mui/material"; // Typography component for text styling

// Notifications
import { toast } from "react-toastify"; // Notifications for success and error messages

// PropTypes for type checking
import PropTypes from "prop-types";

/**
 * Action function to handle form submission for creating or updating a UE.
 * Sends a POST or PUT request depending on whether an `idUE` is provided.
 *
 * @param {Object} request - The HTTP request object.
 * @param {Object} params - Parameters passed to the action, including `idUE` and `idTAF`.
 * @returns {Object|null} - Redirects on success or returns null on failure.
 */
export async function action({ request, params }) {
  const updates = await request.json(); // Parse the request body as JSON

  if (params.idUE) {
    // Update an existing UE
    return await axiosInstance
      .put(`/ue/${params.idUE}`, updates)
      .then(() => {
        toast.success("UE mise à jour"); // Show success notification
        return redirect(".."); // Redirect to the parent route
      })
      .catch(() => {
        toast.error("Erreur lors de la mise à jour de l'UE"); // Show error notification
        return null; // Return null on failure
      });
  } else {
    // Create a new UE
    return await axiosInstance
      .post(`/ue`, {
        ...updates,
        tafId: parseInt(params.idTAF), // Include the TAF ID in the request
      })
      .then((response) => {
        toast.success("UE créée"); // Show success notification
        return redirect("../ue/" + response.data); // Redirect to the newly created UE
      })
      .catch(() => {
        toast.error("Erreur lors de la création de l'UE"); // Show error notification
        return null; // Return null on failure
      });
  }
}

/**
 * DeleteUEButton component.
 * Renders a confirmation button to delete a UE.
 *
 * @param {string} idUE - The ID of the UE to delete.
 * @returns {JSX.Element} - The rendered button.
 */
const DeleteUEButton = ({ idUE }) => {
  const navigate = useNavigate(); // Hook for navigation
  const revalidator = useRevalidator(); // Hook for revalidating data

  /**
   * Handles the deletion of a UE.
   * Sends a DELETE request and navigates back on success.
   */
  const handleDelete = () => {
    axiosInstance
      .delete(`/ue/${idUE}`) // Send DELETE request
      .then(() => {
        toast.success("UE supprimée"); // Show success notification
        navigate("../.."); // Navigate back to the parent route
        revalidator.revalidate(); // Revalidate the data
      })
      .catch(() => {
        toast.error("Erreur lors de la suppression de l'UE"); // Show error notification
      });
  };

  return (
    <ConfirmationButton
      buttonText="Supprimer l'UE"
      onConfirm={handleDelete}
      buttonColor="warning"
      dialogMessage="Êtes-vous sûr de vouloir supprimer cette UE ? Cette action est irréversible."
      confirmText="Supprimer"
    />
  );
};

DeleteUEButton.propTypes = {
  idUE: PropTypes.string.isRequired, // Prop type validation for idUE
};

/**
 * UESettings component.
 * Renders a form for creating or editing a UE, including fields for name, description, and managers.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function UESettings() {
  const context = useOutletContext(); // Access context data from the parent route
  const navigate = useNavigate(); // Hook for navigation
  const params = useParams(); // Access route parameters

  const isEditing = !!params.idUE; // Determine if the form is in edit mode
  const ue = context?.ue; // Retrieve UE data from the context

  // State for managing the list of managers
  const [managers, setManagers] = React.useState(
    ue?.managers.map((m) => m.id) || [], // Initialize managers state
  );

  /**
   * Validates form fields based on their name and value.
   *
   * @param {string} name - The name of the field.
   * @param {any} value - The value of the field.
   * @returns {string} - An error message if validation fails, otherwise an empty string.
   */
  const validateField = (name, value) => {
    switch (name) {
      case "name":
        if (value.length < 3)
          return "Le nom doit contenir au moins 3 caractères."; // Validation for name field
        break;
      default:
        return "";
    }
    return ""; // No error
  };

  /**
   * Handles the cancel action by navigating back to the parent route.
   */
  const onCancel = () => {
    navigate("..");
  };

  return (
    <>
      {/* Header section */}
      <Typography variant="h5" mt={3}>
        {isEditing ? "Paramètres de l'UE" : "Nouvelle UE"}
      </Typography>

      {/* Form for creating or editing a UE */}
      <ValidatedForm
        validateField={validateField}
        onCancel={onCancel}
        actionButtons={isEditing ? <DeleteUEButton idUE={ue.id} /> : null}
      >
        {/* Name input field */}
        <ValidatedInput
          name="name"
          label="Nom"
          defaultValue={ue?.name}
          margin="normal"
          fullWidth
          required
        />

        {/* Description input field */}
        <ValidatedInput
          name="description"
          label="Description"
          defaultValue={ue?.description}
          multiline
          minRows={3}
          margin="normal"
          fullWidth
        />

        {/* Managers selector */}
        <ValidatedInput
          name={"managers"}
          label={"Responsables"}
          value={managers}
        >
          <UserSelector
            lecturers={managers}
            setLecturers={setManagers}
            title="Responsable UE"
          />
        </ValidatedInput>
      </ValidatedForm>

      {/* Render nested routes with the same context */}
      <Outlet context={context} />
    </>
  );
}
