const styles = {
  TAFSelectorBox: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    flexGrow: 1,
    height: "100%",
  },
  formControl: {
    margin: 1,
    minWidth: 300,
  },
  label: {
    color: "white",
    "&.Mui-focused": {
      color: "white",
    },
  },
  select: {
    color: "white",
    "&. MuiOutlinedInput-notchedOutline": {
      borderColor: "white",
    },

    "&:hover .MuiOutlinedInput-notchedOutline": {
      borderColor: "white",
    },
    "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
      borderColor: "white",
    },
    "& .MuiSvgIcon-root": {
      color: "#fff",
    },
  },
};

export default styles;
