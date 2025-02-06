import { create } from "zustand";

const useStore = create((set) => ({
  sideBarOpen: true,
  toggleSideBar: () => set((state) => ({ sideBarOpen: !state.sideBarOpen })),
}));

export default useStore;
