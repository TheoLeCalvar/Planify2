import { create } from 'zustand';

const useStore = create((set) => ({
  sideBarOpen: false,
  toggleSideBar: () => set((state) => ({ sideBarOpen: !state.sideBarOpen })),
}))

export default useStore;
