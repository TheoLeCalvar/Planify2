// React imports
import React, { useEffect, useState } from "react";
import {
  Outlet,
  useLoaderData,
  useOutletContext,
  useFetcher,
  redirect,
} from "react-router-dom"; // React Router hooks for data loading, context, and navigation

// Custom components
import BlockManager from "@/features/lessons/components/BlockManager"; // Component for managing lesson blocks
import { LessonsContext } from "@/hooks/LessonsContext"; // Context for managing lessons data

// Material-UI imports
import SaveIcon from "@mui/icons-material/Save"; // Save icon for the floating action button
import { Fab } from "@mui/material"; // Floating action button component

// Axios instance for API requests
import axiosInstance from "@/config/axiosConfig";

// Notifications
import { toast } from "react-toastify"; // Notifications for success and error messages

// Styles for the floating action button
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

/**
 * Loader function to fetch lessons and users data for a specific UE.
 * Fetches lessons associated with the UE and users associated with the TAF.
 *
 * @param {Object} params - Parameters passed to the loader, including `idUE` and `idTAF`.
 * @returns {Promise<Object>} - A promise that resolves to an object containing lessons and users data.
 */
export async function loader({ params }) {
  const [lessons, users] = await Promise.all([
    axiosInstance.get(`/ue/${params.idUE}/lesson`), // Fetch lessons for the UE
    axiosInstance.get(`/users?tafId=${params.idTAF}`), // Fetch users for the TAF
  ]);
  return { lessons: lessons.data, users: users.data }; // Return the fetched data
}

/**
 * Action function to handle form submission for updating lessons.
 * Sends a PUT request to update the lessons of a specific UE.
 *
 * @param {Object} request - The HTTP request object.
 * @param {Object} params - Parameters passed to the action, including `idUE`.
 * @returns {Object|null} - Redirects on success or returns null on failure.
 */
export async function action({ request, params }) {
  const data = await request.json(); // Parse the request body as JSON

  return await axiosInstance
    .put(`/ue/${params.idUE}/lesson`, data) // Update lessons for the UE
    .then(() => {
      toast.success("Cours de l'UE mis à jour"); // Show success notification
      return redirect(".."); // Redirect to the parent route
    })
    .catch(() => {
      toast.error("Erreur lors de la mise à jour des cours"); // Show error notification
      return null; // Return null on failure
    });
}

/**
 * UELessons component.
 * This component displays and manages lessons for a specific UE.
 * It provides a form-like interface for editing lessons and saving changes.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function UELessons() {
  const { lessons: data, users } = useLoaderData(); // Load lessons and users data from the loader
  const context = useOutletContext(); // Access context data from the parent route
  const fetcher = useFetcher(); // Hook for programmatic form submission

  const busy = fetcher.state !== "idle"; // Determine if a fetcher action is in progress

  // State for managing lessons data
  const [lessonsData, setLessonsData] = useState(data ?? []);
  const [dependencyError, setDependencyError] = useState(null); // State for dependency errors
  const [lecturersList, setLecturersList] = useState(users ?? []); // State for the list of lecturers

  // Update the lecturers list when the users data changes
  useEffect(() => {
    setLecturersList(users);
  }, [users]);

  return (
    <>
      {/* Header section */}
      <h1>Cours</h1>

      {/* Provide lessons context to child components */}
      <LessonsContext.Provider value={{ lecturersList, setLecturersList }}>
        <BlockManager
          lessonsData={lessonsData} // Pass lessons data to the BlockManager component
          setLessonsData={setLessonsData} // Function to update lessons data
          dependencyError={dependencyError} // Pass dependency error state
          setDependencyError={setDependencyError} // Function to update dependency error state
        />
      </LessonsContext.Provider>

      {/* Floating action button for saving lessons */}
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
          !!dependencyError || // Disable if there is a dependency error
          busy || // Disable if a fetcher action is in progress
          lessonsData.some((block) => block.lessons.length === 0) || // Disable if any block has no lessons
          lessonsData.some((block) => block.lessons.length > 3) // Disable if any block has more than 3 lessons
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

      {/* Render nested routes with the same context */}
      <Outlet context={context} />
    </>
  );
}
