// Date imports
import dayjs from "dayjs";

// Local imports
import { parseTime } from "./parseTime";

const minBreakTime = dayjs().set("hour", 8).startOf("hour");
const maxBreakTime = dayjs().set("hour", 18).startOf("hour");

const globalConfigSections = [
  {
    title: "Informations générales",
    description: "",
    fields: [
      {
        name: "name",
        label: "Nom de la configuration",
        type: "text",
        defaultValue: "",
        required: true,
        validate: (value) =>
          value.length < 3 ? "Le nom doit contenir au moins 3 caractères." : "",
      },
    ],
  },
  {
    title: "Disponibilité globale",
    description:
      "Les cours sont positionnés en respectant la disponibilité des créneaux globale à la TAF",
    fields: [
      {
        name: "globalUnavailability",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour la disponibilité globale"
            : "",
      },
      {
        name: "weightGlobalUnavailability",
        label: "Poids",
        type: "number",
        defaultValue: "30",
        required: true,
        validate: (value) =>
          Number.isNaN(parseInt(value)) || parseInt(value) < 0
            ? "Le poids doit être un nombre positif"
            : "",
      },
    ],
  },
  {
    title: "Disponibilité des intervenants",
    description:
      "Les cours sont positionnés en respectant la disponibilité des intervenants",
    fields: [
      {
        name: "lecturersUnavailability",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour la disponibilité des intervenants"
            : "",
      },
      {
        name: "weightLecturersUnavailability",
        label: "Poids",
        type: "number",
        defaultValue: "19",
        required: true,
        validate: (value) =>
          Number.isNaN(parseInt(value)) || parseInt(value) < 0
            ? "Le poids doit être un nombre positif"
            : "",
      },
    ],
  },
  {
    title: "Synchronisation avec d'autres TAFs",
    description:
      "Les cours synchronisés seront placés sur les mêmes créneaux que les cours homologues de la TAF synchronisée",
    fields: [
      {
        name: "synchronise",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour la synchronisation"
            : "",
      },
    ],
  },
  {
    title: "Entrelacement",
    description: "L'entrelacement de cours d'UE différentes sera évité",
    fields: [
      {
        name: "UEInterlacing",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour l'entrelacement"
            : "",
      },
    ],
  },
  {
    title: "Pause midi",
    description:
      "Faut-il prévoir une pause de midi de 1 créneau entre les cours du matin et de l'après-midi ?",
    fields: [
      {
        name: "middayBreak",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour la pause de midi"
            : "",
      },
      {
        name: "startMiddayBreak",
        label: "Heure de début",
        type: "time",
        defaultValue: "11:30",
        required: true,
        minBreakTime,
        maxBreakTime,
        validate: (value, values) => {
          const startTime = parseTime(value, "HH:mm");
          if (!startTime.isValid()) return "Heure de début de pause invalide";
          if (startTime.isBefore(minBreakTime))
            return "L'heure de début de pause doit être après 8h00";
          if (
            values.endMiddayBreak &&
            startTime.isAfter(parseTime(values.endMiddayBreak, "HH:mm"))
          )
            return "L'heure de début de pause doit être avant l'heure de fin";
          return "";
        },
      },
      {
        name: "endMiddayBreak",
        label: "Heure de fin",
        type: "time",
        defaultValue: "13:45",
        required: true,
        minBreakTime,
        maxBreakTime,
        validate: (value, values) => {
          const endTime = parseTime(value, "HH:mm");
          if (!endTime.isValid()) return "Heure de fin de pause invalide";
          if (endTime.isAfter(maxBreakTime))
            return "L'heure de fin de pause doit être avant 18h00";
          if (
            values.startMiddayBreak &&
            endTime.isBefore(parseTime(values.startMiddayBreak, "HH:mm"))
          )
            return "L'heure de fin de pause doit être après l'heure de début";
          return "";
        },
      },
    ],
  },
  {
    title: "Groupement des cours (midi)",
    description:
      "Les cours seront groupés pour éviter les trous dans les emplois du temps, de préférence vers le milieu de la journée",
    fields: [
      {
        name: "middayGrouping",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour le groupement de midi"
            : "",
      },
      {
        name: "weightMiddayGrouping",
        label: "Poids",
        type: "number",
        defaultValue: "1",
        required: true,
        validate: (value) =>
          Number.isNaN(parseInt(value)) || parseInt(value) < 0
            ? "Le poids doit être un nombre positif"
            : "",
      },
    ],
  },
  {
    title: "Equilibrage des cours",
    description:
      "Le nombre de cours par jour sera équilibré pour éviter les journées surchargées ou vides",
    fields: [
      {
        name: "lessonBalancing",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour l'équilibrage des cours"
            : "",
      },
      {
        name: "weightLessonBalancing",
        label: "Poids",
        type: "number",
        defaultValue: "2",
        required: true,
        validate: (value) =>
          Number.isNaN(parseInt(value)) || parseInt(value) < 0
            ? "Le poids doit être un nombre positif"
            : "",
      },
    ],
  },
  {
    title: "Groupement des cours (UE)",
    description:
      "Les cours d'une même UE seront préférentiellement groupés (2 ou 3 cours consécutifs)",
    fields: [
      {
        name: "lessonGrouping",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour le groupement des cours"
            : "",
      },
      {
        name: "weightLessonGrouping",
        label: "Poids",
        type: "number",
        defaultValue: "5",
        required: true,
        validate: (value) =>
          Number.isNaN(parseInt(value)) || parseInt(value) < 0
            ? "Le poids doit être un nombre positif"
            : "",
      },
    ],
  },
  {
    title: "Durée sans cours",
    description:
      "Temps maximum entre deux cours d'une même UE. Cette contrainte est configurée individuellement pour chaque UE. Seul le poids est commun à toutes les UE",
    fields: [
      {
        name: "weightMaxTimeWithoutLesson",
        label: "Poids",
        type: "number",
        defaultValue: "11",
        required: true,
        validate: (value) =>
          Number.isNaN(parseInt(value)) || parseInt(value) < 0
            ? "Le poids doit être un nombre positif"
            : "",
      },
    ],
  },
];

