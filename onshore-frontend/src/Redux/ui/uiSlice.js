import { createSlice } from "@reduxjs/toolkit";

let nextId = 1;

const uiSlice = createSlice({
  name: "ui",
  initialState: { toasts: [] },
  reducers: {
    notify: {
      reducer: (state, action) => {
        state.toasts.push(action.payload);
      },
      prepare: (message, type = "success") => ({ payload: { id: nextId++, message, type } }),
    },
    dismissToast: (state, action) => {
      state.toasts = state.toasts.filter((t) => t.id !== action.payload);
    },
  },
});

export const { notify, dismissToast } = uiSlice.actions;
export default uiSlice.reducer;
