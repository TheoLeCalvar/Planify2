const initialState = {
  notificationsAnchorEl: null,
  notifications: [
    {
      id: 1,
      title: "New message",
      description: "You have a new message from John Doe",
      date: "2 minutes ago",
    },
    {
      id: 2,
      title: "New like",
      description: "Your post has been liked by Jane Doe",
      date: "1 hour ago",
    },
  ],
};

function notificationsMenuReducer(state, action) {
  switch (action.type) {
    case "OPEN_NOTIFICATIONS":
      return { ...state, notificationsAnchorEl: action.payload };
    case "CLOSE_NOTIFICATIONS":
      return { ...state, notificationsAnchorEl: null };
    case "DISMISS_NOTIFICATION":
      return {
        ...state,
        notifications: state.notifications.filter(
          (notification) => notification.id !== action.payload,
        ),
      };
    default:
      return state;
  }
}

export { initialState, notificationsMenuReducer };
