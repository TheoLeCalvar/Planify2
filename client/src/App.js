import { useState } from "react";
import Calendar from "./components/calendar/Calendar";
import GenericWeekCalendar from "./components/calendar/GenericWeekCalendar"
import { Tabs, Tab } from "@mui/material";
import CalendarContextProvider from "./context/CalendarContext";
import ImportButton from "./components/calendar/ImportButton";
import ExportButton from "./components/calendar/ExportButton";

export default function App() {
  const [tabIndex, setTabIndex] = useState(1)

  const handleChange = (event, newValue) => {
    setTabIndex(newValue);
  };
  
  return (
    <div>
      <Tabs value={tabIndex} onChange={handleChange}>
        <Tab label="Semaine type" />
        <Tab label="Agenda" />
      </Tabs>
      <CalendarContextProvider>
        <div style={{display: tabIndex === 0 ? 'block' : 'none'}}>
          <GenericWeekCalendar/>
        </div>
        <div style={{display: tabIndex === 1 ? 'block' : 'none'}}>
          <Calendar/>
        </div>
        <ImportButton/>
        <ExportButton/>
      </CalendarContextProvider>
    </div>
  )
}
 