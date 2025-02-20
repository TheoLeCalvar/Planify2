import { useCallback, useReducer } from "react";
import axiosInstance from "@/config/axiosConfig";

const initialState = {
  generatingCalendar: false,
};

function reducer(state, action) {
  switch (action.type) {
    case "START_GENERATION":
      return { ...state, generatingCalendar: true };
    case "END_GENERATION":
      return { ...state, generatingCalendar: false };
    default:
      return state;
  }
}

const useCalendarGeneration = (tafID) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  const handleGenerateCalendar = useCallback(async () => {
    dispatch({ type: "START_GENERATION" });
    try {
      await axiosInstance.get(`/solver/run/${tafID}`);
    } catch (error) {
      console.error("Error generating calendar:", error);
    } finally {
      dispatch({ type: "END_GENERATION" });
    }
  }, [tafID]);

  return {
    generatingCalendar: state.generatingCalendar,
    handleGenerateCalendar,
  };
};

export default useCalendarGeneration;
