// React imports
import React, { useState, useEffect, useContext } from "react";
import PropTypes from "prop-types";

// Material-UI imports
import { TextField } from "@mui/material";

// DayJS imports
import dayjs from "dayjs";

// Local imports
import { FormContext } from "@/hooks/FormContext";

export default function ValidatedInput({
  name,
  required = false,
  value: externalValue, // Valeur contrôlée (optionnelle)
  onChange: externalOnChange, // Gestion contrôlée (optionnelle)
  defaultValue, // Valeur par défaut (optionnelle)
  children, // Autres éléments enfants (optionnels)
  ...props
}) {
  const [localValue, setLocalValue] = useState(defaultValue ?? "");
  const [error, setError] = useState("");

  const {
    validate,
    value: formValue,
    setValue: setFormValue,
  } = useContext(FormContext);

  // Détermine la valeur utilisée (interne ou externe)
  const isControlled = externalValue !== undefined;

  const value = isControlled ? externalValue : localValue;

  // Validation locale
  const handleValidation = (value) => {
    let validationError = "";
    if (required && (!value || value.trim?.() === "")) {
      validationError = `Ce champ est requis.`;
    } else if (validate) {
      validationError = validate(name, value, formValue); // Appel de la fonction de validation passée en prop
    }
    setError(validationError);
    return validationError;
  };

  // Gestion du changement de valeur
  const handleChange = (e) => {
    const newValue = dayjs.isDayjs(e)
      ? e
      : isControlled
        ? externalValue
        : e.target.value;

    const newFormValues = { ...formValue, [name]: newValue };

    // Valider la nouvelle valeur
    handleValidation(newValue);

    // Met à jour la valeur locale ou appelle le gestionnaire externe
    if (isControlled) {
      externalOnChange?.(name, newValue); // Si contrôlé, informer le parent
    } else {
      setLocalValue(newValue); // Si non contrôlé, mise à jour locale
    }
    setFormValue(newFormValues);
  };

  // Effet pour valider la valeur contrôlée lorsqu'elle change
  useEffect(() => {
    if (isControlled) {
      handleValidation(externalValue);
      setFormValue((prev) => ({ ...prev, [name]: externalValue }));
    }
  }, [externalValue]);

  useEffect(() => {
    setFormValue((prev) => ({ ...prev, [name]: value }));
  }, []);

  return (
    <>
      {children ? (
        React.Children.map(children, (child) =>
          React.cloneElement(child, {
            name,
            value,
            onChange: handleChange,
            error: !!error,
            helperText: error,
            required,
            slotProps: {
              textField: {
                helperText: error,
                error: !!error,
              },
            },
            ...props,
          }),
        )
      ) : (
        <TextField
          name={name}
          value={value}
          onChange={handleChange}
          error={!!error}
          helperText={error}
          required={required}
          {...props}
        />
      )}
    </>
  );
}

ValidatedInput.propTypes = {
  name: PropTypes.string.isRequired,
  required: PropTypes.bool,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.object,
    PropTypes.array,
  ]),
  onChange: PropTypes.func,
  defaultValue: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
  children: PropTypes.node,
};
