// React imports
import React from "react";
import { toast } from "react-toastify";
import { redirect } from "react-router-dom";

// Data imports
import axiosInstance from "@/config/axiosConfig";

// Date imports
import dayjs from "dayjs";

// Custom components
import SolverConfigComponent from "@/features/solver-config/components/SolverConfig";

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

export default function SolverConfig() {
  return <SolverConfigComponent />;
}
