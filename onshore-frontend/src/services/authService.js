import { post } from "../utils/api";

export const authService = {
  login: (email, password) => post("/auth/login", { email, password }),
  signup: ({ fullName, email, phone, password, role }) => post("/auth/signup", { fullName, email, phone, password, role }),
};
