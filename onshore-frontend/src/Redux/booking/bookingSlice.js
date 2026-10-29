import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { bookingService } from "../../services/bookingService";
import { asyncCases, guarded } from "../../utils/asyncCases";

export const fetchMyBookings = createAsyncThunk("booking/mine", guarded(() => bookingService.mine()));
export const bookClass = createAsyncThunk("booking/book", guarded((classId) => bookingService.book(classId)));
export const cancelBooking = createAsyncThunk("booking/cancel", guarded((id) => bookingService.cancel(id)));
export const checkInBooking = createAsyncThunk(
  "booking/checkin",
  guarded(({ id, code }) => bookingService.checkIn(id, code)),
);
export const fetchPartnerBookings = createAsyncThunk("booking/partnerList", guarded((params) => bookingService.list(params)));
export const validateBookingCode = createAsyncThunk("booking/validate", guarded((code) => bookingService.validate(code)));

const replace = (list, item) => list.map((b) => (b.id === item.id ? item : b));

const bookingSlice = createSlice({
  name: "booking",
  initialState: { mine: [], managed: [], loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    asyncCases(builder, fetchMyBookings, (state, { payload }) => {
      state.mine = payload;
    });
    asyncCases(builder, bookClass, (state, { payload }) => {
      state.mine.unshift(payload);
    });
    asyncCases(builder, cancelBooking, (state, { payload }) => {
      state.mine = replace(state.mine, payload);
    });
    asyncCases(builder, checkInBooking, (state, { payload }) => {
      state.mine = replace(state.mine, payload);
    });
    asyncCases(builder, fetchPartnerBookings, (state, { payload }) => {
      state.managed = payload;
    });
    asyncCases(builder, validateBookingCode, (state, { payload }) => {
      state.managed = replace(state.managed, payload);
    });
  },
});

export default bookingSlice.reducer;
