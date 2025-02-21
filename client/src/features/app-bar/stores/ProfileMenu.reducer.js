const initialState = {
  profileAnchorEl: null,
};

function profileMenuReducer(state, action) {
  switch (action.type) {
    case "OPEN_PROFILE":
      return { ...state, profileAnchorEl: action.payload };
    case "CLOSE_PROFILE":
      return { ...state, profileAnchorEl: null };
    default:
      return state;
  }
}

export { initialState, profileMenuReducer };