const ueConfigSections = [
  {
    title: "Durée sans cours",
    description:
      "Temps maximum entre deux cours de l'UE. 0 semaine signifie qu'un cours par semaine est souhaitable. Le poids commun de cette contrainte est configurable dans les paramètres de la TAF.",
    fields: [
      {
        name: "maxTimeWithoutLesson",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour la durée sans cours"
            : "",
      },
      {
        name: "maxTimeWLDuration",
        label: "Valeur",
        type: "number",
        defaultValue: "1",
        required: true,
        validate: (value) =>
          Number.isNaN(parseInt(value)) || parseInt(value) < 0
            ? "La valeur doit être un nombre positif"
            : "",
      },
      {
        name: "maxTimeWLUnitInWeeks",
        label: "Unité",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Semaine(s)" },
          { value: "false", label: "Jour(s)" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour l'unité de durée"
            : "",
      },
    ],
  },
  {
    title: "Cours par semaines",
    description: "Nombre minimal et maximal de cours de cette UE par semaine.",
    fields: [
      {
        name: "lessonCountInWeek",
        label: "Statut",
        type: "select",
        defaultValue: "true",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour le nombre de cours par semaine"
            : "",
      },
      {
        name: "minLessonCountInWeek",
        label: "Minimum",
        type: "number",
        defaultValue: "0",
        required: true,
        validate: (value, values) => {
          if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
            return "La valeur doit être un nombre positif";
          if (
            values.maxLessonCountInWeek &&
            parseInt(value) > parseInt(values.maxLessonCountInWeek)
          )
            return "La valeur minimale doit être inférieure à la valeur maximale";
          return "";
        },
      },
      {
        name: "maxLessonCountInWeek",
        label: "Maximum",
        type: "number",
        defaultValue: "6",
        required: true,
        validate: (value, values) => {
          if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
            return "La valeur doit être un nombre positif";
          if (
            values.minLessonCountInWeek &&
            parseInt(value) < parseInt(values.minLessonCountInWeek)
          )
            return "La valeur maximale doit être supérieure à la valeur minimale";
          return "";
        },
      },
    ],
  },
  {
    title: "Durée de l'UE",
    description:
      "Nombre minimal et maximal de semaines pour placer tous les cours de l'UE.",
    fields: [
      {
        name: "spreading",
        label: "Statut",
        type: "select",
        defaultValue: "false",
        required: true,
        options: [
          { value: "true", label: "Activée" },
          { value: "false", label: "Désactivée" },
        ],
        validate: (value) =>
          value !== "true" && value !== "false"
            ? "Valeur invalide pour la durée de l'UE"
            : "",
      },
      {
        name: "minSpreading",
        label: "Minimum",
        type: "number",
        defaultValue: "2",
        required: true,
        validate: (value, values) => {
          if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
            return "La valeur doit être un nombre positif";
          if (
            values.maxSpreading &&
            parseInt(value) > parseInt(values.maxSpreading)
          )
            return "La valeur minimale doit être inférieure à la valeur maximale";
          return "";
        },
      },
      {
        name: "maxSpreading",
        label: "Maximum",
        type: "number",
        defaultValue: "12",
        required: true,
        validate: (value, values) => {
          if (Number.isNaN(parseInt(value)) || parseInt(value) < 0)
            return "La valeur doit être un nombre positif";
          if (
            values.minSpreading &&
            parseInt(value) < parseInt(values.minSpreading)
          )
            return "La valeur maximale doit être supérieure à la valeur minimale";
          return "";
        },
      },
    ],
  },
];

export { globalConfigSections, ueConfigSections };
