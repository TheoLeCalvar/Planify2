import { useState, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";

export const useTAFSelection = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // Extract the TAF id from the URL (if present)
  const tafIdFromPath = location.pathname.match(/\/taf\/(\d+)/)?.[1] || "";
  const [selectedTAF, setSelectedTAF] = useState(tafIdFromPath);

  const onTAFChange = useCallback(
    (newTAFId) => {
      setSelectedTAF(newTAFId);
      navigate(`/taf/${newTAFId}`);
    },
    [navigate],
  );

  return { selectedTAF, onTAFChange };
};
