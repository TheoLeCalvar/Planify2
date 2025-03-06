import axiosInstance from "../config/axiosConfig";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

const useAxiosInterceptor = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Intercepteur de réponse pour gérer les erreurs globales
    const responseInterceptor = axiosInstance.interceptors.response.use(
      (response) => {
        return response;
      },
      (error) => {
        // Vous pouvez gérer les erreurs ici, comme des redirections si l'utilisateur n'est pas authentifié
        console.log("Erreur capturée par Axios", error);
        if (error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            // Rediriger vers la page de connexion
            localStorage.removeItem("authToken");
            sessionStorage.removeItem("authToken");
            navigate("/login");
          } else {
            return Promise.reject({
              ...error,
              statusText:
                "Erreur code" + error.response?.status + " - " + error?.message,
            });
          }
        } else if (error.request) {
          return Promise.reject({
            ...error,
            statusText: "Serveur injoignable. " + error?.message,
          });
        }
        return Promise.reject({
          ...error,
          statusText: "Erreur inconnue lors de la requête. " + error?.message,
        });
      },
    );

    console.log("Intercepteurs Axios configurés");

    return () => {
      axiosInstance.interceptors.response.eject(responseInterceptor);
    };
  }, [navigate]);
};

export default useAxiosInterceptor;
