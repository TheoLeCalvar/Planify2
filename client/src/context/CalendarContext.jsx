import {createContext , useMemo} from "react";
import { createEventsServicePlugin } from '@schedule-x/events-service';

export const CalendarContext = createContext()

export default function CalendarContextProvider({children}) {

    const eventService = useMemo(() => createEventsServicePlugin(), []);
    const genericEventService = useMemo(() => createEventsServicePlugin(), []);

    const onGenericChange = (event) => {
        eventService.getAll().filter((e) => e.inWeekId === event.inWeekId).forEach((e) => {
            eventService.update({
                ...e,
                status: event.status,
                people: event.people,
                _options: {
                    additionalClasses: [event._options.additionalClasses[0]],
                }
            })
        })
    }

    return ( 
        <div> 
            <CalendarContext.Provider value={{
                onGenericChange,
                eventService,
                genericEventService
            }}>
                {children}
            </CalendarContext.Provider> 
        </div>
    )
}