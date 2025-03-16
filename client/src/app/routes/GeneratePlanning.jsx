// React imports
import React from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  Switch,
  Typography,
} from "@mui/material"; // Material-UI components for UI elements
import { useLoaderData, useNavigate, useRevalidator } from "react-router-dom"; // React Router hooks for data loading, navigation, and revalidation
import axiosInstance from "@/config/axiosConfig"; // Axios instance for API requests
import ConfirmationDialog from "@/components/ConfirmationDialog"; // Custom confirmation dialog component
import { toast } from "react-toastify"; // Notifications for success and error messages
import PropTypes from "prop-types"; // PropTypes for type checking

/**
 * Loader function to fetch configurations for a specific TAF.
 * Retrieves all configurations associated with the given TAF ID.
 *
 * @param {Object} params - Parameters passed to the loader, including `idTAF`.
 * @returns {Promise<Array>} - A promise that resolves to an array of configurations.
 */
export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}/configs`); // Fetch configurations from the API
  return response.data; // Return the fetched data
}

/**
 * GeneratePlanning component.
 * This component handles the generation of a new planning based on selected configurations.
 * It provides a dialog interface for selecting configurations and managing synchronization options.
 *
 * @returns {JSX.Element} - The rendered component.
 */
export default function GeneratePlanning() {
  const navigate = useNavigate(); // Hook for navigation
  const options = useLoaderData(); // Load configurations from the loader
  const revalidator = useRevalidator(); // Hook for revalidating data

  // State variables
  const [selectedConfig, setSelectedConfig] = React.useState(null); // Selected configuration ID
  const [openError, setOpenError] = React.useState(false); // Error dialog visibility
  const [errorMessage, setErrorMessage] = React.useState(""); // Error message
  const [synchroniseSelectionData, setSynchroniseSelectionData] =
    React.useState(null); // Data for synchronized TAFs
  const [synchroniseSelectOpen, setSynchroniseSelectOpen] =
    React.useState(false); // Synchronization selector visibility
  const [synchroniseResult, setSynchroniseResult] = React.useState(null); // Synchronization results

  /**
   * Handles closing the main dialog.
   */
  const handleClose = () => {
    navigate("..");
  };

  /**
   * Handles changes in the selected configuration.
   *
   * @param {Object} event - The event object from the select input.
   */
  const handleChange = (event) => {
    setSelectedConfig(event.target.value);
  };

  /**
   * Handles closing the error dialog.
   */
  const handleCloseError = () => {
    setOpenError(false);
  };

  /**
   * Handles selecting a configuration and checking its validity.
   * If valid, it prepares synchronization data or starts the planning generation.
   */
  const handleSelect = async () => {
    if (selectedConfig) {
      await axiosInstance
        .get(`/solver/check/${selectedConfig}`) // Check the selected configuration
        .then((response) => {
          if (response.status === 200) {
            // Prepare synchronization data
            setSynchroniseSelectionData(response.data.synchronisedTafs);
            setSynchroniseResult(
              response.data.synchronisedTafs.map((taf) => ({
                tafId: taf.id,
                otherPlanning: null,
                enabled: true,
              })),
            );
          } else if (response.status === 201) {
            // Planning generation started
            toast.success("Le planning est en cours de génération...");
            navigate(`..`);
          }
        })
        .catch((error) => {
          if (error.response?.status === 409) {
            // Handle specific errors
            switch (error.response.data.error) {
              case "No slots":
                setErrorMessage(
                  "Les disponibilités de la TAF ne sont pas définies. Veuillez les définir dans l'onglet 'Calendrier'.",
                );
                break;
              case "No UE":
                setErrorMessage("La TAF ne contient aucune UE.");
                break;
              case "No lessons in UE":
                setErrorMessage("Toutes les UE doivent avoir des cours.");
                break;
              case "Lecturer availability not filled":
                setErrorMessage(
                  "Certains intervenants n'ont pas déclaré leur disponibilités.",
                );
                break;
              default:
                setErrorMessage("Erreur inconnue");
                console.error(
                  "Erreur non reconnue lors de l'affichage de l'erreur",
                  error.response.data,
                );
            }
            setOpenError(true);
          } else {
            toast.error(
              "Une erreur est survenue lors de la génération du planning.",
            );
          }
        });
    }
  };

  /**
   * Handles generating the planning with the selected configuration and synchronization data.
   */
  const handleGeneratePlanning = async () => {
    axiosInstance
      .post(`/solver/run/${selectedConfig}`, {
        constraintsSynchronisation: synchroniseResult,
      })
      .then(() => {
        toast.success("Le planning est en cours de génération...");
        navigate(`..`);
        revalidator.revalidate();
      })
      .catch(() => {
        toast.error(
          "Une erreur est survenue lors de la génération du planning.",
        );
      });
  };

  /**
   * Handles toggling synchronization for a specific TAF.
   *
   * @param {number} tafId - The ID of the TAF to toggle.
   */
  const handleSynchroniseCheckChange = (tafId) => {
    setSynchroniseResult(
      synchroniseResult.map((result) =>
        result.tafId === tafId
          ? { ...result, enabled: !result.enabled }
          : result,
      ),
    );
  };

  /**
   * Handles selecting a configuration for synchronization.
   *
   * @param {number} tafId - The ID of the TAF.
   * @param {number} configId - The ID of the selected configuration.
   */
  const handleSynchroniseConfigSelector = (tafId, configId) => {
    setSynchroniseResult(
      synchroniseResult.map((result) =>
        result.tafId === tafId
          ? { ...result, otherPlanning: configId }
          : result,
      ),
    );
    setSynchroniseSelectOpen(false);
  };

  return (
    <>
      {/* Main dialog for generating a planning */}
      <Dialog
        open={true}
        onClose={handleClose}
        aria-labelledby="confirmation-dialog-title"
        aria-describedby="confirmation-dialog-description"
      >
        <DialogTitle id="confirmation-dialog-title">
          Générer un nouveau planning
        </DialogTitle>
        <DialogContent>
          {synchroniseSelectionData ? (
            <>
              {/* Synchronization options */}
              <DialogContentText id="confirmation-dialog-description" mb={2}>
                Sélectionnez pour chaque TAF synchronisée la configuration avec
                laquelle générer le planning.
              </DialogContentText>
              <Typography variant="h6" mb={2}>
                TAF synchronisées
              </Typography>
              <Stack direction="column" gap={2}>
                {synchroniseSelectionData.map((taf) => (
                  <Stack
                    key={taf.id}
                    direction="row"
                    alignItems="center"
                    gap={2}
                  >
                    <Switch
                      checked={
                        synchroniseResult.find(
                          (result) => result.tafId === taf.id,
                        )?.enabled
                      }
                      onChange={() => handleSynchroniseCheckChange(taf.id)}
                      inputProps={{ "aria-label": "controlled" }}
                    />
                    <Typography fontWeight="bold">{taf.name}</Typography>
                    {synchroniseResult.find((result) => result.tafId === taf.id)
                      ?.otherPlanning && (
                      <Typography>
                        Planning associé :{" "}
                        {
                          synchroniseSelectionData
                            .find((result) => result.id === taf.id)
                            .plannings.find(
                              (planning) =>
                                planning.id ===
                                synchroniseResult.find(
                                  (result) => result.tafId === taf.id,
                                ).otherPlanning,
                            ).name
                        }
                      </Typography>
                    )}
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={() => {
                        setSynchroniseSelectOpen(taf.id);
                      }}
                      disabled={
                        !synchroniseResult.find(
                          (result) => result.tafId === taf.id,
                        )?.enabled
                      }
                    >
                      Associer
                    </Button>
                  </Stack>
                ))}
              </Stack>
            </>
          ) : (
            <>
              {/* Configuration selection */}
              <DialogContentText id="confirmation-dialog-description" mb={2}>
                Sélectionnez une configuration pour générer un nouveau planning.
              </DialogContentText>
              <FormControl sx={{ minWidth: 300 }}>
                <InputLabel id="select-label-configs">
                  Choisir une configuration
                </InputLabel>
                <Select
                  labelId="select-label-configs"
                  value={selectedConfig ?? ""}
                  onChange={handleChange}
                  label="Choisir une configuration"
                >
                  {options.map((option) => (
                    <MenuItem
                      key={option.id}
                      value={option.id}
                      selected={option.id === selectedConfig}
                    >
                      {option.name}
                    </MenuItem>
                  ))}
                  {options.length === 0 && (
                    <MenuItem disabled>Aucune configuration</MenuItem>
                  )}
                </Select>
              </FormControl>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="secondary">
            Annuler
          </Button>
          {synchroniseSelectionData ? (
            <Button
              onClick={handleGeneratePlanning}
              variant="contained"
              color="primary"
              autoFocus
              disabled={synchroniseResult
                .filter((result) => result.enabled)
                .some((result) => result.otherPlanning === null)}
            >
              Générer
            </Button>
          ) : (
            <Button
              onClick={handleSelect}
              variant="contained"
              color="primary"
              autoFocus
              disabled={!selectedConfig}
            >
              Continuer
            </Button>
          )}
        </DialogActions>
      </Dialog>

      {/* Error dialog */}
      <ConfirmationDialog
        open={openError}
        onCancel={handleCloseError}
        onConfirm={null}
        handleClose={handleCloseError}
        cancelText="Fermer"
        dialogTitle="Erreur"
        dialogMessage={"Impossible de générer le planning. " + errorMessage}
      />

      {/* Synchronization configuration selector */}
      {synchroniseSelectOpen && (
        <SynchroniseConfigSelector
          tafSynchroniseData={synchroniseSelectionData.find(
            (taf) => taf.id == synchroniseSelectOpen,
          )}
          handleValidate={handleSynchroniseConfigSelector}
          onClose={() => setSynchroniseSelectOpen(false)}
        />
      )}
    </>
  );
}

/**
 * SynchroniseConfigSelector component.
 * This component allows the user to select a configuration for a synchronized TAF.
 *
 * @param {Object} props - The component props.
 * @param {Object} props.tafSynchroniseData - Data for the synchronized TAF.
 * @param {Function} props.onClose - Function to close the selector.
 * @param {Function} props.handleValidate - Function to validate the selected configuration.
 * @returns {JSX.Element} - The rendered component.
 */
const SynchroniseConfigSelector = ({
  tafSynchroniseData,
  onClose,
  handleValidate,
}) => {
  const [selectedConfig, setSelectedConfig] = React.useState(""); // Selected configuration ID

  /**
   * Handles changes in the selected configuration.
   *
   * @param {Object} event - The event object from the select input.
   */
  const handleChange = (event) => {
    setSelectedConfig(event.target.value);
  };

  const options = tafSynchroniseData.plannings; // Available configurations for the TAF

  return (
    <Dialog
      open={true}
      onClose={onClose}
      aria-labelledby="confirmation-dialog-title"
      aria-describedby="confirmation-dialog-description"
    >
      <DialogTitle id="confirmation-dialog-title">
        Sélectionner une configuration
      </DialogTitle>
      <DialogContent>
        <DialogContentText id="confirmation-dialog-description" mb={2}>
          Sélectionnez une configuration de la TAF synchronisée pour générer le
          nouveau planning.
        </DialogContentText>
        <DialogContentText mb={2}>
          TAF : <strong>{tafSynchroniseData.name}</strong>
        </DialogContentText>
        <Stack direction="column" gap={3}>
          <FormControl sx={{ minWidth: 300 }}>
            <InputLabel id="select-label-configurations">
              Configurations
            </InputLabel>
            <Select
              labelId="select-label-configurations"
              value={selectedConfig}
              onChange={handleChange}
              label="Configurations"
            >
              {options
                .filter((option) => option.status === "CONFIG")
                .map((option) => (
                  <MenuItem
                    key={option.id}
                    value={option.id}
                    selected={option.id === selectedConfig}
                  >
                    {option.name}
                  </MenuItem>
                ))}
              {options.filter((option) => option.status === "CONFIG").length ===
                0 && <MenuItem disabled>Aucune configuration</MenuItem>}
            </Select>
          </FormControl>
          <FormControl sx={{ minWidth: 300 }}>
            <InputLabel id="select-label-generated">
              Plannings générés
            </InputLabel>
            <Select
              labelId="select-label-generated"
              value={selectedConfig}
              onChange={handleChange}
              label="Plannings générés"
            >
              {options
                .filter((option) => option.status === "GENERATED")
                .map((option) => (
                  <MenuItem
                    key={option.id}
                    value={option.id}
                    selected={option.id === selectedConfig}
                  >
                    {option.timestamp} - {option.name}
                  </MenuItem>
                ))}
              {options.filter((option) => option.status === "GENERATED")
                .length === 0 && (
                <MenuItem disabled>Aucune configuration</MenuItem>
              )}
            </Select>
          </FormControl>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary">
          Annuler
        </Button>
        <Button
          onClick={() => handleValidate(tafSynchroniseData.id, selectedConfig)}
          variant="contained"
          color="primary"
          autoFocus
          disabled={!selectedConfig}
        >
          Valider
        </Button>
      </DialogActions>
    </Dialog>
  );
};

SynchroniseConfigSelector.propTypes = {
  tafSynchroniseData: PropTypes.object, // Data for the synchronized TAF
  onClose: PropTypes.func.isRequired, // Function to close the selector
  handleValidate: PropTypes.func.isRequired, // Function to validate the selected configuration
};
