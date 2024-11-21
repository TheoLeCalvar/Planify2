import { useState } from "react";
import Calendar from "../components/calendar/Calendar";
import GenericWeekCalendar from "../components/calendar/GenericWeekCalendar"
import { Tabs, Tab } from "@mui/material";
import CalendarContextProvider from "../context/CalendarContext";
import ImportButton from "../components/calendar/ImportButton";
import ExportButton from "../components/calendar/ExportButton";
import generateClassSlots from "../helper/classSlot";
import ResetButton from "../components/calendar/ResetButton";
import ImportExclusionButton from "../components/calendar/ImportExclusionButton";


export default function CalendarPage() {
  const [tabIndex, setTabIndex] = useState(1)

  const handleChange = (event, newValue) => {
    setTabIndex(newValue);
  };

  const initialEvents = generateClassSlots('2024-09-01', '2024-12-31')
  
  return (
    <div>
      <Tabs value={tabIndex} onChange={handleChange}>
        <Tab label="Semaine type" />
        <Tab label="Agenda" />
      </Tabs>
      <CalendarContextProvider initialEvents={initialEvents}>
        <div hidden={tabIndex !== 0}>
          <GenericWeekCalendar/>
        </div>
        <div hidden={tabIndex !== 1}>
          <Calendar/>
        </div>
        <ResetButton/>
        <ImportButton/>
        <ExportButton/>
        <ImportExclusionButton/>
      </CalendarContextProvider>
    </div>
  )
}
 