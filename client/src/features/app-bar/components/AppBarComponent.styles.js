import { appBarHeight } from "@/config/constants";

const styles = {
  toolbar: {
    justifyContent: "space-between",
  },
  appbar: {
    height: appBarHeight,
    position: "fixed",
    "& .MuiAppBar-root": {
      height: appBarHeight,
      boxSizing: "border-box",
    },
  },
  mainContent: {
    marginTop: `${appBarHeight}px`,
  }
};

export default styles;
