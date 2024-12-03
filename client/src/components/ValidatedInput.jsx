import React, { useState, useEffect, useContext } from "react";
import { TextField } from "@mui/material";
import { FormContext } from "../context/FormContext";

export default function ValidatedInput({
  name,
  required = false,
  value: externalValue, // Valeur contrôlée (optionnelle)
  onChange: externalOnChange, // Gestion contrôlée (optionnelle)
  defaultValue, // Valeur par défaut (optionnelle)
  ...props
}) {
  const [localValue, setLocalValue] = useState(defaultValue ?? "");
  const [error, setError] = useState("");

  const validate = useContext(FormContext);

  // Détermine la valeur utilisée (interne ou externe)
  const isControlled = externalValue !== undefined;

  const value = isControlled ? externalValue : localValue;

  // Validation locale
  const handleValidation = (value) => {
    let validationError = "";
    if (required && !value) {
      validationError = `Ce champ est requis.`;
    } else if (validate) {
      validationError = validate(name, value); // Appel de la fonction de validation passée en prop
    }
    setError(validationError);
    return validationError;
  };

  // Gestion du changement de valeur
  const handleChange = (e) => {
    const newValue = e.target.value;

    // Valider la nouvelle valeur
    handleValidation(newValue);

    // Met à jour la valeur locale ou appelle le gestionnaire externe
    if (isControlled) {
      externalOnChange?.(name, newValue); // Si contrôlé, informer le parent
    } else {
      setLocalValue(newValue); // Si non contrôlé, mise à jour locale
    }
  };

  // Effet pour valider la valeur contrôlée lorsqu'elle change
  useEffect(() => {
    if (isControlled) {
      handleValidation(externalValue);
    }
  }, [externalValue]);

  return (
    <TextField
      name={name}
      value={value}
      onChange={handleChange}
      error={!!error}
      helperText={error}
      {...props}
    />
  );
}
