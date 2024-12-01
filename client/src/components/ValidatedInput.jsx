import React, { useState, useEffect } from "react";
import { TextField } from "@mui/material";

export default function ValidatedInput({
  label,
  name,
  type = "text",
  required = false,
  validate,
  fullWidth = true,
  value: externalValue, // Valeur contrôlée (optionnelle)
  onChange: externalOnChange, // Gestion contrôlée (optionnelle)
}) {
  const [localValue, setLocalValue] = useState("");
  const [error, setError] = useState("");

  // Détermine la valeur utilisée (interne ou externe)
  const isControlled = externalValue !== undefined;

  const value = isControlled ? externalValue : localValue;

  // Validation locale
  const handleValidation = (value) => {
    let validationError = "";
    if (required && !value) {
      validationError = `${label} est requis.`;
    } else if (validate) {
      validationError = validate(value); // Appel de la fonction de validation passée en prop
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
      label={label}
      name={name}
      value={value}
      onChange={handleChange}
      error={!!error}
      helperText={error}
      type={type}
      fullWidth={fullWidth}
    />
  );
}
