import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./auth/authSlice";
import catalogReducer from "./catalog/catalogSlice";
import bookingReducer from "./booking/bookingSlice";
import creditReducer from "./credit/creditSlice";
import partnerReducer from "./partner/partnerSlice";
import adminReducer from "./admin/adminSlice";
import uiReducer from "./ui/uiSlice";
import { setTokenGetter } from "../utils/api";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    catalog: catalogReducer,
    booking: bookingReducer,
    credit: creditReducer,
    partner: partnerReducer,
    admin: adminReducer,
    ui: uiReducer,
  },
});

setTokenGetter(() => store.getState().auth.jwt);
