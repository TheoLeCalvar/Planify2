import axios from "axios";

// Configuration globale de l'instance Axios
const axiosInstance = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api`,
  timeout: 10000, // Timeout pour les requêtes
});

// Intercepteur pour ajouter un token d'authentification si nécessaire
axiosInstance.interceptors.request.use(
  (config) => {
    // Exemple de récupération du token depuis le localStorage ou un state
    const token = localStorage.getItem("authToken"); // ou utilisez Redux ou un Context API
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.log("Erreur capturée par Axios", error);
    return Promise.reject(error);
  },
);

// Intercepteur de réponse pour gérer les erreurs globales
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Vous pouvez gérer les erreurs ici, comme des redirections si l'utilisateur n'est pas authentifié
    console.log("Erreur capturée par Axios", error);
    if (error.response) {
      if (error.response.status === 401) {
        // Rediriger vers la page de connexion
        window.location.href = "/login";
      }
    } else {
      return Promise.reject({
        ...error,
        statusText:
          "Erreur code" + error.response.status + " - " + error.message,
      });
    }
    if (error.request) {
      return Promise.reject({
        ...error,
        statusText: "Serveur injoignable. " + error.message,
      });
    }
    return Promise.reject({
      ...error,
      statusText: "Erreur inconnue lors de la requête. " + error.message,
    });
  },
);

export default axiosInstance;
