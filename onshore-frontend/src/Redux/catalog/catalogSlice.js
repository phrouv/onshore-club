import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { partnerService } from "../../services/partnerService";
import { planningService } from "../../services/planningService";
import { asyncCases, guarded } from "../../utils/asyncCases";

export const fetchPartners = createAsyncThunk("catalog/partners", guarded((params) => partnerService.listPartners(params)));
export const fetchActivities = createAsyncThunk("catalog/activities", guarded((params) => partnerService.listActivities(params)));
export const fetchClasses = createAsyncThunk("catalog/classes", guarded((params) => planningService.list(params)));

const catalogSlice = createSlice({
  name: "catalog",
  initialState: { partners: [], activities: [], classes: [], loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    asyncCases(builder, fetchPartners, (state, { payload }) => {
      state.partners = payload;
    });
    asyncCases(builder, fetchActivities, (state, { payload }) => {
      state.activities = payload;
    });
    asyncCases(builder, fetchClasses, (state, { payload }) => {
      state.classes = payload;
    });
  },
});

export default catalogSlice.reducer;
