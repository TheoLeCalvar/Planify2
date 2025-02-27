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
import {
  globalConfigSections,
  ueConfigSections,
} from "@/features/solver-config/utils/solverConfig";

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

const rawDataFieldToType = (value, type) => {
  switch (type) {
    case "boolean":
      return value === "true";
    case "number":
      return parseInt(value, 10);
    case "time":
      return dayjs(value).format("HH:mm");
    default:
      return value;
  }
};

const rawDataToTypedData = (rawData, configSections) =>
  configSections.reduce((acc, section) => {
    section.fields.forEach((field) => {
      acc[field.name] = rawDataFieldToType(rawData[field.name], field.type);
    });
    return acc;
  }, {});

export async function action({ request, params }) {
  const rawData = await request.json();

  let data;
  if (!params.idUE) {
    data = rawDataToTypedData(rawData, globalConfigSections);
  } else {
    data = {
      ue: parseInt(params.idUE),
      ...rawDataToTypedData(rawData, ueConfigSections),
    };
  }

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
