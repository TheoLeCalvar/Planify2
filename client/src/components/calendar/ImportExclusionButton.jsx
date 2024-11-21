import importExclusionCalendar from '../../helper/importExclusionCalendar'
import { useContext } from 'react'
import { CalendarContext } from '../../context/CalendarContext'
import ConfirmationButton from "../ConfirmationButton";

export default function ImportExclusionButton(){

    const { eventService } = useContext(CalendarContext)

    return(
        <ConfirmationButton 
          variant="text" 
          buttonText="Importer un calendrier d'indisponibilité"
          dialogTitle="Avertissement"
          dialogMessage="Importer un calendrier d'indisponibilité modifiera la disponibilité de certains créneaux."
          confirmText="Poursuivre l'importation"
          onConfirm={() => {
            importExclusionCalendar(eventService.getAll(), -30)
            .then(events => {
                eventService.set(events)
            })
            .catch((error) => {
              console.error(error)
            })
          }}/>
    )
}