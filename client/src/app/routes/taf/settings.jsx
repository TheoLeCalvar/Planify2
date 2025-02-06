import React from "react";
import {
  Outlet,
  redirect,
  useNavigate,
  useOutletContext,
} from "react-router-dom";
import ValidatedInput from "../../../components/ValidatedInput";
import ValidatedForm from "../../../components/ValidatedForm";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import axiosInstance from "../../../config/axiosConfig";

import "dayjs/locale/fr";
import { Stack } from "@mui/material";
import { USE_MOCK_DATA } from "../../../constants";

dayjs.extend(customParseFormat);

export async function action({ request, params }) {
  //TODO: Update section with backend call

  const formData = await request.formData();
  const updates = Object.fromEntries(formData);

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
    const response = await axiosInstance.put(`/taf/${params.idTAF}`, updates);
  }

  return redirect("..");
}

const minDate = dayjs().subtract(1, "year");
const maxDate = dayjs().add(2, "year");

export default function TAFSettings() {
  const context = useOutletContext();
  const navigate = useNavigate();

  const taf = context.taf;

  const validateField = (name, value, otherValues) => {
    switch (name) {
      case "name":
        if (value.length < 3)
          return "Le nom doit contenir au moins 3 caractères.";
        break;
      default:
        return "";
      case "endDate": {
        const compareValue = dayjs.isDayjs(value)
          ? value
          : dayjs(value, "DD/MM/YYYY");
        if (!compareValue.isValid()) {
          return "La date de fin n'est pas valide.";
        }
        if (compareValue.isBefore(dayjs(otherValues.startDate, "DD/MM/YYYY"))) {
          return "La date de fin doit être après la date de début.";
        }
        break;
      }
      case "startDate": {
        const compareValue2 = dayjs.isDayjs(value)
          ? value
          : dayjs(value, "DD/MM/YYYY");
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
      <ValidatedForm validateField={validateField} onCancel={onCancel}>
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
          <Stack direction="row" spacing={2}>
            <ValidatedInput
              name="startDate"
              label="Date de début des cours"
              defaultValue={dayjs(taf.startDate, "YYYY-MM-DD")}
            >
              <DatePicker minDate={minDate} maxDate={maxDate} />
            </ValidatedInput>
            <ValidatedInput
              name="endDate"
              label="Date de fin des cours"
              defaultValue={dayjs(taf.endDate, "YYYY-MM-DD")}
            >
              <DatePicker minDate={minDate} maxDate={maxDate} />
            </ValidatedInput>
          </Stack>
        </LocalizationProvider>
      </ValidatedForm>
      <Outlet context={context} />
    </>
  );
}
