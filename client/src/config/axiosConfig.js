// Import Axios
import axios from "axios";

/**
 * Global configuration for the Axios instance.
 * This instance is used to perform HTTP requests with custom parameters and behaviors.
 */
const axiosInstance = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api`, // Base URL for all requests
  timeout: 10000, // Timeout for requests (10 seconds)
  // withCredentials: true, // Include cookies in requests (disabled by default)
});

/**
 * Request interceptor.
 * Automatically adds the authentication token (if it exists) to the request headers.
 */
axiosInstance.interceptors.request.use(
  (config) => {
    // Retrieve the token from localStorage or sessionStorage
    const token =
      localStorage.getItem("authToken") || sessionStorage.getItem("authToken"); // Alternatively, use Redux or Context API
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`; // Add the token to the headers
    }
    return config; // Return the modified configuration
  },
  (error) => {
    // Handle errors during request configuration
    console.log("Erreur capturée par Axios", error);
    return Promise.reject(error); // Reject the error to handle it elsewhere
  },
);

/**
 * Response interceptor.
 * Handles response errors, such as authentication errors (401, 403),
 * and redirects the user to the login page if necessary.
 */
axiosInstance.interceptors.response.use(
  (response) => {
    // Directly return the response if it is successful
    return response;
  },
  (error) => {
    // Handle response errors
    console.log("Erreur capturée par Axios", error);
    if (error.response) {
      // If a response is received but contains an error
      console.log("Erreur de réponse", error.response.status);
      if (error.response.status === 401 || error.response.status === 403) {
        // If the user is not authenticated or lacks permissions
        localStorage.removeItem("authToken"); // Remove the token from localStorage
        sessionStorage.removeItem("authToken"); // Remove the token from sessionStorage
        // Redirect to the login page if the user is not already there
        if (window.location.pathname !== "/login") {
          window.location.href = "/login";
        }
      } else {
        // Handle other response errors
        return Promise.reject({
          ...error,
          statusText:
            "Erreur code" + error.response?.status + " - " + error?.message,
        });
      }
    } else if (error.request) {
      // If no response is received (network issue or unreachable server)
      return Promise.reject({
        ...error,
        statusText: "Serveur injoignable. " + error?.message,
      });
    }
    // Handle unknown errors
    return Promise.reject({
      ...error,
      statusText: "Erreur inconnue lors de la requête. " + error?.message,
    });
  },
);

// Export the configured Axios instance
export default axiosInstance;
