import React from "react";
import {
  Outlet,
  redirect,
  useNavigate,
  useOutletContext,
  useParams,
  useRevalidator,
} from "react-router-dom";
import ValidatedInput from "@/components/ValidatedInput";
import ValidatedForm from "@/components/ValidatedForm";
import { USE_MOCK_DATA } from "@/config/constants";
import axiosInstance from "@/config/axiosConfig";
import UserSelector from "@/components/UserSelector";
import { Typography } from "@mui/material";
import ConfirmationButton from "@/components/ConfirmationButton";
import PropTypes from "prop-types";
import { toast } from "react-toastify";

export async function action({ request, params }) {
  //TODO: Update section with backend call
  const updates = await request.json();

  if (USE_MOCK_DATA) {
    const delay = () => {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve("Résolu après 2 secondes");
        }, 2000); // 2000 millisecondes = 2 secondes
      });
    };

    await delay();
    console.log(updates);
  } else {
    if (params.idUE) {
      return await axiosInstance
        .put(`/ue/${params.idUE}`, updates)
        .then(() => {
          toast.success("UE mise à jour");
          return redirect("..");
        })
        .catch(() => {
          toast.error("Erreur lors de la mise à jour de l'UE");
          return null;
        });
    } else {
      return await axiosInstance
        .post(`/ue`, {
          ...updates,
          tafId: parseInt(params.idTAF),
        })
        .then(() => {
          toast.success("UE créée");
          return redirect("..");
        })
        .catch(() => {
          toast.error("Erreur lors de la création de l'UE");
          return null;
        });
    }
  }
}

const DeleteUEButton = ({ idUE }) => {
  const navigate = useNavigate();
  const revalidator = useRevalidator();

  const handleDelete = () => {
    axiosInstance
      .delete(`/ue/${idUE}`)
      .then(() => {
        toast.success("UE supprimée");
        navigate("../..");
        revalidator.revalidate();
      })
      .catch(() => {
        toast.error("Erreur lors de la suppression de l'UE");
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
  idUE: PropTypes.string.isRequired,
};

export default function UESettings() {
  const context = useOutletContext();
  const navigate = useNavigate();
  const params = useParams();

  const isEditing = !!params.idUE;

  const ue = context?.ue;

  const [managers, setManagers] = React.useState(
    ue?.managers.map((m) => m.id) || [],
  );
  const validateField = (name, value) => {
    switch (name) {
      case "name":
        if (value.length < 3)
          return "Le nom doit contenir au moins 3 caractères.";
        break;
      default:
        return "";
    }
    return ""; // Pas d'erreur
  };

  const onCancel = () => {
    navigate("..");
  };

  return (
    <>
      <Typography variant="h5" mt={3}>
        {isEditing ? "Paramètres de l'UE" : "Nouvelle UE"}
      </Typography>
      <ValidatedForm
        validateField={validateField}
        onCancel={onCancel}
        actionButtons={isEditing ? <DeleteUEButton idUE={ue.id} /> : null}
      >
        <ValidatedInput
          name="name"
          label="Nom"
          defaultValue={ue?.name}
          margin="normal"
          fullWidth
          required
        />
        <ValidatedInput
          name="description"
          label="Description"
          defaultValue={ue?.description}
          multiline
          minRows={3}
          margin="normal"
          fullWidth
        />
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
      <Outlet context={context} />
    </>
  );
}
