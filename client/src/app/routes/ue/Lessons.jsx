import React from "react";
import {
  Outlet,
  useLoaderData,
  useOutletContext,
  useFetcher,
  redirect,
} from "react-router-dom";
import BlockManager from "@/features/lessons/components/BlockManager";
import { useEffect, useState } from "react";
import SaveIcon from "@mui/icons-material/Save";
import { Fab } from "@mui/material";
import { LessonsContext } from "@/hooks/LessonsContext";
import axiosInstance from "@/config/axiosConfig";
import { toast } from "react-toastify";

const styles = {
  fab: {
    position: "fixed",
    right: 32,
    bottom: 32,
  },
  saveIcon: {
    mr: 1,
  },
};

export async function loader({ params }) {
  const [lessons, users] = await Promise.all([
    axiosInstance.get(`/ue/${params.idUE}/lesson`),
    axiosInstance.get(`/users?tafId=${params.idTAF}`),
  ]);
  return { lessons: lessons.data, users: users.data };
}

export async function action({ request, params }) {
  const data = await request.json();

  return await axiosInstance
    .put(`/ue/${params.idUE}/lesson`, data)
    .then(() => {
      toast.success("Cours de l'UE mis à jour");
      return redirect("..");
    })
    .catch(() => {
      toast.error("Erreur lors de la mise à jour des cours");
      return null;
    });
}

export default function UELessons() {
  const { lessons: data, users } = useLoaderData();
  const context = useOutletContext();
  const fetcher = useFetcher();

  const busy = fetcher.state !== "idle";

  const [lessonsData, setLessonsData] = useState(data ?? []);
  const [dependencyError, setDependencyError] = useState(null);
  const [lecturersList, setLecturersList] = useState(users ?? []);

  useEffect(() => {
    setLecturersList(users);
  }, [users]);

  return (
    <>
      <h1>Cours</h1>
      <LessonsContext.Provider value={{ lecturersList, setLecturersList }}>
        <BlockManager
          lessonsData={lessonsData}
          setLessonsData={setLessonsData}
          dependencyError={dependencyError}
          setDependencyError={setDependencyError}
        />
      </LessonsContext.Provider>
      <Fab
        variant="extended"
        size="large"
        color="primary"
        onClick={() =>
          fetcher.submit(lessonsData, {
            encType: "application/json",
            method: "post",
          })
        }
        disabled={
          !!dependencyError ||
          busy ||
          lessonsData.some((block) => block.lessons.length === 0) ||
          lessonsData.some((block) => block.lessons.length > 3)
        }
        sx={styles.fab}
      >
        {busy ? (
          <>
            <SaveIcon sx={styles.saveIcon} />
            Sauvegarde...
          </>
        ) : (
          <>
            <SaveIcon sx={styles.saveIcon} />
            Sauvegarder
          </>
        )}
      </Fab>

      <Outlet context={context} />
    </>
  );
}
