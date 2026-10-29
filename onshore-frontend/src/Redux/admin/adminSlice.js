import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { adminService } from "../../services/adminService";
import { creditService } from "../../services/creditService";
import { partnerService } from "../../services/partnerService";
import { payoutService } from "../../services/payoutService";
import { userService } from "../../services/userService";
import { asyncCases, guarded } from "../../utils/asyncCases";

export const fetchDashboard = createAsyncThunk(
  "admin/dashboard",
  guarded(() => adminService.dashboard()),
);
export const fetchUsers = createAsyncThunk(
  "admin/users",
  guarded(() => userService.list()),
);
export const fetchAllPartners = createAsyncThunk(
  "admin/partners",
  guarded(() => partnerService.listPartners({})),
);
export const fetchAllActivities = createAsyncThunk(
  "admin/activities",
  guarded(() => partnerService.listActivities({})),
);
export const setPartnerStatus = createAsyncThunk(
  "admin/partnerStatus",
  guarded(({ id, status }) => partnerService.setStatus(id, status)),
);
export const editPartner = createAsyncThunk(
  "admin/editPartner",
  guarded(async ({ id, body, status }) => {
    const updated = await partnerService.updatePartner(id, body);
    return status ? partnerService.setStatus(id, status) : updated;
  }),
);
export const createPartner = createAsyncThunk(
  "admin/createPartner",
  guarded(async ({ body, active }) => {
    const created = await partnerService.signupPartner(body);
    return active ? partnerService.setStatus(created.id, "ACTIVE") : created;
  }),
);
export const fetchRules = createAsyncThunk(
  "admin/rules",
  guarded(() => adminService.rules()),
);
export const saveRule = createAsyncThunk(
  "admin/saveRule",
  guarded(({ id, body }) =>
    id ? adminService.updateRule(id, body) : adminService.createRule(body),
  ),
);
export const deleteRule = createAsyncThunk(
  "admin/deleteRule",
  guarded(async (id) => {
    await adminService.deleteRule(id);
    return id;
  }),
);
export const fetchSettings = createAsyncThunk(
  "admin/settings",
  guarded(() => adminService.settings()),
);
export const saveSettings = createAsyncThunk(
  "admin/saveSettings",
  guarded((body) => adminService.saveSettings(body)),
);
export const fetchCreditSettings = createAsyncThunk(
  "admin/creditSettings",
  guarded(() => creditService.settings()),
);
export const saveCreditSetting = createAsyncThunk(
  "admin/saveCreditSetting",
  guarded(({ category, body }) => creditService.saveSetting(category, body)),
);
export const fetchAdminPlans = createAsyncThunk(
  "admin/plans",
  guarded(() => creditService.plans()),
);
export const savePlan = createAsyncThunk(
  "admin/savePlan",
  guarded((body) => creditService.savePlan(body)),
);
export const adjustCredits = createAsyncThunk(
  "admin/adjust",
  guarded((body) => creditService.adjust(body)),
);
export const fetchAllPayouts = createAsyncThunk(
  "admin/payouts",
  guarded(() => payoutService.list({})),
);
export const fetchPayoutSummary = createAsyncThunk(
  "admin/payoutSummary",
  guarded(() => payoutService.summary()),
);
export const setPayoutStatus = createAsyncThunk(
  "admin/payoutStatus",
  guarded(({ id, status }) => payoutService.setStatus(id, status)),
);

const upsert = (list, item, key = "id") =>
  list.some((x) => x[key] === item[key])
    ? list.map((x) => (x[key] === item[key] ? item : x))
    : [...list, item];

const adminSlice = createSlice({
  name: "admin",
  initialState: {
    dashboard: null,
    users: [],
    partners: [],
    activities: [],
    rules: [],
    settings: null,
    creditSettings: [],
    plans: [],
    payouts: [],
    payoutSummary: [],
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    asyncCases(builder, fetchDashboard, (state, { payload }) => {
      state.dashboard = payload;
    });
    asyncCases(builder, fetchUsers, (state, { payload }) => {
      state.users = payload;
    });
    asyncCases(builder, fetchAllPartners, (state, { payload }) => {
      state.partners = payload;
    });
    asyncCases(builder, fetchAllActivities, (state, { payload }) => {
      state.activities = payload;
    });
    asyncCases(builder, setPartnerStatus, (state, { payload }) => {
      state.partners = upsert(state.partners, payload);
    });
    asyncCases(builder, createPartner, (state, { payload }) => {
      state.partners = upsert(state.partners, payload);
    });
    asyncCases(builder, editPartner, (state, { payload }) => {
      state.partners = upsert(state.partners, payload);
    });
    asyncCases(builder, fetchRules, (state, { payload }) => {
      state.rules = payload;
    });
    asyncCases(builder, saveRule, (state, { payload }) => {
      state.rules = upsert(state.rules, payload);
    });
    asyncCases(builder, deleteRule, (state, { payload }) => {
      state.rules = state.rules.filter((r) => r.id !== payload);
    });
    asyncCases(builder, fetchSettings, (state, { payload }) => {
      state.settings = payload;
    });
    asyncCases(builder, saveSettings, (state, { payload }) => {
      state.settings = payload;
    });
    asyncCases(builder, fetchCreditSettings, (state, { payload }) => {
      state.creditSettings = payload;
    });
    asyncCases(builder, saveCreditSetting, (state, { payload }) => {
      state.creditSettings = upsert(state.creditSettings, payload, "category");
    });
    asyncCases(builder, fetchAdminPlans, (state, { payload }) => {
      state.plans = payload;
    });
    asyncCases(builder, savePlan, (state, { payload }) => {
      state.plans = upsert(state.plans, payload);
    });
    asyncCases(builder, adjustCredits);
    asyncCases(builder, fetchAllPayouts, (state, { payload }) => {
      state.payouts = payload;
    });
    asyncCases(builder, fetchPayoutSummary, (state, { payload }) => {
      state.payoutSummary = payload;
    });
    asyncCases(builder, setPayoutStatus, (state, { payload }) => {
      state.payouts = upsert(state.payouts, payload);
    });
  },
});

export default adminSlice.reducer;
