import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { partnerService } from "../../services/partnerService";
import { planningService } from "../../services/planningService";
import { payoutService } from "../../services/payoutService";
import { asyncCases, guarded } from "../../utils/asyncCases";

export const fetchMyPartner = createAsyncThunk("partner/mine", async (_, { rejectWithValue }) => {
  try {
    return await partnerService.myPartner();
  } catch (error) {
    // 404 only means "no partner application yet".
    if (error.status === 404) return null;
    return rejectWithValue(error.message);
  }
});
export const applyAsPartner = createAsyncThunk("partner/apply", guarded((body) => partnerService.signupPartner(body)));
export const updatePartner = createAsyncThunk("partner/update", guarded(({ id, body }) => partnerService.updatePartner(id, body)));
export const fetchPartnerActivities = createAsyncThunk("partner/activities", guarded((partnerId) => partnerService.listActivities({ partnerId })));
export const saveActivity = createAsyncThunk(
  "partner/saveActivity",
  guarded(({ id, body }) => (id ? partnerService.updateActivity(id, body) : partnerService.createActivity(body))),
);
export const fetchPartnerClasses = createAsyncThunk("partner/classes", guarded((params) => planningService.list(params)));
export const createClasses = createAsyncThunk("partner/createClasses", guarded((body) => planningService.create(body)));
export const updateClass = createAsyncThunk("partner/updateClass", guarded(({ id, body }) => planningService.update(id, body)));
export const closeClass = createAsyncThunk("partner/closeClass", guarded((id) => planningService.close(id)));
export const reopenClass = createAsyncThunk("partner/reopenClass", guarded((id) => planningService.reopen(id)));
export const deleteClass = createAsyncThunk("partner/deleteClass", guarded(async (id) => {
  await planningService.remove(id);
  return id;
}));
export const fetchPayouts = createAsyncThunk("partner/payouts", guarded((partnerId) => payoutService.list({ partnerId })));
export const fetchMonthly = createAsyncThunk("partner/monthly", guarded((partnerId) => payoutService.monthly(partnerId)));

const upsert = (list, item) => (list.some((x) => x.id === item.id) ? list.map((x) => (x.id === item.id ? item : x)) : [item, ...list]);

const partnerSlice = createSlice({
  name: "partner",
  initialState: {
    partner: null,
    partnerLoaded: false,
    activities: [],
    classes: [],
    payouts: [],
    monthly: [],
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    asyncCases(builder, fetchMyPartner, (state, { payload }) => {
      state.partner = payload;
      state.partnerLoaded = true;
    });
    asyncCases(builder, applyAsPartner, (state, { payload }) => {
      state.partner = payload;
    });
    asyncCases(builder, updatePartner, (state, { payload }) => {
      state.partner = payload;
    });
    asyncCases(builder, fetchPartnerActivities, (state, { payload }) => {
      state.activities = payload;
    });
    asyncCases(builder, saveActivity, (state, { payload }) => {
      state.activities = upsert(state.activities, payload);
    });
    asyncCases(builder, fetchPartnerClasses, (state, { payload }) => {
      state.classes = payload;
    });
    asyncCases(builder, createClasses, (state, { payload }) => {
      state.classes = [...state.classes, ...payload];
    });
    asyncCases(builder, updateClass, (state, { payload }) => {
      state.classes = upsert(state.classes, payload);
    });
    asyncCases(builder, closeClass, (state, { payload }) => {
      state.classes = upsert(state.classes, payload);
    });
    asyncCases(builder, reopenClass, (state, { payload }) => {
      state.classes = upsert(state.classes, payload);
    });
    asyncCases(builder, deleteClass, (state, { payload }) => {
      state.classes = state.classes.filter((c) => c.id !== payload);
    });
    asyncCases(builder, fetchPayouts, (state, { payload }) => {
      state.payouts = payload;
    });
    asyncCases(builder, fetchMonthly, (state, { payload }) => {
      state.monthly = payload;
    });
  },
});

export default partnerSlice.reducer;
