import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { authService } from "../../services/authService";
import { asyncCases, guarded } from "../../utils/asyncCases";

const SESSION_KEY = "onshore.session";

const readSession = () => {
  try {
    return JSON.parse(sessionStorage.getItem(SESSION_KEY)) || { jwt: null, user: null };
  } catch {
    return { jwt: null, user: null };
  }
};

export const login = createAsyncThunk("auth/login", guarded(({ email, password }) => authService.login(email, password)));
export const signup = createAsyncThunk("auth/signup", guarded((payload) => authService.signup(payload)));

export const ROLE_HOME = {
  ROLE_MEMBER: "/member",
  ROLE_PARTNER: "/partner",
  ROLE_SYSTEM_ADMIN: "/admin",
};

const persist = (state, { payload }) => {
  state.jwt = payload.jwt;
  state.user = payload.user;
  sessionStorage.setItem(SESSION_KEY, JSON.stringify({ jwt: payload.jwt, user: payload.user }));
};

const authSlice = createSlice({
  name: "auth",
  initialState: { ...readSession(), loading: false, error: null },
  reducers: {
    logout: (state) => {
      state.jwt = null;
      state.user = null;
      sessionStorage.removeItem(SESSION_KEY);
    },
    clearAuthError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    asyncCases(builder, login, persist);
    asyncCases(builder, signup, persist);
  },
});

export const { logout, clearAuthError } = authSlice.actions;
export default authSlice.reducer;
