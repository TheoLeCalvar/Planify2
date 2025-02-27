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
} from "@mui/material";
import { useLoaderData, useNavigate } from "react-router-dom";
import axiosInstance from "@/config/axiosConfig";
import ConfirmationDialog from "@/components/ConfirmationDialog";
import { toast } from "react-toastify";
import PropTypes from "prop-types";

export async function loader({ params }) {
  const response = await axiosInstance.get(`/taf/${params.idTAF}/configs`);
  return response.data;
}

export default function GeneratePlanning() {
  const navigate = useNavigate();
  const options = useLoaderData();

  const [selectedConfig, setSelectedConfig] = React.useState(null);
  const [openError, setOpenError] = React.useState(false);
  const [errorMessage, setErrorMessage] = React.useState("");
  const [synchroniseSelectionData, setSynchroniseSelectionData] =
    React.useState(null);
  const [synchroniseSelectOpen, setSynchroniseSelectOpen] =
    React.useState(false);
  const [synchroniseResult, setSynchroniseResult] = React.useState(null);

  const handleClose = () => {
    navigate("..");
  };

  const handleChange = (event) => {
    setSelectedConfig(event.target.value);
  };

  const handleCloseError = () => {
    setOpenError(false);
    //navigate("..");
  };

  const handleSelect = async () => {
    if (selectedConfig) {
      await axiosInstance
        .get(`/solver/check/${selectedConfig}`)
        .then((response) => {
          if (response.status === 200) {
            setSynchroniseSelectionData(response.data.synchronisedTafs);
            setSynchroniseResult(
              response.data.synchronisedTafs.map((taf) => ({
                tafId: taf.id,
                otherPlanning: null,
                enabled: true,
              })),
            );
          } else if (response.status === 201) {
            toast.success("Le planning est en cours de génération...");
            navigate(`..`);
          }
        })
        .catch((error) => {
          if (error.response?.status === 409) {
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

  const handleGeneratePlanning = async () => {
    axiosInstance
      .post(`/solver/run/${selectedConfig}`, {
        constraintsSynchronisation: synchroniseResult,
      })
      .then(() => {
        toast.success("Le planning est en cours de génération...");
        navigate(`..`);
      })
      .catch(() => {
        toast.error(
          "Une erreur est survenue lors de la génération du planning.",
        );
      });
  };

  const handleSynchroniseCheckChange = (tafId) => {
    setSynchroniseResult(
      synchroniseResult.map((result) =>
        result.tafId === tafId
          ? { ...result, enabled: !result.enabled }
          : result,
      ),
    );
  };

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
      {/* Popup de confirmation */}
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
              <DialogContentText id="confirmation-dialog-description" mb={2}>
                Sélectionnez pour chaque TAF synchronisée la configuration avec
                laquelle générer le planning. Si vous sélectionnez un planning
                généré, la synchonisation se basera sur le planning déjà généré.
                Sinon, les deux plannings seront générés conjointements.
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
      <ConfirmationDialog
        open={openError}
        onCancel={handleCloseError}
        onConfirm={null}
        handleClose={handleCloseError}
        cancelText="Fermer"
        dialogTitle="Erreur"
        dialogMessage={"Impossible de générer le planning. " + errorMessage}
      />
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

const SynchroniseConfigSelector = ({
  tafSynchroniseData,
  onClose,
  handleValidate,
}) => {
  const [selectedConfig, setSelectedConfig] = React.useState("");

  const handleChange = (event) => {
    setSelectedConfig(event.target.value);
  };

  const options = tafSynchroniseData.plannings;

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
              Plannnings générés
            </InputLabel>

            <Select
              labelId="select-label-generated"
              value={selectedConfig}
              onChange={handleChange}
              label="Plannnings générés"
            >
              {options
                .filter((option) => option.status === "GENERATED")
                .map((option) => (
                  <MenuItem
                    key={option.id}
                    value={option.id}
                    selected={option.id === selectedConfig}
                  >
                    {option.name}
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
  tafSynchroniseData: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  handleValidate: PropTypes.func.isRequired,
};
