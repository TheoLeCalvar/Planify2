import { drawerWidth, appBarHeight } from "@/config/constants";

const styles = {
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
    "& .MuiDrawer-paper": {
      width: drawerWidth,
      boxSizing: "border-box",
      top: appBarHeight,
      height: `calc(100vh - ${appBarHeight}px)`,
      position: "fixed",
      overflowX: "hidden",
    },
  },
  sidebarContainer: {
    width: drawerWidth,
    display: "flex",
    flexDirection: "column",
    height: "90vh",
  },
  mainContent: {
    flexGrow: 1,
    padding: 3,
  },
};

export default styles;
