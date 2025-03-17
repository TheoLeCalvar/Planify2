// React imports
import React from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { Box, Button, Stack } from "@mui/material";

// React Router imports
import { Link } from "react-router-dom";

// Material-UI icons
import AddIcon from "@mui/icons-material/Add";

// Localization
import locale from "@/config/locale.json";

// Styles
import styles from "./SideBarActions.styles";

/**
 * SidebarActions Component
 * This component renders a set of action buttons in the sidebar.
 * The buttons allow users to:
 * - Add a new lesson (UE).
 * - View the generated planning results (if available).
 * - Generate a new planning.
 *
 * @param {Object} props - The component props.
 * @param {string|number} props.tafID - The ID of the TAF to which the actions apply.
 * @param {Array} [props.resultPlanning] - The list of generated planning results (optional).
 * @param {Function} props.handleGenerateCalendar - Callback for generating a new calendar.
 * @param {boolean} props.generatingCalendar - Indicates whether the calendar is currently being generated.
 * @returns {JSX.Element} - The rendered SidebarActions component.
 */
const SidebarActions = ({ tafID, resultPlanning }) => {
  return (
    <Box sx={styles.container}>
      <Stack spacing={2}>
        {/* Button to add a new lesson (UE) */}
        <Link to={`/taf/${tafID}/ue/new`}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            sx={{ width: "100%" }}
          >
            {locale.layout.sideBar.addUE}
          </Button>
        </Link>

        {/* Button to view planning results (only if results are available) */}
        {resultPlanning?.length > 0 && (
          <Link to={`/taf/${tafID}/results`}>
            <Button variant="outlined" sx={{ width: "100%" }}>
              Voir les plannings
            </Button>
          </Link>
        )}

        {/* Button to generate a new planning */}
        <Link to={`/taf/${tafID}/generate`}>
          <Button variant="outlined" sx={{ width: "100%" }}>
            Générer le planning
          </Button>
        </Link>
      </Stack>
    </Box>
  );
};

SidebarActions.propTypes = {
  /**
   * The ID of the TAF to which the actions apply.
   * This is used to construct the navigation links.
   */
  tafID: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,

  /**
   * The list of generated planning results (optional).
   * If provided and not empty, the "Voir les plannings" button will be displayed.
   */
  resultPlanning: PropTypes.array,

  /**
   * Callback for generating a new calendar.
   * This prop is required but is not used in the current implementation.
   */
  handleGenerateCalendar: PropTypes.func.isRequired,

  /**
   * Indicates whether the calendar is currently being generated.
   * This prop is required but is not used in the current implementation.
   */
  generatingCalendar: PropTypes.bool.isRequired,
};

export default SidebarActions;
