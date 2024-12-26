import React, { useState, useEffect, useContext } from "react";
import { TextField } from "@mui/material";
import { FormContext } from "../../context/FormContext";
import dayjs from 'dayjs'

export default function ValidatedInput({
  name,
  required = false,
  value: externalValue, // Valeur contrôlée (optionnelle)
  onChange: externalOnChange, // Gestion contrôlée (optionnelle)
  defaultValue, // Valeur par défaut (optionnelle)
  children, // Autres éléments enfants (optionnels)
  ...props
}) {
  const [localValue, setLocalValue] = useState(defaultValue ?? null);
  const [error, setError] = useState("");

  const { validate, form } = useContext(FormContext);

  // Détermine la valeur utilisée (interne ou externe)
  const isControlled = externalValue !== undefined;

  const value = isControlled ? externalValue : localValue;

  // Validation locale
  const handleValidation = (value, formValues) => {
    let validationError = "";
    if (required && !value) {
      validationError = `Ce champ est requis.`;
    } else if (validate) {
      validationError = validate(name, value, formValues); // Appel de la fonction de validation passée en prop
    }
    setError(validationError);
    return validationError;
  };

  // Gestion du changement de valeur
  const handleChange = (e) => {
    
    const newValue = dayjs.isDayjs(e) ? e : e.target.value;

    const formValues = Object.fromEntries(new FormData(form?.current));

    // Valider la nouvelle valeur
    handleValidation(newValue, formValues);

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
    <>
      {children ? 
        React.Children.map(children, (child) =>
          React.cloneElement(child, {
            name,
            value,
            onChange: handleChange,
            slotProps: {
              textField: {
                helperText: error,
                error: !!error,
              }
            },
            ...props,
          })
        )
        :
        (<TextField
        name={name}
        value={value}
        onChange={handleChange}
        error={!!error}
        helperText={error}
        required={required}
        {...props}
      />)}
    </>
  );
}
