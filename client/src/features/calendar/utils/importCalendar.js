import { JSONToCalendarEvent } from "./calendarEvent";
import { toast } from "react-toastify";

export default function importCalendar(acceptedType) {
  return new Promise((resolve, reject) => {
    // Créer dynamiquement un élément input de type file
    const input = document.createElement("input");
    input.type = "file";
    input.accept = ".json";

    // Ajouter un écouteur d'événement pour la sélection du fichier
    input.onchange = (event) => {
      const file = event.target.files[0];
      if (!file) {
        toast.error("Importation impossible : Aucun fichier sélectionné");
        reject("Aucun fichier sélectionné");
        return;
      }

      const reader = new FileReader();

      // Lire le fichier en tant que texte
      reader.onload = (e) => {
        try {
          const jsonData = JSON.parse(e.target.result);

          if (jsonData.calendarType !== acceptedType) {
            toast.error(
              "Importation impossible : type de calendrier incompatible",
            );
            reject("Mauvais type de calendrier");
            return;
          }

          const events = jsonData.dataCalendar.map(JSONToCalendarEvent);
          const genericEvents =
            jsonData.dataGenericCalendar.map(JSONToCalendarEvent);

          toast.success("Calendrier importé avec succès !");

          resolve({ events, genericEvents }); // Résoudre la promesse avec les données JSON
        } catch (error) {
          toast.error("Importation impossible : données incompatible");
          reject("Erreur lors de la lecture du fichier JSON : " + error);
        }
      };

      reader.onerror = () => {
        toast.error("Erreur lors de la lecture du fichier JSON");
        reject("Erreur de lecture du fichier.");
      };

      // Lire le contenu du fichier
      reader.readAsText(file);
    };

    // Simuler un clic pour ouvrir la boîte de dialogue
    input.click();
  });
}
