import ICAL from "ical.js";
import { toast } from "react-toastify";
import { JSONToCalendarEvent } from "./calendarEvent";

export default function importExclusionCalendar(previousEvents, delay, changingStatus) {
  return new Promise((resolve, reject) => {
    // Créer dynamiquement un élément input de type file
    const input = document.createElement("input");
    input.type = "file";
    input.accept = ".ics";

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
          const icsData = e.target.result;

          // Analyse du fichier ICS avec ical.js
          const jcalData = ICAL.parse(icsData);
          const component = new ICAL.Component(jcalData);
          const vevents = component.getAllSubcomponents("vevent");

          // Conversion des événements ICS en un format exploitable
          const importedEvents = vevents.map((vevent) => {
            const event = new ICAL.Event(vevent);
            return {
              start: event.startDate.toJSDate(), // Convertir en Date
              end: event.endDate.toJSDate(),
            };
          });

          if (importedEvents.length === 0) {
            toast.warn("Aucun événement trouvé dans le fichier");
            resolve(previousEvents); // Aucun changement dans les événements existants
            return;
          }

          // Ajouter le délai avant et après chaque événement importé
          const delayInMs = delay * 60 * 1000; // Convertir le délai en millisecondes
          const extendedRanges = importedEvents.map((event) => ({
            start: new Date(event.start.getTime() - delayInMs),
            end: new Date(event.end.getTime() + delayInMs),
          }));

          // Vérifier les chevauchements et mettre à jour le statut
          const newEvents = previousEvents.map((prevEvent) => {
            const prevStart = new Date(prevEvent.start);
            const prevEnd = new Date(prevEvent.end);
            let newStatus = prevEvent.status;

            const overlaps = extendedRanges.some(
              (range) =>
                prevStart < range.end && prevEnd > range.start // Vérifie si les plages se chevauchent
            );

            if (overlaps) {
                newStatus = changingStatus;
            }

            return JSONToCalendarEvent({...prevEvent, status: newStatus});
          });

          toast.success("Calendrier importé avec succès !");
          resolve(newEvents); // Résoudre avec les événements mis à jour
        } catch (error) {
          toast.error("Importation impossible : données incompatibles");
          reject("Erreur lors de l'analyse du fichier ICS : " + error.message);
        }
      };

      reader.onerror = () => {
        toast.error("Erreur lors de la lecture du fichier ICS");
        reject("Erreur de lecture du fichier.");
      };

      // Lire le contenu du fichier
      reader.readAsText(file);
    };

    // Simuler un clic pour ouvrir la boîte de dialogue
    input.click();
  });
}
