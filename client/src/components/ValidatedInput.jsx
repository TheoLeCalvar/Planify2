// React imports
import React, { useState, useEffect, useContext } from "react";
import PropTypes from "prop-types"; // PropTypes for type checking

// Material-UI imports
import { TextField } from "@mui/material"; // Material-UI TextField component

// DayJS imports
import dayjs from "dayjs"; // Utility for date manipulation

// Local imports
import { FormContext } from "@/hooks/FormContext"; // Context for managing form state and validation

/**
 * ValidatedInput component.
 * This component renders an input field with validation and error handling.
 * It supports both controlled and uncontrolled modes and integrates with a form context.
 *
 * @param {Object} props - The component props.
 * @param {string} props.name - The name of the input field.
 * @param {boolean} [props.required=false] - Whether the field is required.
 * @param {string|Object|Array} [props.value] - The controlled value of the input field.
 * @param {Function} [props.onChange] - Callback function for handling value changes.
 * @param {string|Object} [props.defaultValue] - The default value for the input field.
 * @param {React.ReactNode} [props.children] - Custom child components to render instead of the default TextField.
 * @param {Object} props.props - Additional props to pass to the input field.
 *
 * @returns {JSX.Element} - The rendered ValidatedInput component.
 */
export default function ValidatedInput({
  name,
  required = false,
  value: externalValue, // Controlled value (optional)
  onChange: externalOnChange, // Controlled change handler (optional)
  defaultValue, // Default value (optional)
  children, // Custom child components (optional)
  ...props
}) {
  // Local state for the input value and error message
  const [localValue, setLocalValue] = useState(defaultValue ?? "");
  const [error, setError] = useState("");

  // Access the form context
  const {
    validate, // Validation function from the context
    value: formValue, // Current form values
    setValue: setFormValue, // Function to update form values
  } = useContext(FormContext);

  // Determine if the input is controlled or uncontrolled
  const isControlled = externalValue !== undefined;
  const value = isControlled ? externalValue : localValue;

  /**
   * Validates the input value.
   *
   * @param {any} value - The value to validate.
   * @returns {string} - The validation error message, or an empty string if valid.
   */
  const handleValidation = (value) => {
    let validationError = "";
    if (required && (!value || value.trim?.() === "")) {
      validationError = `Ce champ est requis.`; // Required field error
    } else if (validate) {
      validationError = validate(name, value, formValue); // Custom validation from the context
    }
    setError(validationError);
    return validationError;
  };

  /**
   * Handles changes to the input value.
   *
   * @param {Object} e - The change event.
   */
  const handleChange = (e) => {
    const newValue = dayjs.isDayjs(e)
      ? e
      : isControlled
        ? externalValue
        : e.target.value;

    const newFormValues = { ...formValue, [name]: newValue };

    // Validate the new value
    handleValidation(newValue);

    // Update the value locally or notify the parent component
    if (isControlled) {
      externalOnChange?.(name, newValue); // Notify the parent if controlled
    } else {
      setLocalValue(newValue); // Update local state if uncontrolled
    }
    setFormValue(newFormValues); // Update the form context
  };

  // Validate the controlled value when it changes
  useEffect(() => {
    if (isControlled) {
      handleValidation(externalValue);
      setFormValue((prev) => ({ ...prev, [name]: externalValue }));
    }
  }, [externalValue]);

  // Initialize the form context with the default value
  useEffect(() => {
    setFormValue((prev) => ({ ...prev, [name]: value }));
  }, []);

  return (
    <>
      {children ? (
        // Render custom child components if provided
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
        // Render the default TextField component
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

// Define the prop types for the component
ValidatedInput.propTypes = {
  name: PropTypes.string.isRequired, // Name of the input field
  required: PropTypes.bool, // Whether the field is required
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.object,
    PropTypes.array,
  ]), // Controlled value
  onChange: PropTypes.func, // Controlled change handler
  defaultValue: PropTypes.oneOfType([PropTypes.string, PropTypes.object]), // Default value
  children: PropTypes.node, // Custom child components
};
