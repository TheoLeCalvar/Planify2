// React imports
import React from "react";
import {
  Outlet,
  redirect,
  useNavigate,
  useOutletContext,
  useParams,
  useRevalidator,
} from "react-router-dom";
import PropTypes from "prop-types";

// Material-UI imports
import { Stack, Typography } from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";

// DayJS imports
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import "dayjs/locale/fr";

// Local imports
import ValidatedInput from "@/components/ValidatedInput";
import ValidatedForm from "@/components/ValidatedForm";
import axiosInstance from "@/config/axiosConfig";
import UserSelector from "@/components/UserSelector";
import ConfirmationButton from "@/components/ConfirmationButton";
import { toast } from "react-toastify";

dayjs.extend(customParseFormat);

export async function action({ request, params }) {
  const data = await request.json();

  if (params.idTAF) {
    return await axiosInstance
      .put(`/taf/${params.idTAF}`, data)
      .then(() => {
        toast.success("TAF mise à jour");
        return redirect("..");
      })
      .catch(() => {
        toast.error("Erreur lors de la mise à jour du TAF");
        return null;
      });
  } else {
    return await axiosInstance
      .post(`/taf`, data)
      .then((response) => {
        toast.success("TAF créé");
        return redirect("/taf/" + response.data);
      })
      .catch(() => {
        toast.error("Erreur lors de la création du TAF");
        return null;
      });
  }
}

const DeleteTAFButton = ({ idTAF }) => {
  const navigate = useNavigate();
  const revalidator = useRevalidator();

  const handleDelete = () => {
    axiosInstance
      .delete(`/taf/${idTAF}`)
      .then(() => {
        toast.success("TAF supprimée");
        navigate("../..");
        revalidator.revalidate();
      })
      .catch(() => {
        toast.error("Erreur lors de la suppression de la TAF");
      });
  };

  return (
    <ConfirmationButton
      buttonText="Supprimer la TAF"
      onConfirm={handleDelete}
      buttonColor="warning"
      dialogMessage="Êtes-vous sûr de vouloir supprimer cette TAF ? Toutes les UE et cours seront également supprimés. Cette action est irréversible."
      confirmText="Supprimer"
    />
  );
};

DeleteTAFButton.propTypes = {
  idTAF: PropTypes.number.isRequired,
};

const minDate = dayjs().subtract(1, "year");
const maxDate = dayjs().add(2, "year");

export default function TAFSettings() {
  const context = useOutletContext();
  const navigate = useNavigate();
  const params = useParams();

  const isEditing = !!params.idTAF;

  const taf = context?.taf;

  const [managers, setManagers] = React.useState(
    taf?.managers.map((m) => m.id) || [],
  );

  const validateField = (name, value, otherValues) => {
    switch (name) {
      case "name":
        if (value.length < 3)
          return "Le nom doit contenir au moins 3 caractères.";
        break;
      default:
        return "";
      case "endDate": {
        const compareValue = dayjs.isDayjs(value) ? value : dayjs(value);
        if (!compareValue.isValid()) {
          return "La date de fin n'est pas valide.";
        }
        if (compareValue.isBefore(dayjs(otherValues.startDate))) {
          return "La date de fin doit être après la date de début.";
        }
        if (compareValue.diff(dayjs(otherValues.startDate), "week") < 1) {
          return "La durée d'étalement de la TAF doit être au moins d'une semaine.";
        }
        break;
      }
      case "startDate": {
        const compareValue2 = dayjs.isDayjs(value) ? value : dayjs(value);
        if (!compareValue2.isValid()) {
          return "La date de début n'est pas valide.";
        }
      }
    }
    return ""; // Pas d'erreur
  };

  const onCancel = () => {
    navigate("..");
  };

  return (
    <>
      <Typography variant="h5" gutterBottom mt={2}>
        {isEditing ? "Paramètres de la TAF" : "Nouvelle TAF"}
      </Typography>
      <ValidatedForm
        validateField={validateField}
        onCancel={onCancel}
        actionButtons={isEditing ? <DeleteTAFButton idTAF={taf.id} /> : null}
      >
        <Stack direction="column" spacing={3}>
          <ValidatedInput
            name="name"
            label="Nom"
            defaultValue={taf?.name}
            margin="normal"
            fullWidth
            required
          />
          <ValidatedInput
            name="description"
            label="Description"
            defaultValue={taf?.description}
            multiline
            minRows={3}
            margin="normal"
            fullWidth
          />
          <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="fr">
            <Stack direction="row" spacing={2} mt={2}>
              <ValidatedInput
                name="startDate"
                label="Date de début des cours"
                defaultValue={dayjs(taf?.startDate)}
              >
                <DatePicker minDate={minDate} maxDate={maxDate} />
              </ValidatedInput>
              <ValidatedInput
                name="endDate"
                label="Date de fin des cours"
                defaultValue={dayjs(taf?.endDate)}
              >
                <DatePicker minDate={minDate} maxDate={maxDate} />
              </ValidatedInput>
            </Stack>
          </LocalizationProvider>
          <ValidatedInput
            name={"managers"}
            label={"Responsables"}
            value={managers}
          >
            <UserSelector
              lecturers={managers}
              setLecturers={setManagers}
              title="Responsable TAF"
            />
          </ValidatedInput>
        </Stack>
      </ValidatedForm>
      <Outlet context={context} />
    </>
  );
}
