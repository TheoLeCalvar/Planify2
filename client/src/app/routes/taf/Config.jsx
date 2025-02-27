import React from "react";
import axiosInstance from "@/config/axiosConfig";
import {
  redirect,
  useLoaderData,
  useNavigate,
  useOutletContext,
  useParams,
  useRevalidator,
} from "react-router-dom";
import {
  IconButton,
  MenuItem,
  Select,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import ValidatedForm from "@/components/ValidatedForm";
import { toast } from "react-toastify";
import PropTypes from "prop-types";
import ConfirmationButton from "@/components/ConfirmationButton";
import ValidatedInput from "@/components/ValidatedInput";
import { LocalizationProvider, TimeField } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import InfoIcon from "@mui/icons-material/Info";
import dayjs from "dayjs";

export async function loader({ params }) {
  if (params.idConfig) {
    const response = await axiosInstance.get(`/config/${params.idConfig}`);
    const responseData = response.data;
    Object.keys(responseData).forEach((key) => {
      responseData[key] = String(responseData[key]);
    });
    return response.data;
  } else {
    return {};
  }
}

const minBreakTime = dayjs().set("hour", 8).startOf("hour");
const maxBreakTime = dayjs().set("hour", 18).startOf("hour");

export async function action({ request, params }) {
  const rawData = await request.json();

  const data = !params.idUE
    ? {
        name: rawData.name,
        globalUnavailability: rawData.globalUnavailability === "true",
        weightGlobalUnavailability: parseInt(
          rawData.weightGlobalUnavailability,
        ),
        lecturersUnavailability: rawData.lecturersUnavailability === "true",
        weightLecturersUnavailability: parseInt(
          rawData.weightLecturersUnavailability,
        ),
        synchronise: rawData.synchronise === "true",
        UEInterlacing: rawData.UEInterlacing === "true",
        middayBreak: rawData.middayBreak === "true",
        startMiddayBreak: dayjs(rawData.startMiddayBreak).format("HH:mm"),
        endMiddayBreak: dayjs(rawData.endMiddayBreak).format("HH:mm"),
        middayGrouping: rawData.middayGrouping === "true",
        weightMiddayGrouping: parseInt(rawData.weightMiddayGrouping),
        lessonBalancing: rawData.lessonBalancing === "true",
        weightLessonBalancing: parseInt(rawData.weightLessonBalancing),
        lessonGrouping: rawData.lessonGrouping === "true",
        weightLessonGrouping: parseInt(rawData.weightLessonGrouping),
        weightMaxTimeWithoutLesson: parseInt(
          rawData.weightMaxTimeWithoutLesson,
        ),
      }
    : {
        ue: parseInt(params.idUE),
        maxTimeWithoutLesson: rawData.maxTimeWithoutLesson === "true",
        maxTimeWLDuration: parseInt(rawData.maxTimeWLDuration),
        maxTimeWLUnitInWeeks: rawData.maxTimeWLUnitInWeeks === "true",
        lessonCountInWeek: rawData.lessonCountInWeek === "true",
        minLessonCountInWeek: parseInt(rawData.minLessonCountInWeek),
        maxLessonCountInWeek: parseInt(rawData.maxLessonCountInWeek),
        spreading: rawData.spreading === "true",
        minSpreading: parseInt(rawData.minSpreading),
        maxSpreading: parseInt(rawData.maxSpreading),
      };

  if (params.idConfig) {
    return await axiosInstance
      .patch(`/config/${params.idConfig}`, data)
      .then(() => {
        toast.success("Configuration mise à jour");
        return redirect("..");
      })
      .catch(() => {
        toast.error("Erreur lors de la mise à jour de la configuration");
        return null;
      });
  } else {
    return await axiosInstance
      .post(`/taf/${params.idTAF}/configs`, data)
      .then(() => {
        toast.success("Configuration créée");
        return redirect("..");
      })
      .catch(() => {
        toast.error("Erreur lors de la création de la configuration");
        return null;
      });
  }
}

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

export default function TAFConfig() {
  const data = useLoaderData();
  const params = useParams();
  const navigate = useNavigate();
  const context = useOutletContext();

  const isEditing = !!params.idConfig;

  const onCancel = () => {
    navigate("..");
  };

  const validateField = (name, value, otherValues) => {
    switch (name) {
      case "name":
        if (value.length < 3)
          return "Le nom doit contenir au moins 3 caractères.";
        break;
      case "globalUnavailability":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour la disponibilité globale";
        break;
      case "weightGlobalUnavailability":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "Le poids doit être un nombre positif";
        break;
      case "lecturersUnavailability":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour la disponibilité des intervenants";
        break;
      case "weightLecturersUnavailability":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "Le poids doit être un nombre positif";
        break;
      case "synchronise":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour la synchronisation";
        break;
      case "UEInterlacing":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour l'entrelacement";
        break;
      case "middayBreak":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour la pause de midi";
        break;
      case "startMiddayBreak":
        if (!dayjs(value, "HH:mm").isValid())
          return "Heure de début de pause invalide";
        if (dayjs(value, "HH:mm").isBefore(minBreakTime))
          return "L'heure de début de pause doit être après 8h00";
        if (dayjs(value, "HH:mm").isAfter(dayjs(otherValues.endMiddayBreak)))
          return "L'heure de début de pause doit être avant l'heure de fin";
        break;
      case "endMiddayBreak":
        if (!dayjs(value, "HH:mm").isValid())
          return "Heure de fin de pause invalide";
        if (dayjs(value, "HH:mm").isAfter(maxBreakTime))
          return "L'heure de fin de pause doit être avant 18h00";
        if (dayjs(value, "HH:mm").isBefore(dayjs(otherValues.startMiddayBreak)))
          return "L'heure de fin de pause doit être après l'heure de début";
        break;
      case "middayGrouping":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour le groupement de midi";
        break;
      case "weightMiddayGrouping":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "Le poids doit être un nombre positif";
        break;
      case "lessonBalancing":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour l'équilibrage des cours";
        break;
      case "weightLessonBalancing":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "Le poids doit être un nombre positif";
        break;
      case "lessonGrouping":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour le groupement des cours";
        break;
      case "weightLessonGrouping":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "Le poids doit être un nombre positif";
        break;
      case "weightMaxTimeWithoutLesson":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "Le poids doit être un nombre positif";
        break;
      case "maxTimeWithoutLesson":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour la durée sans cours";
        break;
      case "maxTimeWLDuration":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "La valeur doit être un nombre positif";
        break;
      case "maxTimeWLUnitInWeeks":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour la durée sans cours";
        break;
      case "lessonCountInWeek":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour le nombre de cours par semaine";
        break;
      case "minLessonCountInWeek":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "La valeur doit être un nombre positif";
        if (parseInt(value) > parseInt(otherValues.maxLessonCountInWeek))
          return "La valeur minimale doit être inférieure à la valeur maximale";
        break;
      case "maxLessonCountInWeek":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "La valeur doit être un nombre positif";
        if (parseInt(value) < parseInt(otherValues.minLessonCountInWeek))
          return "La valeur maximale doit être supérieure à la valeur minimale";
        break;
      case "spreading":
        if (value !== "true" && value !== "false")
          return "Valeur invalide pour la durée de l'UE";
        break;
      case "minSpreading":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "La valeur doit être un nombre positif";
        if (parseInt(value) > parseInt(otherValues.maxSpreading))
          return "La valeur minimale doit être inférieure à la valeur maximale";
        break;
      case "maxSpreading":
        if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
          return "La valeur doit être un nombre positif";
        if (parseInt(value) < parseInt(otherValues.minSpreading))
          return "La valeur maximale doit être supérieure à la valeur minimale";
        break;

      default:
        return "";
    }
    return ""; // Pas d'erreur
  };

  return (
    <>
      <Typography variant="h5" gutterBottom mt={2}>
        {isEditing ? "Editer la configuration" : "Nouvelle configuration"}
      </Typography>
      <ValidatedForm
        validateField={validateField}
        onCancel={onCancel}
        actionButtons={
          isEditing ? <DeleteConfigButton idConfig={params.idConfig} /> : null
        }
      >
        <Stack spacing={3} direction="column">
          {!context?.ue ? (
            <>
              <Stack direction="row" alignItems="center" spacing={1}>
                <Tooltip title="Seules les contraintes globales à la TAF sont affichées. Les contraintes relatives aux UE sont configurées dans les paramètres de chaque UE.">
                  <IconButton size="small">
                    <InfoIcon fontSize="inherit" />
                  </IconButton>
                </Tooltip>
                <Typography variant="h6">
                  Contraintes relatives à la TAF
                </Typography>
              </Stack>

              <ValidatedInput
                name="name"
                label="Nom de la configuration"
                defaultValue={data?.name}
                required
              />

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Disponibilité globale</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Les cours sont positionnés en respectant la disponibilité des créneaux globale à la TAF"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="globalUnavailability"
                  defaultValue={data?.globalUnavailability || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Poids :</Typography>
                <ValidatedInput
                  name="weightGlobalUnavailability"
                  label="Poids"
                  defaultValue={data?.weightGlobalUnavailability | "30"}
                  type="number"
                  required
                />
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">
                  Disponibilité des intervenants
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Les cours sont positionnés en respectant la disponibilité des intervenants"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="lecturersUnavailability"
                  defaultValue={data?.lecturersUnavailability || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Poids :</Typography>
                <ValidatedInput
                  name="weightLecturersUnavailability"
                  label="Poids"
                  defaultValue={data?.weightLecturersUnavailability || "19"}
                  type="number"
                  required
                />
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">
                  Synchronisation avec d&apos;autres TAFs
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Les cours synchronisés seront placés sur les mêmes créneaux que les cours homologues de la TAF synchronisée"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="synchronise"
                  defaultValue={data?.synchronise || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">{"Entrelacement"}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {"L'entrelacement de cours d'UE différentes sera évité"}
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="UEInterlacing"
                  defaultValue={data?.UEInterlacing || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Pause midi</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Faut-il prévoir une pause de midi de 1 créneau entre les cours du matin et de l'après-midi ?"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="middayBreak"
                  defaultValue={data?.middayBreak || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Heure de début :</Typography>
                <LocalizationProvider
                  dateAdapter={AdapterDayjs}
                  adapterLocale="fr"
                >
                  <ValidatedInput
                    name="startMiddayBreak"
                    defaultValue={
                      data?.startMiddayBreak
                        ? dayjs(data?.startMiddayBreak, "HH:mm")
                        : dayjs("11:30", "HH:mm")
                    }
                    required
                  >
                    <TimeField
                      format="HH:mm"
                      minTime={minBreakTime}
                      maxTime={maxBreakTime}
                    />
                  </ValidatedInput>
                  <Typography variant="body1">Heure de fin :</Typography>
                  <ValidatedInput
                    name="endMiddayBreak"
                    defaultValue={
                      data?.endMiddayBreak
                        ? dayjs(data?.endMiddayBreak, "HH:mm")
                        : dayjs("13:45", "HH:mm")
                    }
                    required
                  >
                    <TimeField
                      format="HH:mm"
                      minTime={minBreakTime}
                      maxTime={maxBreakTime}
                    />
                  </ValidatedInput>
                </LocalizationProvider>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Groupement des cours</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Les cours seront groupés pour éviter les trous dans les emplois du temps, de préférence vers le milieu de la journée"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="middayGrouping"
                  defaultValue={data?.middayGrouping || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Poids :</Typography>
                <ValidatedInput
                  name="weightMiddayGrouping"
                  label="Poids"
                  defaultValue={data?.weightMiddayGrouping | "1"}
                  type="number"
                  required
                />
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Equilibrage des cours</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Les nombre de cours par jour sera équilibré pour éviter les journées surchargées ou vides"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="lessonBalancing"
                  defaultValue={data?.lessonBalancing || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Poids :</Typography>
                <ValidatedInput
                  name="weightLessonBalancing"
                  label="Poids"
                  defaultValue={data?.weightLessonBalancing | "2"}
                  type="number"
                  required
                />
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Groupement des cours</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Les cours d'une même UE seront préférentiellement groupés (2 ou 3 cours consécutifs)"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="lessonGrouping"
                  defaultValue={data?.lessonGrouping || "true"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Poids :</Typography>
                <ValidatedInput
                  name="weightLessonGrouping"
                  label="Poids"
                  defaultValue={data?.weightLessonGrouping | "5"}
                  type="number"
                  required
                />
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Durée sans cours</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Temps maximum entre deux cours d'une même UE. Cette contrainte est configurée indiviellement pour chaque UE. Seul le poids est commun à toutes les UE"
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Poids :</Typography>
                <ValidatedInput
                  name="weightMaxTimeWithoutLesson"
                  label="Poids"
                  defaultValue={data?.weightMaxTimeWithoutLesson | "11"}
                  type="number"
                  required
                />
              </Stack>
            </>
          ) : (
            <>
              <Stack direction="row" alignItems="center" spacing={1}>
                <Tooltip title="Seules les contraintes spécifique à cette UE sont affichées. Les contraintes globales à la TAF sont configurées dans les paramètres de la TAF.">
                  <IconButton size="small">
                    <InfoIcon fontSize="inherit" />
                  </IconButton>
                </Tooltip>
                <Typography variant="h6">
                  {"Contraintes relatives à l'UE"}
                </Typography>
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Durée sans cours</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Temps maximum entre deux cours de l'UE. 0 semaine signifique qu'un cours par semaine est souhaitable. Le poids commun de cette contrainte est configurable dans les paramètres de la TAF."
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="maxTimeWithoutLesson"
                  defaultValue={
                    data?.constraintsOfUEs?.maxTimeWithoutLesson || "true"
                  }
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Valeur :</Typography>
                <ValidatedInput
                  name="maxTimeWLDuration"
                  label="Valeur"
                  defaultValue={data?.constraintsOfUEs?.maxTimeWLDuration | "1"}
                  type="number"
                  required
                />
                <ValidatedInput
                  name="maxTimeWLUnitInWeeks"
                  defaultValue={
                    data?.constraintsOfUEs?.maxTimeWLUnitInWeeks || "true"
                  }
                  required
                >
                  <Select>
                    <MenuItem value="true">Semaine(s)</MenuItem>
                    <MenuItem value="false">Jour(s)</MenuItem>
                  </Select>
                </ValidatedInput>
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">Cours par semaines</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Nombre minimal et maximal de cours de cette UE par semaine."
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="lessonCountInWeek"
                  defaultValue={
                    data?.constraintsOfUEs?.lessonCountInWeek || "true"
                  }
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Minimum :</Typography>
                <ValidatedInput
                  name="minLessonCountInWeek"
                  label="Minimum"
                  defaultValue={
                    data?.constraintsOfUEs?.minLessonCountInWeek | "0"
                  }
                  type="number"
                  required
                />
                <Typography variant="body1">Maximum :</Typography>
                <ValidatedInput
                  name="maxLessonCountInWeek"
                  label="Maximum"
                  defaultValue={
                    data?.constraintsOfUEs?.maxLessonCountInWeek | "6"
                  }
                  type="number"
                  required
                />
              </Stack>

              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="h6">{"Durée de l'UE"}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {
                    "Nombre minimal et maximal de semaines pour placer tous les cours de l'UE."
                  }
                </Typography>
              </Stack>
              <Stack direction="row" spacing={3} alignItems={"center"}>
                <Typography variant="body1">Statut :</Typography>
                <ValidatedInput
                  name="spreading"
                  defaultValue={data?.constraintsOfUEs?.spreading || "false"}
                  required
                >
                  <Select>
                    <MenuItem value="true">Activée</MenuItem>
                    <MenuItem value="false">Désactivée</MenuItem>
                  </Select>
                </ValidatedInput>
                <Typography variant="body1">Minimum :</Typography>
                <ValidatedInput
                  name="minSpreading"
                  label="Minimum"
                  defaultValue={data?.constraintsOfUEs?.minSpreading | "2"}
                  type="number"
                  required
                />
                <Typography variant="body1">Maximum :</Typography>
                <ValidatedInput
                  name="maxSpreading"
                  label="Maximum"
                  defaultValue={data?.constraintsOfUEs?.maxSpreading | "12"}
                  type="number"
                  required
                />
              </Stack>
            </>
          )}
        </Stack>
      </ValidatedForm>
    </>
  );
}
