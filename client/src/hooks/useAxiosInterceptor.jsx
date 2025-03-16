// Axios instance import
import axiosInstance from "../config/axiosConfig";

// React Router imports
import { useNavigate } from "react-router-dom";

// React imports
import { useEffect } from "react";

/**
 * useAxiosInterceptor Hook
 * This custom hook sets up an Axios response interceptor to handle global errors.
 * It manages actions such as redirecting the user to the login page when authentication errors occur.
 */
const useAxiosInterceptor = () => {
  const navigate = useNavigate(); // Hook for navigating between routes

  useEffect(() => {
    // Response interceptor to handle global errors
    const responseInterceptor = axiosInstance.interceptors.response.use(
      (response) => {
        // Return the response as-is if no errors occur
        return response;
      },
      (error) => {
        // Handle errors globally
        console.log("Error captured by Axios", error);

        if (error.response) {
          // Handle errors with a response from the server
          if (error.response.status === 401 || error.response.status === 403) {
            // If the user is unauthorized or forbidden, redirect to the login page
            localStorage.removeItem("authToken"); // Remove the auth token from localStorage
            sessionStorage.removeItem("authToken"); // Remove the auth token from sessionStorage
            navigate("/login"); // Redirect to the login page
          } else {
            // For other response errors, reject the promise with a custom error message
            return Promise.reject({
              ...error,
              statusText:
                "Error code " + error.response?.status + " - " + error?.message,
            });
          }
        } else if (error.request) {
          // Handle errors where the request was made but no response was received
          return Promise.reject({
            ...error,
            statusText: "Server unreachable. " + error?.message,
          });
        }

        // Handle unknown errors
        return Promise.reject({
          ...error,
          statusText: "Unknown error during the request. " + error?.message,
        });
      },
    );

    console.log("Axios interceptors configured");

    // Cleanup function to eject the interceptor when the component unmounts
    return () => {
      axiosInstance.interceptors.response.eject(responseInterceptor);
    };
  }, [navigate]); // Dependency array ensures the effect runs when `navigate` changes
};

export default useAxiosInterceptor;
