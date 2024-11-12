import { useState } from "react";
import Calendar from "./components/calendar/Calendar";
import GenericWeekCalendar from "./components/calendar/GenericWeekCalendar"
import { Tabs, Tab } from "@mui/material";
import CalendarContextProvider from "./context/CalendarContext";
import ImportButton from "./components/calendar/ImportButton";
import ExportButton from "./components/calendar/ExportButton";
import { ToastContainer } from 'react-toastify';

import 'react-toastify/dist/ReactToastify.css';

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
      <ToastContainer
        position="bottom-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
    </div>
  )
}
 