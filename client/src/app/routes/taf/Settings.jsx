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

// Extend DayJS with custom parsing format
dayjs.extend(customParseFormat);

/**
 * Action function to handle form submission for creating or updating a TAF.
 * Sends a POST or PUT request depending on whether an `idTAF` is provided.
 *
 * @param {Object} request - The HTTP request object.
 * @param {Object} params - Parameters passed to the action, including `idTAF`.
 * @returns {Object|null} - Redirects on success or returns null on failure.
 */
export async function action({ request, params }) {
  const data = await request.json();

  if (params.idTAF) {
    // Update an existing TAF
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
    // Create a new TAF
    return await axiosInstance
      .post(`/taf`, data)
      .then((response) => {
        toast.success("TAF créée");
        return redirect("/taf/" + response.data);
      })
      .catch(() => {
        toast.error("Erreur lors de la création du TAF");
        return null;
      });
  }
}

/**
 * DeleteTAFButton component.
 * Renders a confirmation button to delete a TAF.
 *
 * @param {number} idTAF - The ID of the TAF to delete.
 * @returns {JSX.Element} - The rendered button.
 */
const DeleteTAFButton = ({ idTAF }) => {
  const navigate = useNavigate();
  const revalidator = useRevalidator();

  /**
   * Handles the deletion of a TAF.
   * Sends a DELETE request and navigates back on success.
   */
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

// Define minimum and maximum dates for date pickers
const minDate = dayjs().subtract(1, "year");
const maxDate = dayjs().add(2, "year");

/**
 * TAFSettings component.
 * Renders a form for creating or editing a TAF, including fields for name, description, dates, and managers.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function TAFSettings() {
  const context = useOutletContext(); // Access context data from the parent route
  const navigate = useNavigate();
  const params = useParams();

  const isEditing = !!params.idTAF; // Determine if the form is in edit mode
  const taf = context?.taf; // Retrieve TAF data from the context

  const [managers, setManagers] = React.useState(
    taf?.managers.map((m) => m.id) || [], // Initialize managers state
  );

  /**
   * Validates form fields based on their name and value.
   *
   * @param {string} name - The name of the field.
   * @param {any} value - The value of the field.
   * @param {Object} otherValues - Other form values for cross-field validation.
   * @returns {string} - An error message if validation fails, otherwise an empty string.
   */
  const validateField = (name, value, otherValues) => {
    switch (name) {
      case "name":
        if (value.length < 3)
          return "Le nom doit contenir au moins 3 caractères.";
        break;
      case "endDate": {
        const compareValue = dayjs.isDayjs(value) ? value : dayjs(value);
        if (!compareValue.isValid()) {
          return "La date de fin n'est pas valide.";
        }
        if (compareValue.isBefore(dayjs(otherValues.startDate))) {
          return "La date de fin doit être après la date de début.";
        }
        if (compareValue.diff(dayjs(otherValues.startDate), "days") < 5) {
          return "La durée d'étalement de la TAF doit être au moins 5 jours.";
        }
        break;
      }
      case "startDate": {
        const compareValue2 = dayjs.isDayjs(value) ? value : dayjs(value);
        if (!compareValue2.isValid()) {
          return "La date de début n'est pas valide.";
        }
        break;
      }
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
      <Typography variant="h5" gutterBottom mt={2}>
        {isEditing ? "Paramètres de la TAF" : "Nouvelle TAF"}
      </Typography>
      <ValidatedForm
        validateField={validateField}
        onCancel={onCancel}
        actionButtons={isEditing ? <DeleteTAFButton idTAF={taf.id} /> : null}
      >
        <Stack direction="column" spacing={3}>
          {/* Name input */}
          <ValidatedInput
            name="name"
            label="Nom"
            defaultValue={taf?.name}
            margin="normal"
            fullWidth
            required
          />
          {/* Description input */}
          <ValidatedInput
            name="description"
            label="Description"
            defaultValue={taf?.description}
            multiline
            minRows={3}
            margin="normal"
            fullWidth
          />
          {/* Date pickers for start and end dates */}
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
          {/* Managers selector */}
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
