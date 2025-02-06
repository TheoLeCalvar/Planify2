import React from "react";
import {
  Outlet,
  useLoaderData,
  useOutletContext,
  useFetcher,
} from "react-router-dom";
import BlockManager from "../../../features/lessons/components/BlockManager";
import { useEffect, useState } from "react";
import SaveIcon from "@mui/icons-material/Save";
import { Fab } from "@mui/material";
import { LessonsContext } from "../../../hooks/LessonsContext";
import { USE_MOCK_DATA } from "../../../constants";
import axiosInstance from "../../../config/axiosConfig";

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
  if (USE_MOCK_DATA) {
    const mockData = [
      {
        id: 1,
        title: "Bloc 1",
        description: "Description du bloc 1",
        lessons: [
          {
            id: 1,
            title: "Cours 1",
            description: "Description du cours 1",
            lecturers: [1, 2],
          },
          {
            id: 2,
            title: "Cours 2",
            description: "Description du cours 2",
            lecturers: [1],
          },
        ],
        dependencies: [],
      },
      {
        id: 2,
        title: "Bloc 2",
        description: "Description du bloc 2",
        lessons: [
          {
            id: 3,
            title: "Cours 3",
            description: "Description du cours 3",
            lecturers: [3],
          },
          {
            id: 4,
            title: "Cours 4",
            description: "Description du cours 4",
            lecturers: [3, 4],
          },
        ],
        dependencies: [1],
      },
      {
        id: 3,
        title: "Bloc 3",
        description: "Description du bloc 3",
        lessons: [
          {
            id: 5,
            title: "Cours 5",
            description: "Description du cours 5",
          },
          {
            id: 6,
            title: "Cours 6",
            description: "Description du cours 6",
          },
        ],
        dependencies: [1, 2],
      },
    ];

    const users = [
      { id: 1, name: "John Doe", alreadySelected: false },
      { id: 2, name: "Jacques Noyé", alreadySelected: true },
      { id: 3, name: "Foo Bar", alreadySelected: true },
      { id: 4, name: "Baz Qux", alreadySelected: false },
    ];

    return { lessons: mockData, users };
  }

  const [lessons, users] = await Promise.all([
    axiosInstance.get(`/ue/${params.idUE}/lesson`),
    axiosInstance.get(`/users?tafId=${params.idTAF}`),
  ]);
  return { lessons: lessons.data, users: users.data };
}

export async function action({ request, params }) {
  const data = await request.json();

  if (USE_MOCK_DATA) {
    console.log(data);
    await new Promise((res) => setTimeout(res, 1000));
    return { ok: true };
  }

  const result = await axiosInstance.put(`/ue/${params.idUE}/lesson`, data);
  return { ok: result.status === 200 };
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
        disabled={!!dependencyError || busy}
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
