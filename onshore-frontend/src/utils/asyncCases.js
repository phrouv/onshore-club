/**
 * Registers pending/fulfilled/rejected for a thunk: sets the loading flag and error
 * of a slice and delegates the success payload handling.
 */
export function asyncCases(builder, thunk, onFulfilled) {
  builder
    .addCase(thunk.pending, (state) => {
      state.loading = true;
      state.error = null;
    })
    .addCase(thunk.fulfilled, (state, action) => {
      state.loading = false;
      if (onFulfilled) onFulfilled(state, action);
    })
    .addCase(thunk.rejected, (state, action) => {
      state.loading = false;
      state.error = action.payload || action.error.message;
    });
}

/** Wraps an API call so thunks reject with the readable server message. */
export const guarded = (call) => async (arg, { rejectWithValue }) => {
  try {
    return await call(arg);
  } catch (error) {
    return rejectWithValue(error.message);
  }
};
