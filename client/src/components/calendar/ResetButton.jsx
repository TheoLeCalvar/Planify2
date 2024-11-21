import { Button } from "@mui/material";
import { constants } from '../../contants'
import { useContext } from 'react'
import { CalendarContext } from '../../context/CalendarContext'

export default function ResetButton(){

    const { eventService, genericEventService } = useContext(CalendarContext)

    return(
        <Button variant="text" onClick={() => {
            importCalendar(constants.CALENDAR.TYPES.AVAILABILITY)
            .then(({events, genericEvents}) => {
                eventService.set(events)
                genericEventService.set(genericEvents)
            })
            .catch((error) => {
              console.error(error)
            })
          }}>Réinitialiser</Button>
    )
}