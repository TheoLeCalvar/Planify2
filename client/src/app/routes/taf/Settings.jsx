// React imports
import React from "react";
import {
  Outlet,
  redirect,
  useNavigate,
  useOutletContext,
} from "react-router-dom";

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
import { USE_MOCK_DATA } from "@/config/constants";
import UserSelector from "@/components/UserSelector";

dayjs.extend(customParseFormat);

export async function action({ request, params }) {
  const data = await request.json();

  if (USE_MOCK_DATA) {
    const delay = () => {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve("Résolu après 2 secondes");
        }, 2000); // 2000 millisecondes = 2 secondes
      });
    };

    await delay();
  } else {
    await axiosInstance.put(`/taf/${params.idTAF}`, data);
  }

  return redirect("..");
}

const minDate = dayjs().subtract(1, "year");
const maxDate = dayjs().add(2, "year");

export default function TAFSettings() {
  const context = useOutletContext();
  const navigate = useNavigate();

  const taf = context.taf;

  const [managers, setManagers] = React.useState([]);

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
      <Typography variant="h4" gutterBottom mt={2}>
        Paramètres de la TAF
      </Typography>
      <ValidatedForm validateField={validateField} onCancel={onCancel}>
        <Stack direction="column" spacing={3}>
          <ValidatedInput
            name="name"
            label="Nom"
            defaultValue={taf.name}
            margin="normal"
            fullWidth
            required
          />
          <ValidatedInput
            name="description"
            label="Description"
            defaultValue={taf.description}
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
                defaultValue={dayjs(taf.startDate)}
              >
                <DatePicker minDate={minDate} maxDate={maxDate} />
              </ValidatedInput>
              <ValidatedInput
                name="endDate"
                label="Date de fin des cours"
                defaultValue={dayjs(taf.endDate)}
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
