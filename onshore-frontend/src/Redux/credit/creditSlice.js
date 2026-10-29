import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { creditService } from "../../services/creditService";
import { asyncCases, guarded } from "../../utils/asyncCases";

export const fetchBalance = createAsyncThunk("credit/balance", guarded(() => creditService.balance()));
export const fetchTransactions = createAsyncThunk("credit/transactions", guarded((memberId) => creditService.transactions(memberId)));
export const fetchPlans = createAsyncThunk("credit/plans", guarded(() => creditService.plans()));
export const fetchSubscription = createAsyncThunk("credit/subscription", async (_, { rejectWithValue }) => {
  try {
    return await creditService.mySubscription();
  } catch (error) {
    // 404 only means "no active subscription"; anything else is a real failure.
    if (error.status === 404) return null;
    return rejectWithValue(error.message);
  }
});
export const purchaseCredits = createAsyncThunk("credit/purchase", guarded((pack) => creditService.purchase(pack)));
export const changePlan = createAsyncThunk("credit/changePlan", guarded((planId) => creditService.changePlan(planId)));
export const cancelPlan = createAsyncThunk("credit/cancelPlan", guarded(() => creditService.cancelPlan()));

const creditSlice = createSlice({
  name: "credit",
  initialState: { balance: 0, transactions: [], plans: [], subscription: null, loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    asyncCases(builder, fetchBalance, (state, { payload }) => {
      state.balance = payload.balance;
    });
    asyncCases(builder, fetchTransactions, (state, { payload }) => {
      state.transactions = payload;
    });
    asyncCases(builder, fetchPlans, (state, { payload }) => {
      state.plans = payload;
    });
    asyncCases(builder, fetchSubscription, (state, { payload }) => {
      state.subscription = payload;
    });
    asyncCases(builder, purchaseCredits, (state, { payload }) => {
      state.balance = payload.balance;
    });
    asyncCases(builder, changePlan, (state, { payload }) => {
      state.subscription = payload;
    });
    asyncCases(builder, cancelPlan, (state, { payload }) => {
      state.subscription = payload;
    });
  },
});

export default creditSlice.reducer;
